package com.tencent.shadow.test.dynamic.host;

import android.app.Application;
import android.os.Build;
import android.os.StrictMode;

import com.tencent.shadow.core.common.LoggerFactory;
import com.tencent.shadow.dynamic.host.DynamicRuntime;
import com.tencent.shadow.dynamic.host.PluginManager;
import com.tencent.shadow.test.dynamic.host.manager.Shadow;

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

        //在全动态架构中，Activity组件没有打包在宿主而是位于被动态加载的runtime，
        //为了防止插件crash后，系统自动恢复crash前的Activity组件，此时由于没有加载runtime而发生classNotFound异常，导致二次crash
        //因此这里恢复加载上一次的runtime
        DynamicRuntime.recoveryRuntime(this);

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
        if (mPluginManager == null) {
            mPluginManager = Shadow.getPluginManager(apk);
        }
    }

    public PluginManager getPluginManager() {
        return mPluginManager;
    }
}
