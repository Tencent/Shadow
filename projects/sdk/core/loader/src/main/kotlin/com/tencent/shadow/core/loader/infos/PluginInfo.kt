package com.tencent.shadow.core.loader.infos

import android.content.pm.Signature
import android.os.Bundle

class PluginInfo(
        val partKey: String,
        val packageName: String,
        val applicationClassName: String,
        val metaData: Bundle?,
        val versionCode: Int,
        val versionName: String,
        val signatures: Array<Signature>
) {
    private val _mActivities: MutableSet<PluginActivityInfo> = HashSet()
    private val _mServices: MutableSet<PluginServiceInfo> = HashSet()
    private val _mProviders: MutableSet<PluginProviderInfo> = HashSet()
    internal val mActivities: Set<PluginActivityInfo>
        get() = _mActivities
    internal val mServices: Set<PluginServiceInfo>
        get() = _mServices
    internal val mProviders: Set<PluginProviderInfo>
        get() = _mProviders


    fun putActivityInfo(pluginActivityInfo: PluginActivityInfo) {
        _mActivities.add(pluginActivityInfo)
    }

    fun putServiceInfo(pluginServiceInfo: PluginServiceInfo) {
        _mServices.add(pluginServiceInfo)
    }

    fun putPluginProviderInfo(pluginProviderInfo: PluginProviderInfo) {
        _mProviders.add(pluginProviderInfo)
    }
}
