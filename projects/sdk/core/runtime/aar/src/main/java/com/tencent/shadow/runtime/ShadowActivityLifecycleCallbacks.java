package com.tencent.shadow.runtime;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import com.tencent.hydevteam.pluginframework.plugincontainer.PluginContainerActivity;

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

        public Wrapper(ShadowActivityLifecycleCallbacks shadowActivityLifecycleCallbacks) {
            this.shadowActivityLifecycleCallbacks = shadowActivityLifecycleCallbacks;
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
            if (pluginActivity != null) {
                shadowActivityLifecycleCallbacks.onActivityCreated(pluginActivity, savedInstanceState);
            }
        }

        @Override
        public void onActivityStarted(Activity activity) {
            final ShadowActivity pluginActivity = getPluginActivity(activity);
            if (pluginActivity != null) {
                shadowActivityLifecycleCallbacks.onActivityStarted(pluginActivity);
            }
        }

        @Override
        public void onActivityResumed(Activity activity) {
            final ShadowActivity pluginActivity = getPluginActivity(activity);
            if (pluginActivity != null) {
                shadowActivityLifecycleCallbacks.onActivityResumed(pluginActivity);
            }
        }

        @Override
        public void onActivityPaused(Activity activity) {
            final ShadowActivity pluginActivity = getPluginActivity(activity);
            if (pluginActivity != null) {
                shadowActivityLifecycleCallbacks.onActivityPaused(pluginActivity);
            }
        }

        @Override
        public void onActivityStopped(Activity activity) {
            final ShadowActivity pluginActivity = getPluginActivity(activity);
            if (pluginActivity != null) {
                shadowActivityLifecycleCallbacks.onActivityStopped(pluginActivity);
            }
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
            final ShadowActivity pluginActivity = getPluginActivity(activity);
            if (pluginActivity != null) {
                shadowActivityLifecycleCallbacks.onActivitySaveInstanceState(pluginActivity, outState);
            }
        }

        @Override
        public void onActivityDestroyed(Activity activity) {
            final ShadowActivity pluginActivity = getPluginActivity(activity);
            if (pluginActivity != null) {
                shadowActivityLifecycleCallbacks.onActivityDestroyed(pluginActivity);
            }
        }
    }
}
