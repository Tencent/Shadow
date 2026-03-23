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
        val matchAuthorityMap = providerAuthorityMap.filter { it.value == containerAuthority }
        if (matchAuthorityMap.isEmpty()) {
            throw IllegalArgumentException("不能识别的uri Authority:$containerAuthority")
        }
        val uriString = uri.toString()
        for (entry in matchAuthorityMap) {
            val pluginAuthority = entry.key
            // 通过正则表达式去除 containerAuthority ，支持以下场景：
            // 1. content://containerAuthority/pluginAuthority（插件内部调用 insert 、query 等方法）
            // 2. content://containerAuthority/containerAuthority/pluginAuthority（插件内部调用 call 方法）
            // 3. content://containerAuthority （外部应用调用 content provider 方法且 containerAuthority == pluginAuthority）
            // 正则表达式结构分解：
            //   - ^content://：
            //       - 作用：强制从字符串的最开始进行匹配。
            //       - 目的：确保只处理标准的 content 协议 URI。
            //   - ((?:$escapedContainer/)*)（捕获组 1）：
            //       - $escapedContainer/：这是经过 Regex.escape() 处理后的容器 Authority 字符串，后面紧跟一个斜杠。转义确保了如 a.b 中的点号不会匹配任意字符。
            //       - (?:...)：非捕获组，仅用于将“容器名+斜杠”作为一个整体进行多次匹配。
            //       - *（贪婪匹配）：匹配零个或多个连续的容器前缀。使用贪婪模式是为了在 containerAuthority 和 pluginAuthority 相同的情况下（如content://A/A/path），尽可能多地剥离外层容器，只留下最后一个作为插件标识。
            //   - $escapedPlugin：
            //       - 作用：匹配插件真实的 Authority 。它是整个正则的锚点，用于确定这个 URI 属于哪个插件。
            //   - (?=/|$)（正向肯定预查）：
            //       - 作用：这是一个非占位匹配，要求匹配到的 pluginAuthority 后面必须紧跟一个斜杠 /（表示路径开始）或者字符串结束符 $ 。
            //       - 目的：防止部分匹配。例如，如果 pluginAuthority 是 A，而 URI 是 content://Ab/path，如果没有这个预查，正则会错误地匹配到 Ab 。
            val escapedContainer = Regex.escape(containerAuthority!!)
            val escapedPlugin = Regex.escape(pluginAuthority)
            val regex = Regex("^$CONTENT_PREFIX((?:$escapedContainer/)*)$escapedPlugin(?=/|$)")

            // 可能存在一个 containerAuthority 匹配多个 pluginAuthority 的场景，所以存在无法匹配的场景
            val matchResult = regex.find(uriString) ?: continue
            // 如果找到了匹配的内容，则剔除匹配的 containerAuthority 内容
            val range = matchResult.groups[1]!!.range
            return Uri.parse(
                uriString.substring(0, range.first) + uriString.substring(range.last + 1)
            )
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
