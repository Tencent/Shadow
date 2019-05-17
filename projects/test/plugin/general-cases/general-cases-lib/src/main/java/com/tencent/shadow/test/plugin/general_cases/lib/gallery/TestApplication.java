package com.tencent.shadow.test.plugin.general_cases.lib.gallery;

import android.app.Application;

import com.tencent.shadow.test.plugin.general_cases.lib.gallery.cases.UseCaseManager;

public class TestApplication extends Application {

    private static TestApplication sInstence;

    public boolean isOnCreate;

    @Override
    public void onCreate() {
        sInstence = this;
        isOnCreate = true;
        super.onCreate();
        UseCaseManager.initCase();
    }

    public static TestApplication getInstance() {
        return sInstence;
    }
}
