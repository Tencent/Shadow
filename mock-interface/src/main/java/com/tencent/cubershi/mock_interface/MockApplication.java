package com.tencent.cubershi.mock_interface;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

/**
 * 用于在plugin-loader中调用假的Application方法的接口
 */
public abstract class MockApplication {
    Context mHostAppContext;
    Resources mPluginResources;

    public void onCreate() {
        Log.i("MockApplication", "MockApplication test");
    }

    public Context getHostApplicationContext() {
        return mHostAppContext;
    }

    public void setHostApplicationContext(Context hostAppContext) {
        mHostAppContext = hostAppContext;
    }

    public void setPluginResources(Resources resources) {
        mPluginResources = resources;
    }
}
