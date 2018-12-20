package com.tencent.shadow.dynamic.host;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

import com.tencent.shadow.core.interface_.log.ILogger;
import com.tencent.shadow.core.interface_.log.ShadowLoggerFactory;

import java.io.File;


public class PluginProcessService extends Service {
    private ILogger mLogger = ShadowLoggerFactory.getLogger("shadow::PluginProcessService");

    private final PpsController.Stub mPpsController = new PpsControllerImpl();

    @Override
    public void onCreate() {
        super.onCreate();
        if (mLogger.isInfoEnabled()) {
            mLogger.info("onCreate:" + this);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        if (mLogger.isInfoEnabled()) {
            mLogger.info("onBind:" + this);
        }
        return mPpsController;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        if (mLogger.isInfoEnabled()) {
            mLogger.info("onUnbind:" + this);
        }
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
        if (mLogger.isInfoEnabled()) {
            mLogger.info("onRebind:" + this);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mLogger.isInfoEnabled()) {
            mLogger.info("onDestroy:" + this);
        }
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        if (mLogger.isInfoEnabled()) {
            mLogger.info("onTaskRemoved:" + this);
        }
    }

    private class PpsControllerImpl extends PpsController.Stub {

        private UuidManager mUuidManager;

        /**
         * 加载{@link #sDynamicPluginLoaderClassName}时
         * 需要从宿主PathClassLoader（含双亲委派）中加载的类
         */
        private final String[] sInterfaces = new String[]{
                //当runtime是动态加载的时候，runtime的ClassLoader是PathClassLoader的parent，
                // 所以不需要写在这个白名单里。但是写在这里不影响，也可以兼容runtime打包在宿主的情况。
                "com.tencent.shadow.runtime.container",
                "com.tencent.shadow.dynamic.host",
                "com.tencent.shadow.core.interface_",
                "com.tencent.shadow.core.interface_.log",
        };

        private final static String sDynamicPluginLoaderClassName
                = "com.tencent.shadow.dynamic.loader.DynamicPluginLoader";

        private final static int TYPE_PLUGIN_LOADER = 3;//todo 删除临时重复定义枚举

        private final static int TYPE_PLUGIN_RUNTIME = 4;//todo 删除临时重复定义枚举

        private IBinder mPluginLoader;

        @Override
        public void loadRuntime(String uuid) throws RemoteException {
            if (mLogger.isInfoEnabled()) {
                mLogger.info("loadRuntime uuid:" + uuid);
            }
            InstalledPart installedPart = getInstalledPL(uuid, TYPE_PLUGIN_RUNTIME);
            RunTimeInfo runTimeInfo = new RunTimeInfo(installedPart.filePath, installedPart.oDexPath, installedPart.libraryPath);
            boolean loaded = RunTimeLoader.loadRunTime(runTimeInfo);
            if (loaded) {
                RunTimeLoader.saveLastRunTimeInfo(PluginProcessService.this, runTimeInfo);
            }

        }

        @Override
        public IBinder loadPluginLoader(String uuid) throws RemoteException {
            if (mLogger.isInfoEnabled()) {
                mLogger.info("loadPluginLoader uuid:" + uuid + " loader:" + mPluginLoader);
            }
            if (mPluginLoader == null) {
                InstalledPart installedPart = getInstalledPL(uuid, TYPE_PLUGIN_LOADER);
                File file = new File(installedPart.filePath);
                if (!file.exists()) {
                    throw new RuntimeException(file.getAbsolutePath() + "文件不存在");
                }
                ApkClassLoader pluginLoaderClassLoader = new ApkClassLoader(
                        installedPart.filePath,
                        installedPart.oDexPath,
                        installedPart.libraryPath,
                        this.getClass().getClassLoader(),
                        sInterfaces,
                        1
                );
                try {
                    mPluginLoader = pluginLoaderClassLoader.getInterface(
                            IBinder.class,
                            sDynamicPluginLoaderClassName,
                            new Class[]{Context.class, UuidManager.class, String.class},
                            new Object[]{PluginProcessService.this.getApplicationContext(), mUuidManager, uuid}
                    );
                } catch (Exception e) {
                    throw new RuntimeException(
                            pluginLoaderClassLoader + " : " + sDynamicPluginLoaderClassName + " 创建失败 ",
                            e
                    );
                }
            }
            return mPluginLoader;
        }

        @Override
        public void setUuidManager(UuidManager uuidManager) throws RemoteException {
            if (mLogger.isInfoEnabled()) {
                mLogger.info("setUuidManager ");
            }
            mUuidManager = uuidManager;
        }

        private InstalledPart getInstalledPL(String uuid, int type) throws RemoteException {
            return mUuidManager.getInstalledPL(uuid, type);
        }
    }

}
