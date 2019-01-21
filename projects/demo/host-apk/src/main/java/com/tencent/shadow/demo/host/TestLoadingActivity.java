package com.tencent.shadow.demo.host;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

public class TestLoadingActivity extends Activity {

    public static View content;
    public static Activity activity;

    public static void startActivity(Activity activity, Bundle bundle, View viewContent) {
        Intent intent = new Intent(activity, TestLoadingActivity.class);
        intent.putExtras(bundle);
        content = viewContent;
        activity.startActivity(intent);
    }

    public static void finishSelf() {
        if (activity != null) {
            activity.finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(content);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }
        activity = this;

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


}
