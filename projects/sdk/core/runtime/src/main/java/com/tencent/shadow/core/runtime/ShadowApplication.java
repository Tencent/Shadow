/*
 * Tencent is pleased to support the open source community by making Tencent Shadow available.
 * Copyright (C) 2019 THL A29 Limited, a Tencent company.  All rights reserved.
 *
 * Licensed under the BSD 3-Clause License (the "License"); you may not use
 * this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 *     https://opensource.org/licenses/BSD-3-Clause
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.tencent.shadow.core.runtime;

import android.annotation.TargetApi;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.ComponentCallbacks;
import android.content.ComponentCallbacks2;
import android.content.Context;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Build;

import java.util.List;
import java.util.Map;

/**
 * 用于在plugin-loader中调用假的Application方法的接口
 */
public class ShadowApplication extends ShadowContext {

    private Application mHostApplication;

    private Map<String, List<String>> mBroadcasts;

    private ShadowAppComponentFactory mAppComponentFactory;

    public boolean isCallOnCreate;

    @Override
    public Context getApplicationContext() {
        return this;
    }

    private ShadowActivityLifecycleCallbacks.Holder lifecycleCallbacksHolder;

    public void registerActivityLifecycleCallbacks(
            ShadowActivityLifecycleCallbacks callback) {
        lifecycleCallbacksHolder.registerActivityLifecycleCallbacks(this, callback);
    }

    public void unregisterActivityLifecycleCallbacks(
            ShadowActivityLifecycleCallbacks callback) {
        lifecycleCallbacksHolder.unregisterActivityLifecycleCallbacks(callback);
    }

    public void onCreate() {

        isCallOnCreate = true;

        for (Map.Entry<String, List<String>> entry : mBroadcasts.entrySet()) {
            try {
                Class<?> clazz = mPluginClassLoader.loadClass(entry.getKey());
                BroadcastReceiver receiver = ((BroadcastReceiver) clazz.newInstance());
                mAppComponentFactory.instantiateReceiver(mPluginClassLoader, entry.getKey(), null);

                IntentFilter intentFilter = new IntentFilter();
                for (String action:entry.getValue()
                     ) {
                    intentFilter.addAction(action);
                }
                registerReceiver(receiver, intentFilter);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        mHostApplication.registerComponentCallbacks(new ComponentCallbacks2() {
            @Override
            public void onTrimMemory(int level) {
                ShadowApplication.this.onTrimMemory(level);
            }

            @Override
            public void onConfigurationChanged(Configuration newConfig) {
                ShadowApplication.this.onConfigurationChanged(newConfig);
            }

            @Override
            public void onLowMemory() {
                ShadowApplication.this.onLowMemory();
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

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void registerOnProvideAssistDataListener(Application.OnProvideAssistDataListener callback) {
        mHostApplication.registerOnProvideAssistDataListener(callback);
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void unregisterOnProvideAssistDataListener(Application.OnProvideAssistDataListener callback) {
        mHostApplication.unregisterOnProvideAssistDataListener(callback);
    }

    public void setHostApplicationContextAsBase(Context hostAppContext) {
        super.attachBaseContext(hostAppContext);
        mHostApplication = (Application) hostAppContext;
        lifecycleCallbacksHolder
                = new ShadowActivityLifecycleCallbacks.Holder(mHostApplication);
    }

    public void setBroadcasts(Map<String, List<String>> broadcast){
        mBroadcasts = broadcast;
    }

    public void attachBaseContext(Context base) {
        //do nothing.
    }

    public void setAppComponentFactory(ShadowAppComponentFactory factory) {
        mAppComponentFactory = factory;
    }
}
