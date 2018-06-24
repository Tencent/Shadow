package com.tencent.cubershi.mock_interface;

import android.annotation.TargetApi;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.ComponentCallbacks;
import android.content.ComponentCallbacks2;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.util.Pair;

import java.util.HashMap;
import java.util.Map;

/**
 * 用于在plugin-loader中调用假的Application方法的接口
 */
public abstract class MockApplication extends MockContext {
    private MixPackageManager mMixPackageManager;

    private Application mHostApplication;

    private Map<String, String> mReceivers;

    @Override
    public Context getApplicationContext() {
        return this;
    }

    private Map<MockActivityLifecycleCallbacks, Application.ActivityLifecycleCallbacks> mActivityLifecycleCallbacksMap = new HashMap<>();

    public void onCreate() {
        for (Map.Entry<String, String> entry: mReceivers.entrySet()){
            try {
                Class<?> clazz = mPluginClassLoader.loadClass(entry.getValue());
                BroadcastReceiver receiver = ((BroadcastReceiver) clazz.newInstance());
                IntentFilter intentFilter = new IntentFilter();
                intentFilter.addAction(entry.getKey());
                mHostApplication.registerReceiver(receiver, intentFilter);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        }
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


    public void registerActivityLifecycleCallbacks(MockActivityLifecycleCallbacks callback) {
        final MockActivityLifecycleCallbacks.Wrapper wrapper
                = new MockActivityLifecycleCallbacks.Wrapper(callback);
        mActivityLifecycleCallbacksMap.put(callback, wrapper);
        mHostApplication.registerActivityLifecycleCallbacks(wrapper);
    }


    public void unregisterActivityLifecycleCallbacks(MockActivityLifecycleCallbacks callback) {
        final Application.ActivityLifecycleCallbacks activityLifecycleCallbacks
                = mActivityLifecycleCallbacksMap.get(callback);
        if (activityLifecycleCallbacks != null) {
            mHostApplication.unregisterActivityLifecycleCallbacks(activityLifecycleCallbacks);
        }
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

    public void setPluginPackageManager(PackageManager pluginPackageManager) {
        mMixPackageManager = new MixPackageManager(super.getPackageManager(), pluginPackageManager);
    }

    public void setReceivers(Map<String, String> receivers){
        mReceivers = receivers;
    }

    @Override
    public PackageManager getPackageManager() {
        return mMixPackageManager;
    }
}
