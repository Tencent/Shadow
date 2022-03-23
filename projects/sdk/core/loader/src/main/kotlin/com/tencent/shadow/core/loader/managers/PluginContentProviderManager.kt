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

package com.tencent.shadow.core.loader.managers

import android.content.ContentProvider
import android.content.Context
import android.content.pm.ProviderInfo
import android.net.Uri
import android.os.Bundle
import com.tencent.shadow.core.loader.infos.ContainerProviderInfo
import com.tencent.shadow.core.loader.infos.PluginParts
import com.tencent.shadow.core.runtime.PluginManifest
import com.tencent.shadow.core.runtime.UriConverter
import java.util.*
import kotlin.collections.HashSet
import kotlin.collections.set

class PluginContentProviderManager() : UriConverter.UriParseDelegate {

    /**
     * key : pluginAuthority
     * value : plugin ContentProvider
     */
    private val providerMap = HashMap<String, ContentProvider>()

    /**
     * key : plugin Authority
     * value :  containerProvider Authority
     */
    private val providerAuthorityMap = HashMap<String, String>()


    private val pluginProviderInfoMap = HashMap<String, HashSet<PluginManifest.ProviderInfo>?>()


    override fun parse(uriString: String): Uri {
        if (uriString.startsWith(CONTENT_PREFIX)) {
            val uriContent = uriString.substring(CONTENT_PREFIX.length)
            val index = uriContent.indexOf("/")
            val originalAuthority = if (index != -1) uriContent.substring(0, index) else uriContent
            val containerAuthority = getContainerProviderAuthority(originalAuthority)
            if (containerAuthority != null) {
                return Uri.parse("$CONTENT_PREFIX$containerAuthority/$uriContent")
            }
        }
        return Uri.parse(uriString)
    }

    override fun parseCall(uriString: String, extra: Bundle): Uri {
        val pluginUri = parse(uriString)
        extra.putString(SHADOW_BUNDLE_KEY, pluginUri.toString())
        return pluginUri
    }

    fun addContentProviderInfo(
        partKey: String,
        pluginProviderInfo: PluginManifest.ProviderInfo,
        containerProviderInfo: ContainerProviderInfo
    ) {
        if (providerMap.containsKey(pluginProviderInfo.authorities)) {
            throw RuntimeException("重复添加 ContentProvider")
        }

        providerAuthorityMap[pluginProviderInfo.authorities] = containerProviderInfo.authority
        var pluginProviderInfos: HashSet<PluginManifest.ProviderInfo>? = null
        if (pluginProviderInfoMap.containsKey(partKey)) {
            pluginProviderInfos = pluginProviderInfoMap[partKey]
        } else {
            pluginProviderInfos = HashSet()
        }
        pluginProviderInfos?.add(pluginProviderInfo)
        pluginProviderInfoMap.put(partKey, pluginProviderInfos)
    }

    fun createContentProviderAndCallOnCreate(
        context: Context,
        partKey: String,
        pluginParts: PluginParts?
    ) {
        pluginProviderInfoMap[partKey]?.forEach {
            try {
                val contentProvider = pluginParts!!.appComponentFactory
                    .instantiateProvider(pluginParts.classLoader, it.className)

                //convert PluginManifest.ProviderInfo to android.content.pm.ProviderInfo
                val providerInfo = ProviderInfo()
                providerInfo.packageName = context.packageName
                providerInfo.name = it.className
                providerInfo.authority = it.authorities
                providerInfo.grantUriPermissions = it.grantUriPermissions
                contentProvider?.attachInfo(context, providerInfo)
                providerMap[it.authorities] = contentProvider
            } catch (e: Exception) {
                throw RuntimeException(
                    "partKey==$partKey className==${it.className} authorities==${it.authorities}",
                    e
                )
            }
        }

    }

    fun getPluginContentProvider(pluginAuthority: String): ContentProvider? {
        return providerMap[pluginAuthority]
    }

    fun getContainerProviderAuthority(pluginAuthority: String): String? {
        return providerAuthorityMap[pluginAuthority]
    }

    fun getAllContentProvider(): Set<ContentProvider> {
        val contentProviders = hashSetOf<ContentProvider>()
        providerMap.keys.forEach {
            contentProviders.add(providerMap[it]!!)
        }
        return contentProviders
    }

    fun convert2PluginUri(uri: Uri): Uri {
        val containerAuthority: String? = uri.authority
        if (!providerAuthorityMap.values.contains(containerAuthority)) {
            throw IllegalArgumentException("不能识别的uri Authority:$containerAuthority")
        }
        val uriString = uri.toString()
        return Uri.parse(uriString.replace("$containerAuthority/", ""))
    }

    fun convert2PluginUri(extra: Bundle): Uri {
        val uriString = extra.getString(SHADOW_BUNDLE_KEY)
        extra.remove(SHADOW_BUNDLE_KEY)
        return convert2PluginUri(Uri.parse(uriString))
    }

    companion object {

        private val CONTENT_PREFIX = "content://"
        private val SHADOW_BUNDLE_KEY = "shadow_cp_bundle_key"
    }


}
