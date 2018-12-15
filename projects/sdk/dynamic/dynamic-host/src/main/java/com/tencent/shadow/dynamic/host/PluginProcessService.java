package com.tencent.shadow.dynamic.host;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.tencent.shadow.core.interface_.InstalledType;

import java.io.File;


public class PluginProcessService extends Service {

    private final static String TAG = "PluginProcessService";

    private final PpsController.Stub mPpsController = new PpsControllerImpl();

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind");
        return mPpsController;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "onUnbind");
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
        Log.d(TAG, "onRebind");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
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
                "com.tencent.shadow.core.interface_.log",
        };

        private final static String sDynamicPluginLoaderClassName
                = "com.tencent.shadow.dynamic.loader.DynamicPluginLoader";

        private IBinder mPluginLoader;

        @Override
        public void loadRuntime(String uuid) throws RemoteException {
            InstalledPL installedPL = getInstalledPL(uuid, InstalledType.TYPE_PLUGIN_RUNTIME);
            RunTimeLoader.loadRunTime(installedPL);
        }

        @Override
        public IBinder loadPluginLoader(String uuid) throws RemoteException {
            if (mPluginLoader == null) {
                InstalledPL installedPL = getInstalledPL(uuid,InstalledType.TYPE_PLUGIN_LOADER);
                File file = new File(installedPL.filePath);
                if (!file.exists()) {
                    throw new RuntimeException(file.getAbsolutePath() + "文件不存在");
                }
                ApkClassLoader pluginLoaderClassLoader = new ApkClassLoader(
                        installedPL.filePath,
                        installedPL.oDexPath,
                        installedPL.libraryPath,
                        this.getClass().getClassLoader(),
                        sInterfaces
                );
                try {
                    mPluginLoader = pluginLoaderClassLoader.getInterface(
                            IBinder.class,
                            sDynamicPluginLoaderClassName,
                            new Class[]{Context.class},
                            new Object[]{PluginProcessService.this.getApplicationContext()}
                    );
                } catch (Exception e) {
                    throw new RuntimeException(
                            pluginLoaderClassLoader + " 没有找到：" + sDynamicPluginLoaderClassName,
                            e
                    );
                }
            }
            return mPluginLoader;
        }

        @Override
        public void setUuidManager(UuidManager uuidManager) throws RemoteException {
            if (mUuidManager != null) {
                throw new IllegalStateException("不能重复设置");
            }
            mUuidManager = uuidManager;
        }

        private InstalledPL getInstalledPL(String uuid,int type) throws RemoteException {
            return mUuidManager.getInstalledPL(uuid, type);
        }
    }
}
