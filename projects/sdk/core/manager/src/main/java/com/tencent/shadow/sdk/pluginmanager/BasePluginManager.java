package com.tencent.shadow.sdk.pluginmanager;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;

import com.tencent.shadow.core.host.PluginManager;
import com.tencent.shadow.core.host.ViewCallback;
import com.tencent.shadow.sdk.pluginmanager.installplugin.InstalledDao;
import com.tencent.shadow.sdk.pluginmanager.installplugin.InstalledPlugin;
import com.tencent.shadow.sdk.pluginmanager.installplugin.InstalledPluginDBHelper;
import com.tencent.shadow.sdk.pluginmanager.installplugin.PartInfo;
import com.tencent.shadow.sdk.pluginmanager.installplugin.PluginConfig;
import com.tencent.shadow.sdk.pluginmanager.installplugin.UnpackManager;
import com.tencent.shadow.sdk.pluginmanager.pluginlauncher.PluginLauncher;
import com.tencent.shadow.sdk.service.IPluginLoaderServiceInterface;
import com.tencent.shadow.sdk.service.IProcessServiceInterface;

import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

import static android.content.Context.BIND_AUTO_CREATE;

public abstract class BasePluginManager implements PluginManager {

    public final static String TAG = "BasePluginManager";
    /*
     * 宿主的context对象
     */
    public Context mHostContext;

    /**
     * 用于view对象创建的回调
     */
    private ViewCallback mViewCallback;

    /**
     * 插件进程PluginProcessService的接口
     */
    protected IProcessServiceInterface mIProcessServiceInterface;
    /**
     * 业务类型
     */
    protected String mAppID;

    /**
     * 从压缩包中将插件解压出来，解析成InstalledPlugin
     */
    private UnpackManager mUnpackManager;

    /**
     * 防止绑定service重入
     */
    private AtomicBoolean mServiceConnecting = new AtomicBoolean(false);
    /**
     * 等待service绑定完成的计数器
     */
    private CountDownLatch mConnectCountDownLatch = new CountDownLatch(1);
    /**
     * 插件信息查询数据库接口
     */
    private InstalledDao mInstalledDao;
    /**
     * 插件加载服务端接口
     */
    private IPluginLoaderServiceInterface mPluginLoaderService;
    /**
     * UI线程的handler
     */
    private Handler mHandler = new Handler(Looper.getMainLooper());
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
        Log.d(TAG, "onCreate bundle:" + bundle);
    }

    /**
     * 当PluginManager有更新时会先回调老的PluginManager对象 onSaveInstanceState存储数据
     *
     * @param bundle 要存储的数据
     */
    @Override
    public void onSaveInstanceState(Bundle bundle) {
        Log.d(TAG, "onSaveInstanceState:" + bundle);
    }

    /**
     * 当PluginManager有更新时先会销毁老的PluginManager对象，回调对应的onDestroy
     */
    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy ");
    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mIProcessServiceInterface = IProcessServiceInterface.Stub.asInterface(service);
            mConnectCountDownLatch.countDown();
            Log.d(TAG, "onServiceConnected");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mServiceConnecting.set(false);
            mIProcessServiceInterface = null;
            mPluginLoaderService = null;
        }
    };


    /**
     * 将view对象回调给mViewCallback
     *
     * @param view
     */
    public final void onViewLoaded(View view) {
        if (mViewCallback != null) {
            mViewCallback.onViewCreated(view);
        }
    }

    /**
     * 启动PluginProcessService
     *
     * @param serviceName 注册在宿主中的插件进程管理service完整名字
     */
    public final void startPluginProcessService(final String serviceName) {
        if (mServiceConnecting.get()) {
            Log.d(TAG, "pps service connecting ");
            return;
        }
        mServiceConnecting.set(true);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent();
                intent.setComponent(new ComponentName(mHostContext, serviceName));
                mHostContext.bindService(intent, mServiceConnection, BIND_AUTO_CREATE);
            }
        });
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
     * 加载插件
     *
     * @param partKey         要加载的插件的partkey
     * @param installedPlugin installedPlugin
     */
    public final PluginLauncher loadPlugin(String partKey, InstalledPlugin installedPlugin) throws RemoteException {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            throw new RuntimeException("loadPlugin 不能在主线程中调用");
        }
        if (mIProcessServiceInterface == null) {
            try {
                long s = System.currentTimeMillis();
                mConnectCountDownLatch.await();
                Log.d(TAG, "wait service connect:" + (System.currentTimeMillis() - s));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
        PartInfo partInfo = installedPlugin.getPartInfo(partKey);
        if (installedPlugin.pluginLoaderFile != null) {
            mIProcessServiceInterface.loadRuntime(installedPlugin.UUID, installedPlugin.runtimeFile.getAbsolutePath());
        }
        if (mPluginLoaderService == null) {
            IBinder iBinder = mIProcessServiceInterface.loadPluginLoader(installedPlugin.UUID, installedPlugin.pluginLoaderFile.getAbsolutePath());
            mPluginLoaderService = IPluginLoaderServiceInterface.Stub.asInterface(iBinder);
        }
        mPluginLoaderService.loadPlugin(partKey, partInfo.filePath, partInfo.isInterface);
        return new PluginLauncher(mPluginLoaderService);
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
