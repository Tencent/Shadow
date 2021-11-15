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

package com.tencent.shadow.test.none_dynamic.host;

import android.content.ComponentName;

import com.tencent.shadow.core.loader.infos.ContainerProviderInfo;
import com.tencent.shadow.core.loader.managers.ComponentManager;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class TestComponentManager extends ComponentManager {
    final private static ComponentName sDefaultContainer = new ComponentName(BuildConfig.APPLICATION_ID, "com.tencent.shadow.test.none_dynamic.host.DefaultContainerActivity");
    final private static ComponentName sSingleTaskContainer = new ComponentName(BuildConfig.APPLICATION_ID, "com.tencent.shadow.test.none_dynamic.host.SingleTaskContainerActivity");
    public static final ArrayList<String> sActivities = new ArrayList<>();

    @Override
    public ComponentName onBindContainerActivity(ComponentName componentName) {
        if (!sActivities.contains(componentName.getClassName())) {
            sActivities.add(componentName.getClassName());
        }
        if (componentName.getClassName().equals("com.tencent.shadow.test.plugin.general_cases.lib.usecases.activity.TestActivityOrientation")) {
            return sSingleTaskContainer;
        }
        return sDefaultContainer;
    }


    @NotNull
    @Override
    public ContainerProviderInfo onBindContainerContentProvider(@NotNull ComponentName pluginContentProvider) {
        return new ContainerProviderInfo("com.tencent.shadow.core.runtime.container.PluginContainerContentProvider", "com.tencent.shadow.contentprovider.authority");
    }

}
