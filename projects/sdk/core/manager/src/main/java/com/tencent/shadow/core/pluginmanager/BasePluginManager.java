package com.tencent.shadow.core.pluginmanager;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import com.tencent.shadow.core.interface_.PluginManager;
import com.tencent.shadow.core.interface_.ViewCallback;
import com.tencent.shadow.core.interface_.log.ILogger;
import com.tencent.shadow.core.interface_.log.ShadowLoggerFactory;
import com.tencent.shadow.core.pluginmanager.installplugin.InstalledDao;
import com.tencent.shadow.core.pluginmanager.installplugin.InstalledPlugin;
import com.tencent.shadow.core.pluginmanager.installplugin.InstalledPluginDBHelper;
import com.tencent.shadow.core.pluginmanager.installplugin.PluginConfig;
import com.tencent.shadow.core.pluginmanager.installplugin.UnpackManager;

import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.util.List;

public abstract class BasePluginManager implements PluginManager {

    public final static String TAG = "BasePluginManager";

    private ILogger mLogger = ShadowLoggerFactory.getLogger(TAG);
    /*
     * 宿主的context对象
     */
    public Context mHostContext;

    /**
     * 用于view对象创建的回调
     */
    private ViewCallback mViewCallback;

    /**
     * 业务类型
     */
    protected String mAppID;

    /**
     * 来源id
     */
    protected long mFromId;

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
     * PluginManager的apk路径
     */
    protected String mApkPath;


    public BasePluginManager(String appId, Context context, ViewCallback viewCallback, String apkPath) {
        this.mHostContext = context.getApplicationContext();
        this.mViewCallback = viewCallback;
        this.mAppID = appId;
        this.mUnpackManager = new UnpackManager(mHostContext.getFilesDir());
        this.mInstalledDao = new InstalledDao(InstalledPluginDBHelper.getInstance(mHostContext), mAppID);
        this.mApkPath = apkPath;
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
    public void enter(Context context, long fromId, Bundle bundle) {
        mFromId = fromId;
    }

    /**
     * 将view对象回调给mViewCallback
     *
     * @param view
     */
    public final void onViewLoaded(View view) {
        if (mViewCallback != null) {
            mViewCallback.onViewCreated(mFromId, view);
        }
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
        return mInstalledDao.insert(pluginConfig);
    }

    /**
     * odex优化
     *
     * @param uuid    插件包的uuid
     * @param partKey 要odex的插件partkey
     */
    public final void odexPlugin(String uuid, String partKey) {
        throw new UnsupportedOperationException("TODO");
    }

    /**
     * 插件apk的so解压
     *
     * @param uuid    插件包的uuid
     * @param partKey 要解压so的插件partkey
     */
    public final void extractSo(String uuid, String partKey) {
        throw new UnsupportedOperationException("TODO");
    }


    /**
     * 获取已安装的插件，最后安装的排在返回List的最前面
     *
     * @param limit 最多获取个数
     */
    public final List<InstalledPlugin> getInstalledPlugins(int limit) {
        return mInstalledDao.getLastPlugins(mAppID, limit);
    }
}
