package com.tencent.shadow.dynamic.host;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.tencent.shadow.runtime.container.DelegateProvider;
import com.tencent.shadow.runtime.container.DelegateProviderHolder;

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
        /**
         * 加载{@link #sDynamicPluginLoaderClassName}时
         * 需要从宿主PathClassLoader（含双亲委派）中加载的类
         */
        private final String[] sInterfaces = new String[]{
                //当runtime是动态加载的时候，runtime的ClassLoader是PathClassLoader的parent，
                // 所以不需要写在这个白名单里。但是写在这里不影响，也可以兼容runtime打包在宿主的情况。
                "com.tencent.shadow.runtime.container",
                "com.tencent.shadow.dynamic.host",
        };

        private final static String sDynamicPluginLoaderClassName
                = "com.tencent.shadow.dynamic.loader.DynamicPluginLoader";

        private IBinder mPluginLoader;

        @Override
        public void loadRuntime(String uuid, String apkPath) throws RemoteException {
            RunTimeLoader.loadRunTime(uuid, apkPath);
        }

        @Override
        public IBinder loadPluginLoader(String uuid, String apkPath) throws RemoteException {
            if (mPluginLoader == null) {
                File file = new File(apkPath);
                if (!file.exists()) {
                    throw new RuntimeException(file.getAbsolutePath() + "文件不存在");
                }
                File odexDir = new File(file.getParent(), "plugin_loader_odex_" + uuid);
                odexDir.mkdirs();
                ApkClassLoader pluginLoaderClassLoader = new ApkClassLoader(
                        apkPath,
                        odexDir.getAbsolutePath(),
                        null,
                        this.getClass().getClassLoader(),
                        sInterfaces
                );
                try {
                    IBinder iBinder = pluginLoaderClassLoader.getInterface(
                            IBinder.class,
                            sDynamicPluginLoaderClassName,
                            new Class[]{Context.class},
                            new Object[]{PluginProcessService.this.getApplicationContext()}
                    );
                    DelegateProviderHolder.setDelegateProvider((DelegateProvider) iBinder);
                    mPluginLoader = iBinder;
                } catch (Exception e) {
                    throw new RuntimeException(
                            pluginLoaderClassLoader + " 没有找到：" + sDynamicPluginLoaderClassName,
                            e
                    );
                }
            }
            return mPluginLoader;
        }
    }
}
