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

package com.tencent.shadow.core.loader.delegates

import android.content.res.Resources
import com.tencent.shadow.core.loader.classloaders.PluginClassLoader
import com.tencent.shadow.core.loader.managers.ComponentManager
import com.tencent.shadow.core.runtime.ShadowApplication
import com.tencent.shadow.core.runtime.remoteview.ShadowRemoteViewCreatorProvider

abstract class ShadowDelegate() {

    fun inject(shadowApplication: ShadowApplication) {
        _pluginApplication = shadowApplication
    }

    fun inject(pluginClassLoader: PluginClassLoader) {
        _pluginClassLoader = pluginClassLoader
    }

    fun inject(resources: Resources) {
        _pluginResources = resources
    }

    fun inject(componentManager: ComponentManager) {
        _componentManager = componentManager
    }

    fun inject(remoteViewCreatorProvider: ShadowRemoteViewCreatorProvider) {
        _remoteViewCreatorProvider = remoteViewCreatorProvider
    }

    private lateinit var _pluginApplication: ShadowApplication
    private lateinit var _pluginClassLoader: PluginClassLoader
    private lateinit var _pluginResources: Resources
    private lateinit var _componentManager: ComponentManager
    private lateinit var _remoteViewCreatorProvider: ShadowRemoteViewCreatorProvider

    protected val mPluginApplication: ShadowApplication
        get() = _pluginApplication
    protected val mPluginClassLoader: PluginClassLoader
        get() = _pluginClassLoader
    protected val mPluginResources: Resources
        get() = _pluginResources
    protected val mComponentManager: ComponentManager
        get() = _componentManager

    protected val mRemoteViewCreatorProvider: ShadowRemoteViewCreatorProvider
        get() = _remoteViewCreatorProvider

}