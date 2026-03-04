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
        containerProviderInfo: ContainerProviderInfo,
        pluginAuthority: String
    ) {
        if (providerMap.containsKey(pluginAuthority)) {
            throw RuntimeException("重复添加 ContentProvider")
        }

        providerAuthorityMap[pluginAuthority] = containerProviderInfo.authority
        var pluginProviderInfos: HashSet<PluginManifest.ProviderInfo>?
        if (pluginProviderInfoMap.containsKey(partKey)) {
            pluginProviderInfos = pluginProviderInfoMap[partKey]
        } else {
            pluginProviderInfos = HashSet()
            pluginProviderInfoMap[partKey] = pluginProviderInfos
        }
        pluginProviderInfos?.add(pluginProviderInfo)
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
                it.authorities
                    .split(";")
                    .filter { authority -> authority.isNotBlank() }
                    .forEach { authority -> providerMap[authority] = contentProvider }
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
        val set = providerAuthorityMap.filter { it.value == containerAuthority }
        if (set.isEmpty()) {
            throw IllegalArgumentException("不能识别的uri Authority:$containerAuthority")
        }
        val uriString = uri.toString()
        for (entry in set) {
            val pluginAuthority = entry.key
            // 通过正则表达式去除 containerAuthority ，支持以下场景：
            // 1. content://containerAuthority/pluginAuthority（插件内部调用 insert 、query 等方法）
            // 2. content://containerAuthority/containerAuthority/pluginAuthority（插件内部调用 call 方法）
            // 3. content://pluginAuthority （外部应用调用 content provider 方法）
            // 正则表达式分为三个部分：
            // 1. `^$CONTENT_PREFIX`: 匹配开头的 "content://"。
            // 2. `((?:$containerAuthority/)+)`: 第一个捕获组。它匹配并捕获零个或多个 "containerAuthority/" 组成的连续前缀。
            //    - `(?:...)` 是一个非捕获组，仅用于组合。
            // 3. $pluginAuthority 是 pluginAuthority ，用于作为删除的锚点。
            val regex = Regex("^$CONTENT_PREFIX((?:$containerAuthority/)?)$pluginAuthority")
            // 可能存在一个 containerAuthority 匹配多个 pluginAuthority 的场景，所以存在无法匹配的场景
            val matchResult = regex.find(uriString) ?: continue
            // 如果找到了匹配的内容，则剔除匹配的 containerAuthority 内容
            val range = matchResult.groups[1]!!.range
            return Uri.parse(uriString.substring(0, range.first) + uriString.substring(range.last + 1))
        }
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
