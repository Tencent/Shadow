package com.tencent.shadow.demo.gallery;

import android.support.multidex.MultiDexApplication;

import com.tencent.shadow.demo.gallery.cases.UseCaseManager;

public class DemoApplication extends MultiDexApplication {

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
