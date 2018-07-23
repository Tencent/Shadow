package com.tencent.shadow.runtime;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import com.tencent.hydevteam.pluginframework.plugincontainer.PluginContainerActivity;

public interface MockActivityLifecycleCallbacks {

    void onActivityCreated(MockActivity activity, Bundle savedInstanceState);

    void onActivityStarted(MockActivity activity);

    void onActivityResumed(MockActivity activity);

    void onActivityPaused(MockActivity activity);

    void onActivityStopped(MockActivity activity);

    void onActivitySaveInstanceState(MockActivity activity, Bundle outState);

    void onActivityDestroyed(MockActivity activity);

    class Wrapper implements Application.ActivityLifecycleCallbacks {

        final MockActivityLifecycleCallbacks mock;

        public Wrapper(MockActivityLifecycleCallbacks mock) {
            this.mock = mock;
        }

        private MockActivity getPluginActivity(Activity activity) {
            if (activity instanceof PluginContainerActivity) {
                return (MockActivity) ((PluginContainerActivity) activity).getPluginActivity();
            } else {
                return null;
            }
        }

        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            final MockActivity pluginActivity = getPluginActivity(activity);
            if (pluginActivity != null) {
                mock.onActivityCreated(pluginActivity, savedInstanceState);
            }
        }

        @Override
        public void onActivityStarted(Activity activity) {
            final MockActivity pluginActivity = getPluginActivity(activity);
            if (pluginActivity != null) {
                mock.onActivityStarted(pluginActivity);
            }
        }

        @Override
        public void onActivityResumed(Activity activity) {
            final MockActivity pluginActivity = getPluginActivity(activity);
            if (pluginActivity != null) {
                mock.onActivityResumed(pluginActivity);
            }
        }

        @Override
        public void onActivityPaused(Activity activity) {
            final MockActivity pluginActivity = getPluginActivity(activity);
            if (pluginActivity != null) {
                mock.onActivityPaused(pluginActivity);
            }
        }

        @Override
        public void onActivityStopped(Activity activity) {
            final MockActivity pluginActivity = getPluginActivity(activity);
            if (pluginActivity != null) {
                mock.onActivityStopped(pluginActivity);
            }
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
            final MockActivity pluginActivity = getPluginActivity(activity);
            if (pluginActivity != null) {
                mock.onActivitySaveInstanceState(pluginActivity, outState);
            }
        }

        @Override
        public void onActivityDestroyed(Activity activity) {
            final MockActivity pluginActivity = getPluginActivity(activity);
            if (pluginActivity != null) {
                mock.onActivityDestroyed(pluginActivity);
            }
        }
    }
}
