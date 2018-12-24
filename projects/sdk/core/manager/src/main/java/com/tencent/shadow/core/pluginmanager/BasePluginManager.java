package com.tencent.shadow.core.pluginmanager;

import android.content.ContentValues;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.tencent.shadow.core.common.Logger;
import com.tencent.shadow.core.common.LoggerFactory;
import com.tencent.shadow.core.pluginmanager.installplugin.AppCacheFolderManager;
import com.tencent.shadow.core.pluginmanager.installplugin.CopySoBloc;
import com.tencent.shadow.core.pluginmanager.installplugin.InstallPluginException;
import com.tencent.shadow.core.pluginmanager.installplugin.InstalledDao;
import com.tencent.shadow.core.pluginmanager.installplugin.InstalledPlugin;
import com.tencent.shadow.core.pluginmanager.installplugin.InstalledPluginDBHelper;
import com.tencent.shadow.core.pluginmanager.installplugin.InstalledType;
import com.tencent.shadow.core.pluginmanager.installplugin.ODexBloc;
import com.tencent.shadow.core.pluginmanager.installplugin.PluginConfig;
import com.tencent.shadow.core.pluginmanager.installplugin.UnpackManager;

import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public abstract class BasePluginManager {
    private static final Logger mLogger = LoggerFactory.getLogger(BasePluginManager.class);
    /*
     * 宿主的context对象
     */
    public Context mHostContext;

    /**
     * 从压缩包中将插件解压出来，解析成InstalledPlugin
     */
    private UnpackManager mUnpackManager;

    /**
     * 插件信息查询数据库接口
     */
    private InstalledDao mInstalledDao;

    /**
     * UI线程的handler
     */
    protected Handler mHandler = new Handler(Looper.getMainLooper());


    public BasePluginManager(Context context) {
        this.mHostContext = context.getApplicationContext();
        this.mUnpackManager = new UnpackManager(mHostContext.getFilesDir(), getName());
        this.mInstalledDao = new InstalledDao(new InstalledPluginDBHelper(mHostContext, getName()));
    }

    /**
     * PluginManager的名字
     * 用于和其他PluginManager区分持续化存储的名字
     */
    abstract protected String getName();

    /**
     * 从文件夹中解压插件
     *
     * @param dir 文件夹路径
     * @return InstalledPlugin
     */
    public final InstalledPlugin installPluginFromDir(File dir) {
        throw new UnsupportedOperationException("TODO");
    }

    /**
     * 从压缩包中解压插件
     *
     * @param zip  压缩包路径
     * @param hash 压缩包hash
     * @return InstalledPlugin
     */
    public final InstalledPlugin installPluginFromZip(File zip, String hash) throws IOException, JSONException {
        PluginConfig pluginConfig = mUnpackManager.unpackPlugin(hash, zip);
        InstalledPlugin installedPlugin = mInstalledDao.insert(pluginConfig);

        return installedPlugin;
    }

    protected InstalledPlugin.Part getPluginPartByPartKey(String uuid, String partKey) {
        InstalledPlugin installedPlugin = mInstalledDao.getInstalledPluginByUUID(uuid);
        if (installedPlugin != null) {
            return installedPlugin.getPart(partKey);
        }
        throw new RuntimeException("没有找到Part partKey:" + partKey);
    }

    protected InstalledPlugin getInstalledPlugin(String uuid) {
        return mInstalledDao.getInstalledPluginByUUID(uuid);
    }

    protected InstalledPlugin.Part getLoaderOrRunTimePart(String uuid, int type) {
        if (type != InstalledType.TYPE_PLUGIN_LOADER && type != InstalledType.TYPE_PLUGIN_RUNTIME) {
            throw new RuntimeException("不支持的type:" + type);
        }
        InstalledPlugin installedPlugin = mInstalledDao.getInstalledPluginByUUID(uuid);
        if (type == InstalledType.TYPE_PLUGIN_RUNTIME) {
            if (installedPlugin.runtimeFile != null) {
                return installedPlugin.runtimeFile;
            }
        } else if (type == InstalledType.TYPE_PLUGIN_LOADER) {
            if (installedPlugin.pluginLoaderFile != null) {
                return installedPlugin.pluginLoaderFile;
            }
        }
        throw new RuntimeException("没有找到Part type :" + type);
    }

    /**
     * odex优化
     *
     * @param uuid    插件包的uuid
     * @param partKey 要oDex的插件partkey
     */
    public final void oDexPlugin(String uuid, String partKey) throws InstallPluginException {
        InstalledPlugin.Part part = getPluginPartByPartKey(uuid, partKey);
        try {
            File root = mUnpackManager.getAppDir();
            File oDexDir = AppCacheFolderManager.getODexDir(root, uuid);
            File oDexPath = ODexBloc.oDexPlugin(part.pluginFile, oDexDir, AppCacheFolderManager.getODexCopiedFile(oDexDir, partKey));

            ContentValues values = new ContentValues();
            values.put(InstalledPluginDBHelper.COLUMN_PLUGIN_ODEX, oDexPath.getAbsolutePath());
            mInstalledDao.updatePlugin(uuid, partKey, values);

            part.oDexDir = oDexPath;
        } catch (InstallPluginException e) {
            throw e;
        }
    }


    /**
     * odex优化
     *
     * @param uuid 插件包的uuid
     * @param type 要oDex的插件类型 @class IntalledType  loader or runtime
     */
    public final void oDexPluginLoaderOrRunTime(String uuid, int type) throws InstallPluginException {
        InstalledPlugin.Part part = getLoaderOrRunTimePart(uuid, type);
        try {
            File root = mUnpackManager.getAppDir();
            File oDexDir = AppCacheFolderManager.getODexDir(root, uuid);
            String key = type == InstalledType.TYPE_PLUGIN_LOADER ? "loader" : "runtime";
            File oDexPath = ODexBloc.oDexPlugin(part.pluginFile, oDexDir, AppCacheFolderManager.getODexCopiedFile(oDexDir, key));

            ContentValues values = new ContentValues();
            values.put(InstalledPluginDBHelper.COLUMN_PLUGIN_ODEX, oDexPath.getAbsolutePath());
            mInstalledDao.updatePlugin(uuid, type, values);

            part.oDexDir = oDexPath;
        } catch (InstallPluginException e) {
            throw e;
        }
    }


    /**
     * 插件apk的so解压
     *
     * @param uuid    插件包的uuid
     * @param partKey 要解压so的插件partkey
     */
    public final void extractSo(String uuid, String partKey) throws InstallPluginException {
        InstalledPlugin.Part part = getPluginPartByPartKey(uuid, partKey);
        try {
            File root = mUnpackManager.getAppDir();
            String filter = "lib/" + getAbi() + "/";
            File soDir = AppCacheFolderManager.getLibDir(root, uuid);
            File soPath = CopySoBloc.copySo(part.pluginFile, soDir
                    , AppCacheFolderManager.getLibCopiedFile(soDir, partKey), filter);

            ContentValues values = new ContentValues();
            values.put(InstalledPluginDBHelper.COLUMN_PLUGIN_LIB, soPath.getAbsolutePath());
            mInstalledDao.updatePlugin(uuid, partKey, values);

            part.libraryDir = soPath;
        } catch (InstallPluginException e) {
            throw e;
        }
    }

    /**
     * 插件apk的so解压
     *
     * @param uuid 插件包的uuid
     * @param type 要oDex的插件类型 @class IntalledType  loader or runtime
     */
    public final void extractLoaderOrRunTimeSo(String uuid, int type) throws InstallPluginException {
        InstalledPlugin.Part part = getLoaderOrRunTimePart(uuid, type);
        try {
            File root = mUnpackManager.getAppDir();
            String key = type == InstalledType.TYPE_PLUGIN_LOADER ? "loader" : "runtime";
            String filter = "lib/" + getAbi() + "/";
            File soDir = AppCacheFolderManager.getLibDir(root, uuid);
            File soPath = CopySoBloc.copySo(part.pluginFile, soDir
                    , AppCacheFolderManager.getLibCopiedFile(soDir, key), filter);

            ContentValues values = new ContentValues();
            values.put(InstalledPluginDBHelper.COLUMN_PLUGIN_LIB, soPath.getAbsolutePath());
            mInstalledDao.updatePlugin(uuid, type, values);

            part.libraryDir = soPath;
        } catch (InstallPluginException e) {
            throw e;
        }
    }


    /**
     * 获取已安装的插件，最后安装的排在返回List的最前面
     *
     * @param limit 最多获取个数
     */
    public final List<InstalledPlugin> getInstalledPlugins(int limit) {
        return mInstalledDao.getLastPlugins(limit);
    }


    /**
     * 删除指定uuid的插件
     *
     * @param uuid 插件包的uuid
     * @return 是否全部执行成功
     */
    public boolean deleteInstalledPlugin(String uuid) {
        InstalledPlugin installedPlugin = mInstalledDao.getInstalledPluginByUUID(uuid);
        boolean suc = true;
        if (installedPlugin.runtimeFile != null) {
            if (!deletePart(installedPlugin.runtimeFile)) {
                suc = false;
            }
        }
        if (installedPlugin.pluginLoaderFile != null) {
            if (!deletePart(installedPlugin.pluginLoaderFile)) {
                suc = false;
            }
        }
        for (Map.Entry<String, InstalledPlugin.PluginPart> plugin : installedPlugin.plugins.entrySet()) {
            if (!deletePart(plugin.getValue())) {
                suc = false;
            }
        }
        for (Map.Entry<String, InstalledPlugin.Part> interfacePlugin : installedPlugin.interfaces.entrySet()) {
            if (!deletePart(interfacePlugin.getValue())) {
                suc = false;
            }
        }
        if (mInstalledDao.deleteByUUID(uuid) <= 0) {
            suc = false;
        }
        return suc;
    }

    private boolean deletePart(InstalledPlugin.Part part) {
        boolean suc = true;
        if (!part.pluginFile.delete()) {
            suc = false;
        }
        if (part.oDexDir != null) {
            if (!part.oDexDir.delete()) {
                suc = false;
            }
        }
        if (part.libraryDir != null) {
            if (!part.libraryDir.delete()) {
                suc = false;
            }
        }
        return suc;
    }


    /**
     * 业务插件的abi
     *
     * @return
     */
    public String getAbi() {
        return null;
    }
}
