package com.tencent.shadow.demo.host;

import android.app.Application;
import android.os.Build;
import android.os.Parcel;
import android.os.StrictMode;

import com.tencent.shadow.core.common.InstalledApk;
import com.tencent.shadow.core.common.LoggerFactory;
import com.tencent.shadow.core.load_parameters.LoadParameters;
import com.tencent.shadow.core.loader.ShadowPluginLoader;
import com.tencent.shadow.runtime.container.ContentProviderDelegateProviderHolder;
import com.tencent.shadow.runtime.container.DelegateProviderHolder;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class HostApplication extends Application {
    private static Application sApp;

    public final static String PART_MAIN = "partMain";

    private static final PreparePluginApkBloc sDemoPluginPrepareBloc
            = new PreparePluginApkBloc(
            "plugin.apk"
    );

    static {
        detectNonSdkApiUsageOnAndroidP();

        LoggerFactory.setILoggerFactory(new SLoggerFactory());
    }

    private ShadowPluginLoader mPluginLoader;

    private final Map<String, InstalledApk> mPluginMap = new HashMap<>();

    public void loadPlugin(String partKey) {
        InstalledApk installedApk = mPluginMap.get(partKey);
        if (installedApk == null) {
            throw new NullPointerException("partKey == " + partKey);
        }

        LoadParameters loadParameters = new LoadParameters(partKey, null);

        Parcel parcel = Parcel.obtain();
        loadParameters.writeToParcel(parcel, 0);
        InstalledApk plugin = new InstalledApk(
                installedApk.apkFilePath,
                installedApk.oDexPath,
                installedApk.libraryPath,
                parcel.marshall()
        );
        parcel.recycle();


        try {
            ShadowPluginLoader pluginLoader = this.mPluginLoader;
            Future<?> future = pluginLoader.loadPlugin(plugin);

            future.get(10, TimeUnit.SECONDS);

            pluginLoader.callApplicationOnCreate(partKey);
        } catch (Exception e) {
            throw new RuntimeException("加载失败", e);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sApp = this;

        ShadowPluginLoader loader = mPluginLoader = new DemoPluginLoader(getApplicationContext());
        loader.onCreate();
        DelegateProviderHolder.setDelegateProvider(loader);
        ContentProviderDelegateProviderHolder.setContentProviderDelegateProvider(loader);

        InstalledApk installedApk = sDemoPluginPrepareBloc.preparePlugin(this.getApplicationContext());
        mPluginMap.put(PART_MAIN, installedApk);
    }

    private static void detectNonSdkApiUsageOnAndroidP() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            return;
        }
        boolean isRunningEspressoTest;
        try {
            Class.forName("android.support.test.espresso.Espresso");
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

    public static Application getApp() {
        return sApp;
    }

    public ShadowPluginLoader getPluginLoader() {
        return mPluginLoader;
    }
}
