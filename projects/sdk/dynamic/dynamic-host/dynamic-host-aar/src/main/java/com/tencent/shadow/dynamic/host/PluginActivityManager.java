package com.tencent.shadow.dynamic.host;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class PluginActivityManager {

    private static PluginActivityManager sInstance = new PluginActivityManager();

    private List<WeakReference<Activity>> mActivities = new ArrayList<>();

    public static PluginActivityManager getInstance() {
        return sInstance;
    }

    public void init(Application application) {
        application.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                mActivities.add(new WeakReference<Activity>(activity));
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

            @Override
            public void onActivityDestroyed(Activity activity) {
                WeakReference<Activity> target = null;
                for (WeakReference<Activity> weakReference : mActivities) {
                    Activity act = weakReference.get();
                    if (act != null && act == activity) {
                        target = weakReference;
                    }
                }
                if (target != null) {
                    mActivities.remove(target);
                }

            }
        });
    }

    public void finishAll() {
        for (WeakReference<Activity> weakReference : mActivities) {
            Activity act = weakReference.get();
            if (act != null) {
                act.finish();
            }
        }
    }


}
