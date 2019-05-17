package com.tencent.shadow.test.none_dynamic.host;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import static com.tencent.shadow.test.none_dynamic.host.HostApplication.PART_MAIN;

public class MainActivity extends Activity {

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
        HostApplication application = (HostApplication) getApplication();
        application.loadPlugin(PART_MAIN);

        Intent pluginIntent = new Intent();
        pluginIntent.setClassName(getPackageName(), "com.tencent.shadow.test.plugin.general_cases.lib.gallery.splash.SplashActivity");

        Intent intent = application.getPluginLoader().getMComponentManager().convertPluginActivityIntent(pluginIntent);
        startActivity(intent);
    }

}
