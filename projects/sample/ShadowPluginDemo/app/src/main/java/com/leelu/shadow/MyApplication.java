package com.leelu.shadow;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.StrictMode;
import android.webkit.WebView;

import com.leelu.shadow.manager.PluginHelper;
import com.leelu.shadow.manager.Shadow;
import com.tencent.shadow.core.common.LoggerFactory;
import com.tencent.shadow.dynamic.host.DynamicRuntime;
import com.tencent.shadow.dynamic.host.PluginManager;

import java.io.File;

import static android.os.Process.myPid;

/**
 * CreateDate: 2022/3/15 17:41
 * Author: 李露
 * Email: lilu2@haier.com
 * Version: 1.0
 * Description:
 */
public class MyApplication extends Application {
    private static MyApplication sApp;
    private static PluginManager sPluginManager;//这个PluginManager对象在Manager升级前后是不变的。它内部持有具体实现，升级时更换具体实现

    @Override
    public void onCreate() {
        super.onCreate();
        sApp = this;
/*        detectNonSdkApiUsageOnAndroidP();
        setWebViewDataDirectorySuffix();*/
        LoggerFactory.setILoggerFactory(new AndroidLogLoggerFactory());

        if (isProcess(this, ":main_plugin")) {//TODO
            //在全动态架构中，Activity组件没有打包在宿主而是位于被动态加载的runtime，
            //为了防止插件crash后，系统自动恢复crash前的Activity组件，此时由于没有加载runtime而发生classNotFound异常，导致二次crash
            //因此这里恢复加载上一次的runtime
            DynamicRuntime.recoveryRuntime(this);
        }
        PluginHelper.getInstance().init(this);
    }

    private static void setWebViewDataDirectorySuffix() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            return;
        }
        WebView.setDataDirectorySuffix(Application.getProcessName());
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
        builder.detectNonSdkApiUsage();
        StrictMode.setVmPolicy(builder.build());
    }

    public static MyApplication getApp() {
        return sApp;
    }

    public void loadPluginManager(File apk) {
        if (sPluginManager == null) {
            sPluginManager = Shadow.getPluginManager(apk);
        }
    }

    public PluginManager getPluginManager() {
        return sPluginManager;
    }

    private static boolean isProcess(Context context, String processName) {
        String currentProcName = "";
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo processInfo : manager.getRunningAppProcesses()) {
            if (processInfo.pid == myPid()) {
                currentProcName = processInfo.processName;
                break;
            }
        }
        return currentProcName.endsWith(processName);
    }
}
