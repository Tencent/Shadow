package com.tencent.shadow.sample.host;

import android.app.Application;
import com.tencent.shadow.sample.introduce_shadow_lib.InitApplication;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        InitApplication.onApplicationCreate(this);
    }
}
