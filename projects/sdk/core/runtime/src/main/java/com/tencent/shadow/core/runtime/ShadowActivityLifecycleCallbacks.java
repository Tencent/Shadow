package com.tencent.shadow.core.runtime;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import com.tencent.shadow.core.runtime.container.PluginContainerActivity;

public interface ShadowActivityLifecycleCallbacks {

    void onActivityCreated(ShadowActivity activity, Bundle savedInstanceState);

    void onActivityStarted(ShadowActivity activity);

    void onActivityResumed(ShadowActivity activity);

    void onActivityPaused(ShadowActivity activity);

    void onActivityStopped(ShadowActivity activity);

    void onActivitySaveInstanceState(ShadowActivity activity, Bundle outState);

    void onActivityDestroyed(ShadowActivity activity);

    class Wrapper implements Application.ActivityLifecycleCallbacks {

        final ShadowActivityLifecycleCallbacks shadowActivityLifecycleCallbacks;
        final ShadowApplication shadowApplication;

        public Wrapper(ShadowActivityLifecycleCallbacks shadowActivityLifecycleCallbacks, ShadowApplication shadowApplication) {
            this.shadowActivityLifecycleCallbacks = shadowActivityLifecycleCallbacks;
            this.shadowApplication = shadowApplication;
        }

        private ShadowActivity getPluginActivity(Activity activity) {
            if (activity instanceof PluginContainerActivity) {
                return (ShadowActivity) ((PluginContainerActivity) activity).getPluginActivity();
            } else {
                return null;
            }
        }

        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            final ShadowActivity pluginActivity = getPluginActivity(activity);
            if (checkOwnerActivity(pluginActivity) ) {
                shadowActivityLifecycleCallbacks.onActivityCreated(pluginActivity, savedInstanceState);
            }
        }

        @Override
        public void onActivityStarted(Activity activity) {
            final ShadowActivity pluginActivity = getPluginActivity(activity);
            if (checkOwnerActivity(pluginActivity) ) {
                shadowActivityLifecycleCallbacks.onActivityStarted(pluginActivity);
            }
        }

        @Override
        public void onActivityResumed(Activity activity) {
            final ShadowActivity pluginActivity = getPluginActivity(activity);
            if (checkOwnerActivity(pluginActivity) ) {
                shadowActivityLifecycleCallbacks.onActivityResumed(pluginActivity);
            }
        }

        @Override
        public void onActivityPaused(Activity activity) {
            final ShadowActivity pluginActivity = getPluginActivity(activity);
            if (checkOwnerActivity(pluginActivity) ) {
                shadowActivityLifecycleCallbacks.onActivityPaused(pluginActivity);
            }
        }

        @Override
        public void onActivityStopped(Activity activity) {
            final ShadowActivity pluginActivity = getPluginActivity(activity);
            if (checkOwnerActivity(pluginActivity) ) {
                shadowActivityLifecycleCallbacks.onActivityStopped(pluginActivity);
            }
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
            final ShadowActivity pluginActivity = getPluginActivity(activity);
            if (checkOwnerActivity(pluginActivity) ) {
                shadowActivityLifecycleCallbacks.onActivitySaveInstanceState(pluginActivity, outState);
            }
        }

        @Override
        public void onActivityDestroyed(Activity activity) {
            final ShadowActivity pluginActivity = getPluginActivity(activity);
            if (checkOwnerActivity(pluginActivity) ) {
                shadowActivityLifecycleCallbacks.onActivityDestroyed(pluginActivity);
            }
        }

        /**
         * 检测Activity是否属于当前Application所在的插件
         *
         * @param activity 插件Activity
         * @return 是否属于当前Application所在的插件 true属于
         */
        private boolean checkOwnerActivity(PluginActivity activity) {
            return activity != null && activity.mPluginApplication == shadowApplication;
        }
    }
}
