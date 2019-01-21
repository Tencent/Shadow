package com.tencent.shadow.demo.host;

import android.app.Application;

import com.tencent.shadow.core.common.LoggerFactory;

public class HostApplication extends Application {
    private static Application sApp;

    @Override
    public void onCreate() {
        super.onCreate();
        sApp = this;

        LoggerFactory.setILoggerFactory(new SLoggerFactory());
    }

    public static Application getApp() {
        return sApp;
    }
}
