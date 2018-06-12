package com.tencent.cubershi.mock_interface;

import android.annotation.TargetApi;
import android.app.Application;
import android.content.ComponentCallbacks;
import android.content.ComponentCallbacks2;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;

/**
 * 用于在plugin-loader中调用假的Application方法的接口
 */
public abstract class MockApplication extends MockContext {
    Resources mPluginResources;

    private MixPackageManager mMixPackageManager;

    private Application mHostApplication;

    public void onCreate() {
        mHostApplication.registerComponentCallbacks(new ComponentCallbacks2() {
            @Override
            public void onTrimMemory(int level) {
                MockApplication.this.onTrimMemory(level);
            }

            @Override
            public void onConfigurationChanged(Configuration newConfig) {
                MockApplication.this.onConfigurationChanged(newConfig);
            }

            @Override
            public void onLowMemory() {
                MockApplication.this.onLowMemory();
            }
        });
    }


    public void onTerminate() {
        throw new UnsupportedOperationException();
    }


    public void onConfigurationChanged(Configuration newConfig) {
        //do nothing.
    }


    public void onLowMemory() {
        //do nothing.
    }


    public void onTrimMemory(int level) {
        //do nothing.
    }


    public void registerComponentCallbacks(ComponentCallbacks callback) {
        mHostApplication.registerComponentCallbacks(callback);
    }


    public void unregisterComponentCallbacks(ComponentCallbacks callback) {
        mHostApplication.unregisterComponentCallbacks(callback);
    }


    public void registerActivityLifecycleCallbacks(Application.ActivityLifecycleCallbacks callback) {
        mHostApplication.registerActivityLifecycleCallbacks(callback);
    }


    public void unregisterActivityLifecycleCallbacks(Application.ActivityLifecycleCallbacks callback) {
        mHostApplication.unregisterActivityLifecycleCallbacks(callback);
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void registerOnProvideAssistDataListener(Application.OnProvideAssistDataListener callback) {
        mHostApplication.registerOnProvideAssistDataListener(callback);
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void unregisterOnProvideAssistDataListener(Application.OnProvideAssistDataListener callback) {
        mHostApplication.unregisterOnProvideAssistDataListener(callback);
    }

    public void setHostApplicationContextAsBase(Context hostAppContext) {
        attachBaseContext(hostAppContext);
        mHostApplication = (Application) hostAppContext;
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
