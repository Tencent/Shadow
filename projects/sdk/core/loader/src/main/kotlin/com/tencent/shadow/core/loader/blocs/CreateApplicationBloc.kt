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

package com.tencent.shadow.core.loader.blocs

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.res.Resources
import com.tencent.shadow.core.loader.classloaders.PluginClassLoader
import com.tencent.shadow.core.loader.exceptions.CreateApplicationException
import com.tencent.shadow.core.loader.infos.PluginInfo
import com.tencent.shadow.core.loader.managers.ComponentManager
import com.tencent.shadow.core.runtime.ShadowApplication
import com.tencent.shadow.core.runtime.remoteview.ShadowRemoteViewCreatorProvider

/**
 * 初始化插件Application类
 *
 * @author cubershi
 */
object CreateApplicationBloc {
    @Throws(CreateApplicationException::class)
    fun createShadowApplication(
            pluginClassLoader: PluginClassLoader,
            pluginInfo: PluginInfo,
            resources: Resources,
            hostAppContext: Context,
            componentManager: ComponentManager,
            remoteViewCreatorProvider: ShadowRemoteViewCreatorProvider?,
            applicationInfo: ApplicationInfo
    ): ShadowApplication {
        try {
            val appClassName = pluginInfo.applicationClassName
            val shadowApplication : ShadowApplication;
            shadowApplication = if (appClassName != null) {
                val appClass = pluginClassLoader.loadClass(appClassName)
                ShadowApplication::class.java.cast(appClass.newInstance())
            } else {
                object : ShadowApplication(){}
            }
            val partKey = pluginInfo.partKey
            shadowApplication.setPluginResources(resources)
            shadowApplication.setPluginClassLoader(pluginClassLoader)
            shadowApplication.setPluginComponentLauncher(componentManager)
            shadowApplication.setHostApplicationContextAsBase(hostAppContext)
            shadowApplication.setBroadcasts(componentManager.getBroadcastsByPartKey(partKey))
            shadowApplication.applicationInfo = applicationInfo
            shadowApplication.setBusinessName(pluginInfo.businessName)
            shadowApplication.setPluginPartKey(partKey)
            shadowApplication.remoteViewCreatorProvider = remoteViewCreatorProvider
            return shadowApplication
        } catch (e: Exception) {
            throw CreateApplicationException(e)
        }

    }
}
