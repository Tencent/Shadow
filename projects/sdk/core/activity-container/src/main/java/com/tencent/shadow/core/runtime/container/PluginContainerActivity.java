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

package com.tencent.shadow.core.runtime.container;

import static com.tencent.shadow.core.runtime.container.DelegateProvider.LOADER_VERSION_KEY;
import static com.tencent.shadow.core.runtime.container.DelegateProvider.PROCESS_ID_KEY;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;

import com.tencent.shadow.coding.java_build_config.BuildConfig;

/**
 * 插件的容器Activity。PluginLoader将把插件的Activity放在其中。
 * PluginContainerActivity以委托模式将Activity的所有回调方法委托给DelegateProviderHolder提供的Delegate。
 *
 * @author cubershi
 */
public class PluginContainerActivity extends GeneratedPluginContainerActivity implements HostActivity, HostActivityDelegator {
    private static final String TAG = "PluginContainerActivity";

    HostActivityDelegate hostActivityDelegate;

    private boolean isBeforeOnCreate = true;

    public PluginContainerActivity() {
        HostActivityDelegate delegate;
        DelegateProvider delegateProvider = DelegateProviderHolder.getDelegateProvider(getDelegateProviderKey());
        if (delegateProvider != null) {
            delegate = delegateProvider.getHostActivityDelegate(this.getClass());
            delegate.setDelegator(this);
        } else {
            Log.e(TAG, "PluginContainerActivity: DelegateProviderHolder没有初始化");
            delegate = null;
        }
        super.hostActivityDelegate = delegate;
        hostActivityDelegate = delegate;
    }

    protected String getDelegateProviderKey() {
        return DelegateProviderHolder.DEFAULT_KEY;
    }

    final public Object getPluginActivity() {
        if (hostActivityDelegate != null) {
            return hostActivityDelegate.getPluginActivity();
        } else {
            return null;
        }
    }

    @Override
    final protected void onCreate(Bundle savedInstanceState) {
        isBeforeOnCreate = false;
        mHostTheme = null;//释放资源

        boolean illegalIntent = isIllegalIntent(savedInstanceState);
        if (illegalIntent) {
            super.hostActivityDelegate = null;
            hostActivityDelegate = null;
            Log.e(TAG, "illegalIntent savedInstanceState==" + savedInstanceState + " getIntent().getExtras()==" + getIntent().getExtras());
        }

        if (hostActivityDelegate != null) {
            hostActivityDelegate.onCreate(savedInstanceState);
        } else {
            //这里是进程被杀后重启后走到，当需要恢复fragment状态的时候，由于系统保留了TAG，会因为找不到fragment引起crash
            super.onCreate(null);
            Log.e(TAG, "onCreate: hostActivityDelegate==null finish activity");
            finish();
            System.exit(0);
        }
    }

    /**
     * IllegalIntent指的是这些情况下的启动：
     * 1.插件版本变化之后，残留于系统中的PendingIntent或系统因回收内存杀死进程残留的任务栈而启动。
     * 由于插件版本变化，PluginLoader逻辑可能不一致，Intent中的参数可能不能满足新代码的启动条件。
     * 2.外部的非法启动，无法确定一个插件的Activity。
     * <p>
     * <p>
     * 3.不支持进程重启后莫名其妙的原因loader也加载了，但是可能要启动的plugin没有load，出现异常
     *
     * @param savedInstanceState onCreate时系统还回来的savedInstanceState
     * @return <code>true</code>表示这次启动不是我们预料的，需要尽早finish并退出进程。
     */
    private boolean isIllegalIntent(Bundle savedInstanceState) {
        Bundle extras = getIntent().getExtras();
        if (extras == null && savedInstanceState == null) {
            return true;
        }
        Bundle bundle;
        bundle = savedInstanceState == null ? extras : savedInstanceState;
        try {
            String loaderVersion = bundle.getString(LOADER_VERSION_KEY);
            long processVersion = bundle.getLong(PROCESS_ID_KEY);
            return !BuildConfig.VERSION_NAME.equals(loaderVersion) || processVersion != DelegateProviderHolder.sCustomPid;
        } catch (Throwable ignored) {
            //捕获可能的非法Intent中包含我们根本反序列化不了的数据
            return true;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (hostActivityDelegate != null) {
            hostActivityDelegate.onSaveInstanceState(outState);
        } else {
            super.onSaveInstanceState(outState);
        }
        //避免插件setIntent清空掉LOADER_VERSION_KEY
        outState.putString(LOADER_VERSION_KEY, BuildConfig.VERSION_NAME);
        outState.putLong(PROCESS_ID_KEY, DelegateProviderHolder.sCustomPid);
    }

    @Override
    public HostActivity getHostActivity() {
        return this;
    }

    @Override
    public Activity getImplementActivity() {
        return this;
    }

    @Override
    public Window getImplementWindow() {
        return getWindow();
    }

    /**
     * Theme一旦设置了就不能更换Theme所在的Resouces了，见{@link Resources.Theme#setTo(Resources.Theme)}
     * 而Activity在OnCreate之前需要设置Theme和使用Theme。我们需要在Activity OnCreate之后才能注入插件资源。
     * 这就需要在Activity OnCreate之前不要调用Activity的setTheme方法，同时在getTheme时返回宿主的Theme资源。
     * 注：{@link Activity#setTheme(int)}会触发初始化Theme，因此不能调用。
     */
    private Resources.Theme mHostTheme;

    @Override
    public Resources.Theme getTheme() {
        if (isBeforeOnCreate) {
            if (mHostTheme == null) {
                mHostTheme = super.getResources().newTheme();
            }
            return mHostTheme;
        } else {
            return super.getTheme();
        }
    }

    @Override
    public void setTheme(int resid) {
        if (!isBeforeOnCreate) {
            super.setTheme(resid);
        }
    }

}
