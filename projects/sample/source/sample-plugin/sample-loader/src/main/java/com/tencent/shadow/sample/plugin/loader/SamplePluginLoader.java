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

package com.tencent.shadow.sample.plugin.loader;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.res.Resources;

import com.tencent.shadow.core.common.InstalledApk;
import com.tencent.shadow.core.load_parameters.LoadParameters;
import com.tencent.shadow.core.loader.ShadowPluginLoader;
import com.tencent.shadow.core.loader.classloaders.PluginClassLoader;
import com.tencent.shadow.core.loader.exceptions.LoadPluginException;
import com.tencent.shadow.core.loader.infos.PluginParts;
import com.tencent.shadow.core.loader.managers.ComponentManager;
import com.tencent.shadow.sample.host.lib.LoadPluginCallback;

import java.util.concurrent.Future;

import static android.content.pm.PackageManager.GET_META_DATA;

public class SamplePluginLoader extends ShadowPluginLoader {

    private final static String TAG = "shadow";

    private ComponentManager componentManager;

    public SamplePluginLoader(Context hostAppContext) {
        super(hostAppContext);
        componentManager = new SampleComponentManager(hostAppContext);
    }

    @Override
    public ComponentManager getComponentManager() {
        return componentManager;
    }

    @Override
    public Future<?> loadPlugin(final InstalledApk installedApk) throws LoadPluginException {
        LoadParameters loadParameters = getLoadParameters(installedApk);
        final String partKey = loadParameters.partKey;

        LoadPluginCallback.getCallback().beforeLoadPlugin(partKey);

        final Future<?> future = super.loadPlugin(installedApk);

        getMExecutorService().submit(new Runnable() {
            @Override
            public void run() {
                try {
                    future.get();
                    PluginParts pluginParts = getPluginParts(partKey);
                    String packageName = pluginParts.getApplication().getPackageName();
                    ApplicationInfo applicationInfo = pluginParts.getPluginPackageManager().getApplicationInfo(packageName, GET_META_DATA);
                    PluginClassLoader classLoader = pluginParts.getClassLoader();
                    Resources resources = pluginParts.getResources();

                    LoadPluginCallback.getCallback().afterLoadPlugin(partKey, applicationInfo, classLoader, resources);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });

        return future;
    }

    @Override
    public String getDelegateProviderKey() {
        return "SAMPLE";
    }
}
