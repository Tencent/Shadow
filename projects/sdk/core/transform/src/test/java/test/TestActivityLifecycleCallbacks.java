package test;

import android.app.Application;

public class TestActivityLifecycleCallbacks implements Application.ActivityLifecycleCallbacks {

    Application.ActivityLifecycleCallbacks get() {
        System.out.println("get ActivityLifecycleCallbacks");
        return this;
    }
}
