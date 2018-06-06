package com.tencent.cubershi.mock_interface;

import android.app.Application;
import android.content.ComponentCallbacks;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.Log;

/**
 * 用于在plugin-loader中调用假的Application方法的接口
 */
public abstract class MockApplication extends ContextWrapper {
    Resources mPluginResources;

    public MockApplication() {
        super(null);
    }

    public void onCreate() {
        Log.i("MockApplication", "MockApplication test");
    }


    public void onTerminate() {

    }


    public void onConfigurationChanged(Configuration newConfig) {

    }


    public void onLowMemory() {

    }


    public void onTrimMemory(int level) {

    }


    public void registerComponentCallbacks(ComponentCallbacks callback) {

    }


    public void unregisterComponentCallbacks(ComponentCallbacks callback) {

    }


    public void registerActivityLifecycleCallbacks(Application.ActivityLifecycleCallbacks callback) {

    }


    public void unregisterActivityLifecycleCallbacks(Application.ActivityLifecycleCallbacks callback) {

    }


    public void registerOnProvideAssistDataListener(Application.OnProvideAssistDataListener callback) {

    }


    public void unregisterOnProvideAssistDataListener(Application.OnProvideAssistDataListener callback) {

    }

    public void setHostApplicationContextAsBase(Context hostAppContext) {
        attachBaseContext(hostAppContext);
    }

    public void setPluginResources(Resources resources) {
        mPluginResources = resources;
    }
}
