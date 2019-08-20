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

package com.tencent.shadow.core.loader.remoteview

import android.content.Context
import android.view.View
import com.tencent.shadow.core.loader.ShadowPluginLoader
import com.tencent.shadow.core.runtime.ShadowContext
import com.tencent.shadow.core.runtime.remoteview.ShadowRemoteViewCreateCallback
import com.tencent.shadow.core.runtime.remoteview.ShadowRemoteViewCreateException
import com.tencent.shadow.core.runtime.remoteview.ShadowRemoteViewCreator

internal class ShadowRemoteViewCreatorImp(private val context: Context, private val shadowPluginLoader: ShadowPluginLoader) : ShadowRemoteViewCreator {


    @Throws(ShadowRemoteViewCreateException::class)
    override fun createView(partKey: String, viewClass: String): View {

        val pluginParts = shadowPluginLoader.getPluginParts(partKey)

        if (pluginParts != null) {

            try {
                val clazz = pluginParts.classLoader.loadClass(viewClass)

                val constructor = clazz.getConstructor(Context::class.java)

                // 构造context
                val shadowContext = ShadowContext(context, 0)
                shadowContext.setPluginClassLoader(pluginParts.classLoader)
                shadowContext.setPluginComponentLauncher(shadowPluginLoader.mComponentManager)
                shadowContext.setBusinessName(pluginParts.businessName)
                shadowContext.setPluginPartKey(partKey)
                shadowContext.setPluginResources(pluginParts.resources)
                shadowContext.setShadowApplication(pluginParts.application)
                shadowContext.applicationInfo = pluginParts.application.applicationInfo

                val view = View::class.java.cast(constructor.newInstance(shadowContext))

                return view

            } catch (e: Exception) {
                throw ShadowRemoteViewCreateException("创建 $viewClass 失败", e)
            }

        } else {
            throw ShadowRemoteViewCreateException("创建 $viewClass 失败，插件(partKey:$partKey)不存在或者还未加载")
        }

    }


    override fun createView(partKey: String, viewClass: String, callback: ShadowRemoteViewCreateCallback?) {
        if (callback != null) {
            callback.onViewCreateFailed(java.lang.Exception("创建View失败，createView(String apkKey, String viewClass, ShadowRemoteViewCreateCallback callback)暂未实现！"))
        }
    }
}