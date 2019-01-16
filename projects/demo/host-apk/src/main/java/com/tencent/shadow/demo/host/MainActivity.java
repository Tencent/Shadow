package com.tencent.shadow.demo.host;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcel;
import android.view.View;

import com.tencent.shadow.core.common.InstalledApk;
import com.tencent.shadow.core.loader.LoadParameters;
import com.tencent.shadow.runtime.ShadowApplication;
import com.tencent.shadow.runtime.container.DelegateProviderHolder;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class MainActivity extends Activity {

    private static final PreparePluginApkBloc sDemoPluginPrepareBloc
            = new PreparePluginApkBloc(
            "plugin.apk"
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.TestHostTheme);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 1) {
            for (int i = 0; i < permissions.length; i++) {
                if (permissions[i].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        throw new RuntimeException("必须赋予权限.");
                    }
                }
            }
        }
    }

    public void startDemoPlugin(View view) {
        InstalledApk installedApk = sDemoPluginPrepareBloc.preparePlugin(this.getApplicationContext());

        LoadParameters loadParameters = new LoadParameters("partMain", 0, null);

        Parcel parcel = Parcel.obtain();
        loadParameters.writeToParcel(parcel, 0);
        InstalledApk plugin = new InstalledApk(installedApk.apkFilePath, installedApk.oDexPath, installedApk.libraryPath, parcel.marshall());
        parcel.recycle();

        DemoPluginLoader pluginLoader = new DemoPluginLoader(getApplicationContext());
        DelegateProviderHolder.setDelegateProvider(pluginLoader);

        try {
            Future<?> future = pluginLoader.loadPlugin(plugin);

            future.get(10, TimeUnit.SECONDS);

            ShadowApplication application = pluginLoader.getPluginParts("partMain").getApplication();
            application.onCreate();

            Intent pluginIntent = new Intent();
            pluginIntent.setClassName("com.tencent.shadow.demo_host", "com.tencent.shadow.demo.main.splash.SplashActivity");

            Intent intent = pluginLoader.getMComponentManager().convertPluginActivityIntent(pluginIntent);
            startActivity(intent);

        } catch (Exception e) {
            throw new RuntimeException("加载失败", e);
        }

    }

}
