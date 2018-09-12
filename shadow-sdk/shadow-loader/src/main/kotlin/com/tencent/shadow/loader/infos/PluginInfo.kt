package com.tencent.shadow.loader.infos

import android.os.Bundle

data class PluginInfo(val partKey: String,
                      val packageName: String,
                      val applicationClassName: String
) {
    private val _mActivities: MutableSet<PluginActivityInfo> = HashSet()
    private val _mServices: MutableSet<PluginServiceInfo> = HashSet()
    internal val mActivities: Set<PluginActivityInfo>
        get() = _mActivities
    internal val mServices: Set<PluginServiceInfo>
        get() = _mServices
    var metaData: Bundle? = null
    var versionCode :Int = 0;
    var versionName :String? =null;
    var firstInstallTime: Long? = null


    fun putActivityInfo(pluginActivityInfo: PluginActivityInfo) {
        _mActivities.add(pluginActivityInfo)
    }

    fun putServiceInfo(pluginServiceInfo: PluginServiceInfo) {
        _mServices.add(pluginServiceInfo)
    }
}
