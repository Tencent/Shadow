package com.tencent.shadow.dynamic.host;

import android.app.Activity;
import android.app.Application;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;

import com.tencent.shadow.core.common.Logger;
import com.tencent.shadow.core.common.LoggerFactory;

import java.util.LinkedList;
import java.util.List;

abstract public class BasePluginProcessService extends Service {

    protected final Logger mLogger = LoggerFactory.getLogger(this.getClass());

    /**
     * PPS应该代表插件进程的生命周期。插件进程应该由PPS启动而启动。
     * 所以不应该出现在同一个插件进程有两个PPS对象的情况。
     * 如果出现，将会重复加载Loader、Runtime、业务等插件，进而出现非常奇怪的异常。
     * 因此，用这样一个静态变量检测出这种情况。PPS不能死后重新创建。需要在上层合理设计保持PPS始终存活。
     */
    private static Object sSingleInstanceFlag = null;

    @Override
    public void onCreate() {
        if (sSingleInstanceFlag == null) {
            sSingleInstanceFlag = new Object();
        } else {
            throw new IllegalStateException("PPS出现多实例");
        }
        super.onCreate();
        if (mLogger.isInfoEnabled()) {
            mLogger.info("onCreate:" + this);
        }
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

    public static class ActivityHolder implements Application.ActivityLifecycleCallbacks {

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
