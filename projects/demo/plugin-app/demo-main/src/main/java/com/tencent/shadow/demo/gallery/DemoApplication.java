package com.tencent.shadow.demo.gallery;

import android.app.Application;

import com.tencent.shadow.demo.gallery.cases.UseCaseManager;

public class DemoApplication extends Application {

    private static DemoApplication sInstence;

    public boolean isOnCreate;

    @Override
    public void onCreate() {
        sInstence = this;
        isOnCreate = true;
        super.onCreate();
        UseCaseManager.initCase();
    }

    public static DemoApplication getInstance(){
        return sInstence;
    }
}
