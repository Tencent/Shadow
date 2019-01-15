package com.tencent.shadow.demo.main;

import android.app.Application;

import com.tencent.shadow.demo.main.cases.TestCaseManager;

public class DemoApplication extends Application {


    @Override
    public void onCreate() {
        super.onCreate();
        TestCaseManager.initCase();
    }
}
