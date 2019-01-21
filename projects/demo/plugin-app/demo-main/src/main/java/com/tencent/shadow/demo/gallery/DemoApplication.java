package com.tencent.shadow.demo.gallery;

import android.app.Application;

import com.tencent.shadow.demo.gallery.cases.UseCaseManager;

public class DemoApplication extends Application {


    @Override
    public void onCreate() {
        super.onCreate();
        UseCaseManager.initCase();
    }
}
