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

package com.tencent.shadow.test.dynamic.manager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.tencent.shadow.core.manager.installplugin.InstalledPlugin;
import com.tencent.shadow.dynamic.host.EnterCallback;
import com.tencent.shadow.test.cases.PluginServiceConnectionTestCase;
import com.tencent.shadow.test.lib.constant.Constant;
import com.tencent.shadow.test.lib.test_manager.TestManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 针对plugin-service-for-host插件
 */
public class ServiceTestDynamicPluginManager extends FastPluginManager {

    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    private Context mCurrentContext;

    public ServiceTestDynamicPluginManager(Context context) {
        super(context);
        mCurrentContext = context;
    }

    /**
     * @return PluginManager实现的别名，用于区分不同PluginManager实现的数据存储路径
     */
    @Override
    protected String getName() {
        return "service-test-dynamic-manager";
    }

    /**
     * @return 宿主中注册的PluginProcessService实现的类名
     */
    @Override
    protected String getPluginProcessServiceName() {
        return "com.tencent.shadow.test.dynamic.host.PluginServiceProcessPPS";
    }

    @Override
    public void enter(final Context context, long fromId, Bundle bundle, final EnterCallback callback) {
        if (fromId == Constant.FROM_ID_BIND_SERVICE) {
            onBindService(context, bundle, callback);
        } else {
            throw new IllegalArgumentException("不认识的fromId==" + fromId);
        }
    }

    private void doCase(Intent pluginIntent) throws InterruptedException {
        String className = pluginIntent.getComponent().getClassName();
        switch (className) {
            case "com.tencent.shadow.test.plugin.particular_cases.plugin_service_for_host.SystemExitService":
                PluginServiceConnectionTestCase systemExitServiceCase = new PluginServiceConnectionTestCase(mPluginLoader, pluginIntent);
                systemExitServiceCase.prepareUi();
                break;
            case "com.tencent.shadow.test.plugin.particular_cases.plugin_service_for_host.SystemExitIntentService":
                PluginServiceConnectionTestCase systemExitIntentService = new PluginServiceConnectionTestCase(mPluginLoader, pluginIntent);
                systemExitIntentService.prepareUi();
                break;
            default:
                throw new IllegalArgumentException(className + "没有对应的PluginServiceConnection");
        }
    }

    private void onBindService(final Context context, Bundle bundle, final EnterCallback callback) {
        final String pluginZipPath = bundle.getString(Constant.KEY_PLUGIN_ZIP_PATH);
        final String partKey = bundle.getString(Constant.KEY_PLUGIN_PART_KEY);
        final String className = bundle.getString(Constant.KEY_ACTIVITY_CLASSNAME);
        if (className == null) {
            throw new NullPointerException("className == null");
        }
        final Bundle extras = bundle.getBundle(Constant.KEY_EXTRAS);

        if (callback != null) {
            final View view = LayoutInflater.from(mCurrentContext).inflate(R.layout.activity_load_plugin, null);
            callback.onShowLoadingView(view);
        }

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    InstalledPlugin installedPlugin = installPlugin(pluginZipPath, null, true);

                    TestManager.uuid = installedPlugin.UUID;

                    loadPlugin(installedPlugin.UUID, partKey);

                    Intent pluginIntent = new Intent();
                    pluginIntent.setClassName(
                            context.getPackageName(),
                            className
                    );
                    if (extras != null) {
                        pluginIntent.replaceExtras(extras);
                    }

                    doCase(pluginIntent);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                if (callback != null) {
                    callback.onCloseLoadingView();
                }
            }
        });

    }
}
