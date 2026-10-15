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

package com.tencent.shadow.test.dynamic.loader;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ActivityInfo;

import com.tencent.shadow.core.loader.infos.ContainerActivityInfo;
import com.tencent.shadow.core.loader.infos.ContainerProviderInfo;
import com.tencent.shadow.core.loader.managers.ComponentManager;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TestComponentManager extends ComponentManager {

    /**
     * dynamic-runtime-apk 模块中定义的壳子Activity，需要在宿主AndroidManifest.xml注册
     */
    private static final String DEFAULT_ACTIVITY = "com.tencent.shadow.test.dynamic.runtime.container.PluginDefaultProxyActivity";
    private static final String SINGLE_INSTANCE_ACTIVITY = "com.tencent.shadow.test.dynamic.runtime.container.PluginSingleInstance1ProxyActivity";
    private static final String SINGLE_TASK_ACTIVITY = "com.tencent.shadow.test.dynamic.runtime.container.PluginSingleTask1ProxyActivity";
    private static final String INDEPENDENT_TASK_ACTIVITY_1 = "com.tencent.shadow.test.dynamic.runtime.container.PluginIndependentTask1ProxyActivity";
    private static final String INDEPENDENT_TASK_ACTIVITY_2 = "com.tencent.shadow.test.dynamic.runtime.container.PluginIndependentTask2ProxyActivity";
    private static final String CONTAINER_SERVICE = "com.tencent.shadow.core.runtime.container.PluginContainerService";

    private Context context;

    public TestComponentManager(Context context) {
        this.context = context;
    }


    /**
     * 配置插件Activity 到 壳子Activity的对应关系
     *
     * @param pluginActivity 插件Activity
     * @return 壳子Activity
     */
    @Override
    public ComponentName onBindContainerActivity(ComponentName pluginActivity) {
        switch (pluginActivity.getClassName()) {
            /**
             * 这里配置对应的对应关系
             */
        }
        return new ComponentName(context, DEFAULT_ACTIVITY);
    }

    /**
     * 独立Task容器池，launchMode和taskAffinity需与宿主Manifest中的声明一致。
     */
    @Override
    public List<ContainerActivityInfo> getContainerActivityPool() {
        String packageName = context.getPackageName();
        return Arrays.asList(
                new ContainerActivityInfo(
                        new ComponentName(context, INDEPENDENT_TASK_ACTIVITY_1),
                        ActivityInfo.LAUNCH_SINGLE_TASK,
                        packageName + ".shadow_task_1"),
                new ContainerActivityInfo(
                        new ComponentName(context, INDEPENDENT_TASK_ACTIVITY_2),
                        ActivityInfo.LAUNCH_SINGLE_TASK,
                        packageName + ".shadow_task_2")
        );
    }

    /**
     * 插件前台Service容器，多个插件前台Service共享同一个宿主容器。
     */
    @Override
    public List<ComponentName> getContainerServicePool() {
        return Collections.singletonList(new ComponentName(context, CONTAINER_SERVICE));
    }

    /**
     * 配置对应宿主中预注册的壳子contentProvider的信息
     */
    @Override
    public ContainerProviderInfo onBindContainerContentProvider(ComponentName pluginContentProvider) {
        return new ContainerProviderInfo(
                "com.tencent.shadow.core.runtime.container.PluginContainerContentProvider",
                context.getPackageName() + ".contentprovider.authority.dynamic");
    }

}
