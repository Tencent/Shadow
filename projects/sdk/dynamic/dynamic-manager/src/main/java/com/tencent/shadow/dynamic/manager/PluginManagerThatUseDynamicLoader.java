package com.tencent.shadow.dynamic.manager;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.util.Log;

import com.tencent.shadow.core.host.ViewCallback;
import com.tencent.shadow.core.pluginmanager.BasePluginManager;
import com.tencent.shadow.core.pluginmanager.installplugin.InstalledPlugin;
import com.tencent.shadow.core.pluginmanager.installplugin.PartInfo;
import com.tencent.shadow.dynamic.host.IProcessServiceInterface;
import com.tencent.shadow.dynamic.loader.IPluginLoaderServiceInterface;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

import static android.content.Context.BIND_AUTO_CREATE;

public class PluginManagerThatUseDynamicLoader extends BasePluginManager {

    public PluginManagerThatUseDynamicLoader(String appId, Context context, ViewCallback viewCallback, String apkPath) {
        super(appId, context, viewCallback, apkPath);
    }

    /**
     * 插件进程PluginProcessService的接口
     */
    protected IProcessServiceInterface mIProcessServiceInterface;

    /**
     * 插件加载服务端接口
     */
    private IPluginLoaderServiceInterface mPluginLoaderService;

    /**
     * 防止绑定service重入
     */
    private AtomicBoolean mServiceConnecting = new AtomicBoolean(false);
    /**
     * 等待service绑定完成的计数器
     */
    private CountDownLatch mConnectCountDownLatch = new CountDownLatch(1);

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
            mIProcessServiceInterface.loadRuntime(installedPlugin.UUID, installedPlugin.runtimeFile.file.getAbsolutePath());
        }
        if (mPluginLoaderService == null) {
            IBinder iBinder = mIProcessServiceInterface.loadPluginLoader(installedPlugin.UUID, installedPlugin.pluginLoaderFile.file.getAbsolutePath());
            mPluginLoaderService = IPluginLoaderServiceInterface.Stub.asInterface(iBinder);
        }
        mPluginLoaderService.loadPlugin(partKey, partInfo.filePath, partInfo.isInterface);
        return new PluginLauncher(mPluginLoaderService);
    }
}
