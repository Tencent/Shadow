package com.tencent.shadow.dynamic.manager;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;

import com.tencent.shadow.core.interface_.InstalledType;
import com.tencent.shadow.core.interface_.log.ILogger;
import com.tencent.shadow.core.interface_.log.ShadowLoggerFactory;
import com.tencent.shadow.core.pluginmanager.BasePluginManager;
import com.tencent.shadow.core.pluginmanager.installplugin.InstalledPlugin;
import com.tencent.shadow.dynamic.host.InstalledPart;
import com.tencent.shadow.dynamic.host.PpsController;
import com.tencent.shadow.dynamic.host.UuidManager;
import com.tencent.shadow.dynamic.loader.PluginLoader;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static android.content.Context.BIND_AUTO_CREATE;

public abstract class PluginManagerThatUseDynamicLoader extends BasePluginManager {

    private ILogger mLogger = ShadowLoggerFactory.getLogger("shadow::BasePluginManager");


    protected PluginManagerThatUseDynamicLoader(Context context) {
        super(context);
    }

    /**
     * 插件进程PluginProcessService的接口
     */
    protected PpsController mPpsController;

    /**
     * 插件加载服务端接口
     */
    protected PluginLoader mPluginLoader;

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
            mPpsController = PpsController.Stub.asInterface(service);
            try {
                mPpsController.setUuidManager(mUuidManager);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
            mConnectCountDownLatch.countDown();
            if (mLogger.isInfoEnabled()) {
                mLogger.info("onServiceConnected");
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mServiceConnecting.set(false);
            mPpsController = null;
            mPluginLoader = null;
        }
    };

    /**
     * 启动PluginProcessService
     *
     * @param serviceName 注册在宿主中的插件进程管理service完整名字
     */
    public final void startPluginProcessService(final String serviceName) {
        if (mServiceConnecting.get()) {
            if (mLogger.isInfoEnabled()) {
                mLogger.info("pps service connecting");
            }
            return;
        }
        if (mLogger.isInfoEnabled()) {
            mLogger.info("startPluginProcessService "+serviceName);
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


    public final void loadRunTime(String uuid) throws RemoteException{
        if (Looper.myLooper() == Looper.getMainLooper()) {
            throw new RuntimeException("loadPlugin 不能在主线程中调用");
        }
        if (mLogger.isInfoEnabled()) {
            mLogger.info("loadRunTime mPpsController:"+mPpsController);
        }
        if (mPpsController == null) {
            try {
                if (mLogger.isInfoEnabled()) {
                    mLogger.info("waiting service connect");
                }
                long s = System.currentTimeMillis();
                mConnectCountDownLatch.await(4, TimeUnit.SECONDS);
                if (mLogger.isInfoEnabled()) {
                    mLogger.info("service connected " + (System.currentTimeMillis() - s));
                }
            } catch (InterruptedException e) {
                if (mLogger.isErrorEnabled()) {
                    mLogger.error(e);
                }
            }
        }
        mPpsController.loadRuntime(uuid);
    }

    public final void loadPluginLoader(String uuid) throws RemoteException{
        if (mLogger.isInfoEnabled()) {
            mLogger.info("loadRunTime loadPluginLoader:"+mPluginLoader);
        }
        if (mPluginLoader == null) {
            IBinder iBinder = mPpsController.loadPluginLoader(uuid);
            mPluginLoader = PluginLoader.Stub.asInterface(iBinder);
        }
    }


    private UuidManager.Stub mUuidManager = new UuidManager.Stub() {
        @Override
        public InstalledPart getInstalledPL(String uuid, int type) throws RemoteException {
            InstalledPlugin.Part part = getLoaderOrRunTimePart(uuid, type);
            return new InstalledPart(uuid, null, type, part.pluginFile.getAbsolutePath(),
                    part.oDexDir == null ? null : part.oDexDir.getAbsolutePath(),
                    part.libraryDir == null ? null : part.libraryDir.getAbsolutePath(), null);
        }

        @Override
        public InstalledPart getInstalledPlugin(String uuid, String partKey) throws RemoteException {
            InstalledPlugin.Part part = getPluginPartByPartKey(uuid, partKey);
            String[] dependsOn = part instanceof InstalledPlugin.PluginPart ? ((InstalledPlugin.PluginPart) part).dependsOn : null;
            int type = part instanceof InstalledPlugin.PluginPart ? InstalledType.TYPE_PLUGIN : InstalledType.TYPE_INTERFACE;
            return new InstalledPart(uuid, null, type, part.pluginFile.getAbsolutePath(),
                    part.oDexDir == null ? null : part.oDexDir.getAbsolutePath(),
                    part.libraryDir == null ? null : part.libraryDir.getAbsolutePath(), dependsOn);
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPpsController != null) {
            try {
                mPpsController.setUuidManager(null);
            } catch (RemoteException e) {
                if (mLogger.isErrorEnabled()) {
                    mLogger.error(e);
                }
            }
        }
        mPpsController = null;
    }
}
