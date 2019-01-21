package com.tencent.shadow.core.loader.managers

import android.content.ContentProvider
import android.content.Context
import android.net.Uri

import com.tencent.shadow.core.loader.infos.ContainerProviderInfo
import com.tencent.shadow.core.loader.infos.PluginParts
import com.tencent.shadow.core.loader.infos.PluginProviderInfo
import com.tencent.shadow.runtime.UriParseDelegate
import java.lang.IllegalArgumentException

import java.util.HashMap

class PluginContentProviderManager() : UriParseDelegate {

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


    private val pluginProviderInfoMap = HashMap<String, HashSet<PluginProviderInfo>?>()


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


    fun addContentProviderInfo(partKey: String, pluginProviderInfo: PluginProviderInfo, containerProviderInfo: ContainerProviderInfo) {
        if (providerMap.containsKey(pluginProviderInfo.authority)) {
            throw RuntimeException("重复添加 ContentProvider")
        }

        providerAuthorityMap[pluginProviderInfo.authority] = containerProviderInfo.authority
        var pluginProviderInfos: HashSet<PluginProviderInfo>? = null
        if (pluginProviderInfoMap.containsKey(partKey)) {
            pluginProviderInfos = pluginProviderInfoMap[partKey]
        } else {
            pluginProviderInfos = HashSet()
        }
        pluginProviderInfos?.add(pluginProviderInfo)
        pluginProviderInfoMap.put(partKey, pluginProviderInfos)


    }

    fun createContentProviderAndCallOnCreate(mContext: Context, partKey: String, pluginParts: PluginParts?) {
        pluginProviderInfoMap[partKey]?.forEach {
            try {
                val clz = pluginParts!!.classLoader.loadClass(it.className)
                val contentProvider = ContentProvider::class.java.cast(clz.newInstance())
                contentProvider?.attachInfo(mContext, it.providerInfo)
                contentProvider!!.onCreate()
                providerMap[it.authority] = contentProvider
            } catch (e: Exception) {
                throw RuntimeException(e)
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

    companion object {

        private val CONTENT_PREFIX = "content://"

    }


}
