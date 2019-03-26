package com.tencent.shadow.demo.host;

import android.app.Application;
import android.os.Build;
import android.os.StrictMode;

import com.tencent.shadow.core.common.LoggerFactory;
import com.tencent.shadow.demo.host.manager.Shadow;
import com.tencent.shadow.dynamic.host.PluginManager;

import java.io.File;

public class HostApplication extends Application {
    private static HostApplication sApp;

    private PluginManager mPluginManager;

    final public SimpleIdlingResource mIdlingResource = new SimpleIdlingResource();

    @Override
    public void onCreate() {
        super.onCreate();
        sApp = this;

        detectNonSdkApiUsageOnAndroidP();

        LoggerFactory.setILoggerFactory(new DemoLoggerFactory());

        PluginHelper.getInstance().init(this);

    }

    private static void detectNonSdkApiUsageOnAndroidP() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            return;
        }
        boolean isRunningEspressoTest;
        try {
            Class.forName("androidx.test.espresso.Espresso");
            isRunningEspressoTest = true;
        } catch (Exception ignored) {
            isRunningEspressoTest = false;
        }
        if (isRunningEspressoTest) {
            return;
        }
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        builder.penaltyDeath();
        builder.detectNonSdkApiUsage();
        StrictMode.setVmPolicy(builder.build());
    }

    public static HostApplication getApp() {
        return sApp;
    }

    public void loadPluginManager(File apk) {
        if (mPluginManager != null) {
            throw new IllegalStateException("mPluginManager != null");
        }
        mPluginManager = Shadow.getPluginManager(apk);
    }

    public PluginManager getPluginManager() {
        return mPluginManager;
    }
}
