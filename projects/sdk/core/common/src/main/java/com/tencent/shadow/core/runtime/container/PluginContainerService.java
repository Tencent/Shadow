/*
 * Tencent is pleased to support the open source community by making Tencent Shadow available.
 * Copyright (C) 2019 THL A29 Limited, a Tencent company.  All rights reserved.
 *
 * Licensed under the BSD 3-Clause License (the "License"); you may not use
 * this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * https://opensource.org/licenses/BSD-3-Clause
 */
package com.tencent.shadow.core.runtime.container;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * 插件前台Service的容器壳。注册在宿主manifest，是AMS认可的真实Android Service。
 * 一个容器组件可以同时代理多个插件Service，插件生命周期和前台状态由
 * PluginServiceManager分别管理；仅当容器内没有插件Service时才停止真实Service。
 */
public class PluginContainerService extends Service {

    private static final String TAG = "PluginContainerService";

    public static final String EXTRA_PLUGIN_SERVICE_CLASS = "shadow_plugin_service_class";
    public static final String EXTRA_PLUGIN_PACKAGE_NAME = "shadow_plugin_package_name";
    public static final String EXTRA_ORIGINAL_EXTRAS = "shadow_original_extras";
    public static final String EXTRA_DELEGATE_PROVIDER_KEY = "shadow_delegate_provider_key";
    public static final String EXTRA_PROCESS_ID = "shadow_process_id";

    private PluginContainerServiceDelegate mDelegate;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // 本类位于宿主侧，与动态下发的Loader版本可以不同，因此不校验Loader版本。
        // 同一进程内Loader不会被替换，PROCESS_ID足以拦截进程重启后系统重投递的残留Intent。
        if (intent == null || intent.getLongExtra(EXTRA_PROCESS_ID, -1L)
                != PluginContainerServiceDelegateHolder.PROCESS_ID) {
            Log.e(TAG, "Reject stale or incomplete container Service intent");
            stopSelfResult(startId);
            return START_NOT_STICKY;
        }

        String delegateProviderKey = intent.getStringExtra(EXTRA_DELEGATE_PROVIDER_KEY);
        if (delegateProviderKey == null) {
            Log.e(TAG, "Reject container Service intent without delegate key");
            stopSelfResult(startId);
            return START_NOT_STICKY;
        }
        mDelegate = PluginContainerServiceDelegateHolder.getDelegate(delegateProviderKey);
        if (mDelegate == null) {
            Log.e(TAG, "No Service delegate for key=" + delegateProviderKey);
            stopSelfResult(startId);
            return START_NOT_STICKY;
        }
        return mDelegate.onStartCommand(this, intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy");
        if (mDelegate != null) {
            mDelegate.onDestroy(this);
        }
        super.onDestroy();
    }
}
