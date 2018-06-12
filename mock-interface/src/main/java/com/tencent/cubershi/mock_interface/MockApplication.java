package com.tencent.cubershi.mock_interface;

import android.app.Application;
import android.content.ComponentCallbacks;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.Log;

/**
 * 用于在plugin-loader中调用假的Application方法的接口
 */
public abstract class MockApplication extends MockContext {
    Resources mPluginResources;

    private MixPackageManager mMixPackageManager;

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

    public void setPluginPackageManager(PackageManager pluginPackageManager) {
        mMixPackageManager = new MixPackageManager(super.getPackageManager(), pluginPackageManager);
    }

    @Override
    public PackageManager getPackageManager() {
        return mMixPackageManager;
    }
}
