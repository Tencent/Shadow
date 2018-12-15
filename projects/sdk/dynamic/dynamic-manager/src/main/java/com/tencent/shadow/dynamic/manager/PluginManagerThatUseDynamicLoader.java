package com.tencent.shadow.dynamic.manager;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;

import com.tencent.shadow.core.interface_.log.ILogger;
import com.tencent.shadow.core.interface_.log.ShadowLoggerFactory;
import com.tencent.shadow.core.pluginmanager.BasePluginManager;
import com.tencent.shadow.core.pluginmanager.installplugin.InstalledPlugin;
import com.tencent.shadow.dynamic.host.InstalledPL;
import com.tencent.shadow.dynamic.host.PpsController;
import com.tencent.shadow.dynamic.host.UuidManager;
import com.tencent.shadow.dynamic.loader.PluginLoader;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

import static android.content.Context.BIND_AUTO_CREATE;

public abstract class PluginManagerThatUseDynamicLoader extends BasePluginManager {

    private ILogger mLogger = ShadowLoggerFactory.getLogger("BasePluginManager");


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
    private PluginLoader mPluginLoader;

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
    public final LoadedPlugin loadPlugin(String partKey, InstalledPlugin installedPlugin) throws RemoteException {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            throw new RuntimeException("loadPlugin 不能在主线程中调用");
        }
        if (mPpsController == null) {
            try {
                long s = System.currentTimeMillis();
                mConnectCountDownLatch.await();
                if (mLogger.isInfoEnabled()) {
                    mLogger.info("wait service connect:" + (System.currentTimeMillis() - s));
                }
            } catch (InterruptedException e) {
                if (mLogger.isErrorEnabled()) {
                    mLogger.error(e);
                }
            }
        }
        if (installedPlugin.pluginLoaderFile != null) {
            mPpsController.loadRuntime(installedPlugin.UUID);
        }
        if (mPluginLoader == null) {
            IBinder iBinder = mPpsController.loadPluginLoader(installedPlugin.UUID);
            mPluginLoader = PluginLoader.Stub.asInterface(iBinder);
        }

        boolean hasPart = installedPlugin.hasPart(partKey);
        if (!hasPart) {
            throw new RemoteException("在" + installedPlugin + "中找不到partKey==" + partKey);
        } else {
            com.tencent.shadow.core.loader.infos.InstalledPlugin loaderInstalledPlugin;

            if (installedPlugin.isInterface(partKey)) {
                InstalledPlugin.Part interfacePart = installedPlugin.getInterface(partKey);
                loaderInstalledPlugin = new com.tencent.shadow.core.loader.infos.InstalledPlugin(
                        interfacePart.pluginFile,
                        1,
                        partKey,
                        Long.toString(interfacePart.pluginFile.lastModified()),
                        null,
                        interfacePart.oDexDir,
                        interfacePart.libraryDir
                );
            } else {
                InstalledPlugin.PluginPart pluginPart = installedPlugin.getPlugin(partKey);
                loaderInstalledPlugin = new com.tencent.shadow.core.loader.infos.InstalledPlugin(
                        pluginPart.pluginFile,
                        0,
                        partKey,
                        Long.toString(pluginPart.pluginFile.lastModified()),
                        pluginPart.dependsOn,
                        pluginPart.oDexDir,
                        pluginPart.libraryDir
                );
            }
            mPluginLoader.loadPlugin(loaderInstalledPlugin);
            return new LoadedPlugin(mPluginLoader);
        }
    }

    private UuidManager.Stub mUuidManager = new UuidManager.Stub() {
        @Override
        public InstalledPL getInstalledPL(String uuid, int type) throws RemoteException {
            InstalledPlugin.Part part = getLoaderOrRunTimePart(uuid,type);
            return new InstalledPL(uuid,part.pluginFile.getAbsolutePath(),
                    part.oDexDir == null ? null : part.oDexDir.getAbsolutePath(),
                    part.libraryDir == null ? null : part.libraryDir.getAbsolutePath());
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            mPpsController.setUuidManager(null);
        } catch (RemoteException e) {
            if (mLogger.isErrorEnabled()) {
                mLogger.error(e);
            }
        }
    }
}
