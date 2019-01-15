package com.tencent.shadow.demo.basicglsurfaceview;

import android.app.Application;
import android.os.Build;
import android.os.Looper;

import java.io.File;

public class MyApplication extends Application {
    String mInit = null;

    @Override
    public void onCreate() {
        super.onCreate();
        mInit = "Myapplication init!";

        final File filesDir = getFilesDir();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Looper.myLooper().getQueue();
        }
    }
}
