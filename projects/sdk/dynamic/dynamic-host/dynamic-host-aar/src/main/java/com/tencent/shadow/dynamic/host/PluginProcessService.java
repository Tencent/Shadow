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

import static com.tencent.shadow.dynamic.host.FailedException.ERROR_CODE_FILE_NOT_FOUND_EXCEPTION;


public class PluginProcessService extends Service {
    private static final Logger mLogger = LoggerFactory.getLogger(PluginProcessService.class);

    private final PpsBinder mPpsControllerBinder = new PpsBinder(this);

    static final ActivityHolder sActivityHolder = new ActivityHolder();

    public static Application.ActivityLifecycleCallbacks getActivityHolder() {
        return sActivityHolder;
    }

    public static PpsController wrapBinder(IBinder ppsBinder) {
        return new PpsController(ppsBinder);
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
        return mPpsControllerBinder;
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

    private UuidManager mRpcUuidManager;

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

    void loadRuntime(String uuid) throws FailedException {
        try {
            if (mLogger.isInfoEnabled()) {
                mLogger.info("loadRuntime uuid:" + uuid);
            }
            InstalledApk installedApk;
            try {
                installedApk = mRpcUuidManager.getRuntime(uuid);
            } catch (RemoteException e) {
                throw new FailedException(e);
            } catch (NotFoundException e) {
                throw new FailedException(ERROR_CODE_FILE_NOT_FOUND_EXCEPTION, "uuid==" + uuid + "的Runtime没有找到。cause:" + e.getMessage());
            }

            InstalledApk installedRuntimeApk = new InstalledApk(installedApk.apkFilePath, installedApk.oDexPath, installedApk.libraryPath);
            boolean loaded = DynamicRuntime.loadRuntime(installedRuntimeApk);
            if (loaded) {
                DynamicRuntime.saveLastRuntimeInfo(this, installedRuntimeApk);
            }
        } catch (RuntimeException e) {
            if (mLogger.isErrorEnabled()) {
                mLogger.error("loadRuntime发生RuntimeException", e);
            }
            throw new FailedException(e);
        }
    }

    IBinder loadPluginLoader(String uuid) throws FailedException {
        //todo 检测重复加载，不能忽略这种错误
        try {
            if (mLogger.isInfoEnabled()) {
                mLogger.info("loadPluginLoader uuid:" + uuid + " loader:" + mPluginLoader);
            }
            if (mPluginLoader == null) {
                InstalledApk installedApk;
                try {
                    installedApk = mRpcUuidManager.getPluginLoader(uuid);
                } catch (RemoteException e) {
                    throw new FailedException(e);
                } catch (NotFoundException e) {
                    throw new FailedException(ERROR_CODE_FILE_NOT_FOUND_EXCEPTION, "uuid==" + uuid + "的PluginLoader没有找到。cause:" + e.getMessage());
                }
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
                mPluginLoader = pluginLoaderClassLoader.getInterface(
                        IBinder.class,
                        sDynamicPluginLoaderClassName,
                        new Class[]{Context.class, UuidManager.class, String.class},
                        new Object[]{getApplicationContext(), mRpcUuidManager, uuid}
                );
            }
            return mPluginLoader;
        } catch (RuntimeException e) {
            if (mLogger.isErrorEnabled()) {
                mLogger.error("loadPluginLoader发生RuntimeException", e);
            }
            throw new FailedException(e);
        }
    }

    void setRpcUuidManager(UuidManager rpcUuidManager) {
        if (mLogger.isInfoEnabled()) {
            mLogger.info("setRpcUuidManager ");
        }
        mRpcUuidManager = rpcUuidManager;
    }

    void exit() {
        if (mLogger.isInfoEnabled()) {
            mLogger.info("exit ");
        }
        PluginProcessService.sActivityHolder.finishAll();
        System.exit(0);
        try {
            wait();
        } catch (InterruptedException ignored) {
        }
    }

    static class ActivityHolder implements Application.ActivityLifecycleCallbacks {

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
