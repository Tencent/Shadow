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

import android.app.Application;
import android.app.FragmentManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.Map;

public abstract class ShadowActivity extends PluginActivity {

    private int mFragmentManagerHash;

    private PluginFragmentManager mPluginFragmentManager;

    public void setContentView(int layoutResID) {
        if ("merge".equals(XmlPullParserUtil.getLayoutStartTagName(getResources(), layoutResID))) {
            //如果传进来的xml文件的根tag是merge时，需要特殊处理
            View decorView = hostActivityDelegator.getWindow().getDecorView();
            ViewGroup viewGroup = decorView.findViewById(android.R.id.content);
            LayoutInflater.from(this).inflate(layoutResID, viewGroup);
        } else {
            View inflate = LayoutInflater.from(this).inflate(layoutResID, null);
            hostActivityDelegator.setContentView(inflate);
        }
    }

    public final ShadowApplication getApplication() {
        return mPluginApplication;
    }

    public PluginFragmentManager getFragmentManager() {
        FragmentManager fragmentManager = hostActivityDelegator.getFragmentManager();
        int hash = System.identityHashCode(fragmentManager);
        if (hash != mFragmentManagerHash) {
            mFragmentManagerHash = hash;
            mPluginFragmentManager = new PluginFragmentManager(fragmentManager);
        }
        return mPluginFragmentManager;
    }

    public final ShadowActivity getParent() {
        return null;
    }

    public void overridePendingTransition(int enterAnim, int exitAnim) {
        //如果使用的资源不是系统资源，我们无法支持这个特性。
        if ((enterAnim & 0xFF000000) != 0x01000000) {
            enterAnim = 0;
        }
        if ((exitAnim & 0xFF000000) != 0x01000000) {
            exitAnim = 0;
        }
        hostActivityDelegator.overridePendingTransition(enterAnim, exitAnim);
    }


    public void startActivityForResult(Intent intent, int requestCode) {
        startActivityForResult(intent, requestCode, null);
    }

    public void startActivityForResult(Intent intent, int requestCode, Bundle options) {
        final Intent pluginIntent = new Intent(intent);
        pluginIntent.setExtrasClassLoader(mPluginClassLoader);
        ComponentName callingActivity = new ComponentName(getPackageName(), getClass().getName());
        final boolean success = mPluginComponentLauncher.startActivityForResult(hostActivityDelegator, pluginIntent, requestCode, options, callingActivity);
        if (!success) {
            hostActivityDelegator.startActivityForResult(intent, requestCode, options);
        }
    }


    @Override
    public SharedPreferences getPreferences(int mode) {
        return super.getSharedPreferences(getLocalClassName(), mode);
    }

    public String getLocalClassName() {
        return this.getClass().getName();
    }


    public boolean shouldUpRecreateTask(Intent targetIntent) {
        Intent intent = mPluginComponentLauncher.convertPluginActivityIntent(targetIntent);
        return hostActivityDelegator.shouldUpRecreateTask(intent);
    }

    public boolean navigateUpTo(Intent upIntent) {
        Intent intent = mPluginComponentLauncher.convertPluginActivityIntent(upIntent);
        return hostActivityDelegator.navigateUpTo(intent);
    }

    public final <T extends View> T requireViewById(int id) {
        T view = findViewById(id);
        if (view == null) {
            throw new IllegalArgumentException("ID does not reference a View inside this Activity");
        }
        return view;
    }

    final private Map<ShadowActivityLifecycleCallbacks,
            Application.ActivityLifecycleCallbacks>
            mActivityLifecycleCallbacksMap = new HashMap<>();

    public void registerActivityLifecycleCallbacks(
            ShadowActivityLifecycleCallbacks callback) {
        synchronized (mActivityLifecycleCallbacksMap) {
            final ShadowActivityLifecycleCallbacks.Wrapper wrapper
                    = new ShadowActivityLifecycleCallbacks.Wrapper(callback, this);
            mActivityLifecycleCallbacksMap.put(callback, wrapper);
            hostActivityDelegator.registerActivityLifecycleCallbacks(wrapper);
        }
    }

    public void unregisterActivityLifecycleCallbacks(
            ShadowActivityLifecycleCallbacks callback) {
        synchronized (mActivityLifecycleCallbacksMap) {
            final Application.ActivityLifecycleCallbacks activityLifecycleCallbacks
                    = mActivityLifecycleCallbacksMap.get(callback);
            if (activityLifecycleCallbacks != null) {
                hostActivityDelegator.unregisterActivityLifecycleCallbacks(activityLifecycleCallbacks);
                mActivityLifecycleCallbacksMap.remove(callback);
            }
        }
    }

    @Override
    public void startIntentSenderFromChild(ShadowActivity arg0, IntentSender arg1, int arg2, Intent arg3, int arg4, int arg5, int arg6) throws IntentSender.SendIntentException {
        throw new UnsupportedOperationException("Unsupported Yet");
    }

    @Override
    public void startIntentSenderFromChild(ShadowActivity arg0, IntentSender arg1, int arg2, Intent arg3, int arg4, int arg5, int arg6, Bundle arg7) throws IntentSender.SendIntentException {
        throw new UnsupportedOperationException("Unsupported Yet");
    }

    @Override
    public boolean navigateUpToFromChild(ShadowActivity arg0, Intent arg1) {
        throw new UnsupportedOperationException("Unsupported Yet");
    }

    @Override
    public void finishFromChild(ShadowActivity arg0) {
        throw new UnsupportedOperationException("Unsupported Yet");
    }

    @Override
    public void finishActivityFromChild(ShadowActivity arg0, int arg1) {
        throw new UnsupportedOperationException("Unsupported Yet");
    }

    @Override
    public void setTheme(int resid) {
        hostActivityDelegator.setTheme(resid);
    }

    @Override
    public ComponentName getCallingActivity() {
        return mCallingActivity;
    }
}
