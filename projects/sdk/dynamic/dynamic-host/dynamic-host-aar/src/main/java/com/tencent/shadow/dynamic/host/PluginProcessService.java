package com.tencent.shadow.dynamic.host;

import android.app.Activity;
import android.app.Application;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;

import com.tencent.shadow.core.common.InstalledApk;
import com.tencent.shadow.core.common.Logger;
import com.tencent.shadow.core.common.LoggerFactory;

import java.io.File;
import java.util.LinkedList;
import java.util.List;


public class PluginProcessService extends Service {
    private static final Logger mLogger = LoggerFactory.getLogger(PluginProcessService.class);

    private final PpsController.Stub mPpsController = new PpsControllerImpl();

    private static final ActivityHolder sActivityHolder = new ActivityHolder();

    public static Application.ActivityLifecycleCallbacks getActivityHolder() {
        return sActivityHolder;
    }

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
                "com.tencent.shadow.core.common",
                "com.tencent.shadow.core.common",
        };

        private final static String sDynamicPluginLoaderClassName
                = "com.tencent.shadow.dynamic.loader.DynamicPluginLoader";

        private IBinder mPluginLoader;

        @Override
        public void loadRuntime(String uuid) throws RemoteException {
            if (mLogger.isInfoEnabled()) {
                mLogger.info("loadRuntime uuid:" + uuid);
            }
            InstalledApk installedApk = mUuidManager.getRuntime(uuid);
            InstalledApk installedRuntimeApk = new InstalledApk(installedApk.apkFilePath, installedApk.oDexPath, installedApk.libraryPath);
            boolean loaded = DynamicRuntime.loadRuntime(installedRuntimeApk);
            if (loaded) {
                DynamicRuntime.saveLastRuntimeInfo(PluginProcessService.this, installedRuntimeApk);
            }

        }

        @Override
        public IBinder loadPluginLoader(String uuid) throws RemoteException {
            if (mLogger.isInfoEnabled()) {
                mLogger.info("loadPluginLoader uuid:" + uuid + " loader:" + mPluginLoader);
            }
            if (mPluginLoader == null) {
                InstalledApk installedApk = mUuidManager.getPluginLoader(uuid);
                File file = new File(installedApk.apkFilePath);
                if (!file.exists()) {
                    throw new RuntimeException(file.getAbsolutePath() + "文件不存在");
                }
                ApkClassLoader pluginLoaderClassLoader = new ApkClassLoader(
                        installedApk.apkFilePath,
                        installedApk.oDexPath,
                        installedApk.libraryPath,
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

        @Override
        public void exit() throws RemoteException {
            if (mLogger.isInfoEnabled()) {
                mLogger.info("exit ");
            }
            sActivityHolder.finishAll();
            System.exit(0);
            try {
                wait();
            } catch (InterruptedException ignored) {
            }
        }
    }

    private static class ActivityHolder implements Application.ActivityLifecycleCallbacks {

        private List<Activity> mActivities = new LinkedList<>();

        void finishAll() {
            for (Activity activity : mActivities) {
                activity.finish();
            }
        }

        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            mActivities.add(activity);
        }

        @Override
        public void onActivityDestroyed(Activity activity) {
            mActivities.remove(activity);
        }

        @Override
        public void onActivityStarted(Activity activity) {

        }

        @Override
        public void onActivityResumed(Activity activity) {

        }

        @Override
        public void onActivityPaused(Activity activity) {

        }

        @Override
        public void onActivityStopped(Activity activity) {

        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

        }


    }
}
