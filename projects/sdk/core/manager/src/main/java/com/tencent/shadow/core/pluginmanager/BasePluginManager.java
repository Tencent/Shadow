package com.tencent.shadow.core.pluginmanager;

import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.tencent.shadow.core.interface_.EnterCallback;
import com.tencent.shadow.core.interface_.PluginManager;
import com.tencent.shadow.core.interface_.log.ILogger;
import com.tencent.shadow.core.interface_.log.ShadowLoggerFactory;
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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public abstract class BasePluginManager implements PluginManager {

    public final static String TAG = "BasePluginManager";

    private ILogger mLogger = ShadowLoggerFactory.getLogger(TAG);
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

    /**
     * 记录安装过的插件
     */
    private ConcurrentHashMap<String, List<InstalledPlugin>> mInstallPlugins = new ConcurrentHashMap<>();


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
     * PluginManager对象创建的时候回调
     *
     * @param bundle 当PluginManager有更新时会回调老的PluginManager对象onSaveInstanceState存储数据，bundle不为null说明发生了更新
     *               为null说明是首次创建
     */
    @Override
    public void onCreate(Bundle bundle) {
        if (mLogger.isInfoEnabled()) {
            mLogger.info("onCreate bundle:" + bundle);
        }
    }

    /**
     * 当PluginManager有更新时会先回调老的PluginManager对象 onSaveInstanceState存储数据
     *
     * @param bundle 要存储的数据
     */
    @Override
    public void onSaveInstanceState(Bundle bundle) {
        if (mLogger.isInfoEnabled()) {
            mLogger.info("onSaveInstanceState:" + bundle);
        }
    }

    /**
     * 当PluginManager有更新时先会销毁老的PluginManager对象，回调对应的onDestroy
     */
    @Override
    public void onDestroy() {
        if (mLogger.isInfoEnabled()) {
            mLogger.info("onDestroy:");
        }
        mInstallPlugins.clear();
    }


    @Override
    public void enter(Context context, long fromId, Bundle bundle, EnterCallback callback) {

    }

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

        List<InstalledPlugin> plugins = null;
        if (mInstallPlugins.get(installedPlugin.UUID) == null) {
            plugins = new ArrayList<>();
        } else {
            plugins = mInstallPlugins.get(installedPlugin.UUID);
        }
        plugins.add(installedPlugin);
        mInstallPlugins.put(installedPlugin.UUID, plugins);
        return installedPlugin;
    }

    protected InstalledPlugin.Part getPluginPartByPartKey(String uuid, String partKey) {
        if (mInstallPlugins.get(uuid) != null) {
            List<InstalledPlugin> plugins = mInstallPlugins.get(uuid);
            for (InstalledPlugin installedPlugin : plugins) {
                if (installedPlugin.getPart(partKey) != null) {
                    return installedPlugin.getPart(partKey);
                }
            }
        } else {
            InstalledPlugin installedPlugin = mInstalledDao.getInstalledPluginByUUID(uuid);
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
        if (mInstallPlugins.get(uuid) != null) {
            List<InstalledPlugin> plugins = mInstallPlugins.get(uuid);
            for (InstalledPlugin installedPlugin : plugins) {
                if (type == InstalledType.TYPE_PLUGIN_RUNTIME) {
                    if (installedPlugin.runtimeFile != null) {
                        return installedPlugin.runtimeFile;
                    }
                } else if (type == InstalledType.TYPE_PLUGIN_LOADER) {
                    if (installedPlugin.pluginLoaderFile != null) {
                        return installedPlugin.pluginLoaderFile;
                    }
                }
            }
        } else {
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
     * 业务插件的abi
     *
     * @return
     */
    public String getAbi() {
        return null;
    }
}
