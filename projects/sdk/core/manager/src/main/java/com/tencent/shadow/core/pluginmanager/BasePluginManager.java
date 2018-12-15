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
import com.tencent.shadow.core.pluginmanager.installplugin.CopySoBloc;
import com.tencent.shadow.core.pluginmanager.installplugin.InstallPluginException;
import com.tencent.shadow.core.pluginmanager.installplugin.InstalledDao;
import com.tencent.shadow.core.pluginmanager.installplugin.InstalledPlugin;
import com.tencent.shadow.core.pluginmanager.installplugin.InstalledPluginDBHelper;
import com.tencent.shadow.core.pluginmanager.installplugin.ODexBloc;
import com.tencent.shadow.core.pluginmanager.installplugin.PluginConfig;
import com.tencent.shadow.core.pluginmanager.installplugin.UnpackManager;

import org.json.JSONException;

import java.io.File;
import java.io.IOException;
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
     * 业务类型
     */
    protected String mAppID;

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
    private ConcurrentHashMap<String, InstalledPlugin> mInstallPlugins = new ConcurrentHashMap<>();


    public BasePluginManager(String appId, Context context) {
        this.mHostContext = context.getApplicationContext();
        this.mAppID = appId;
        this.mUnpackManager = new UnpackManager(mHostContext.getFilesDir());
        this.mInstalledDao = new InstalledDao(InstalledPluginDBHelper.getInstance(mHostContext), mAppID);
    }

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
        PluginConfig pluginConfig = mUnpackManager.unpackPlugin(mAppID, hash, zip);
        InstalledPlugin installedPlugin = mInstalledDao.insert(pluginConfig);

        mInstallPlugins.put(installedPlugin.UUID, installedPlugin);
        return installedPlugin;
    }

    /**
     * odex优化
     *
     * @param uuid    插件包的uuid
     * @param partKey 要oDex的插件partkey
     */
    public final void oDexPlugin(String uuid, String partKey) throws InstallPluginException {
        InstalledPlugin installedPlugin = mInstalledDao.getInstalledPluginByUUID(mAppID, uuid);
        InstalledPlugin.Part part = installedPlugin.getPart(partKey);
        try {
            File oDexDir = ODexBloc.oDexPlugin(mUnpackManager.getAppDir(mAppID), part.pluginFile, uuid, partKey);

            ContentValues values = new ContentValues();
            values.put(InstalledPluginDBHelper.COLUMN_PLUGIN_ODEX, oDexDir.getAbsolutePath());
            mInstalledDao.updatePlugin(mAppID, uuid, partKey, values);
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
        InstalledPlugin installedPlugin = mInstalledDao.getInstalledPluginByUUID(mAppID, uuid);
        InstalledPlugin.Part part = installedPlugin.getPart(partKey);
        try {
            File soDir = CopySoBloc.copySo(mUnpackManager.getAppDir(mAppID), part.pluginFile, uuid, partKey, getAbi());

            ContentValues values = new ContentValues();
            values.put(InstalledPluginDBHelper.COLUMN_PLUGIN_LIB, soDir.getAbsolutePath());
            mInstalledDao.updatePlugin(mAppID, uuid, partKey, values);
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
        return mInstalledDao.getLastPlugins(mAppID, limit);
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
