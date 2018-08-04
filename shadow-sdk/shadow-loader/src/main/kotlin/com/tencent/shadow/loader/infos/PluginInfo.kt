package com.tencent.shadow.loader.infos

import android.os.Bundle

data class PluginInfo(val packageName: String, val applicationClassName: String) {
    //todo cubershi 找个语法让外部查询这个mActivities不能修改它.
    internal val mActivities: MutableSet<PluginActivityInfo> = HashSet()
    var metaData: Bundle? = null
    var versionCode :Int = 0;
    var versionName :String? =null;
    var firstInstallTime: Long? = null

    internal val mServices: MutableSet<PluginServiceInfo> = HashSet()


    fun putActivityInfo(pluginActivityInfo: PluginActivityInfo) {
        mActivities.add(pluginActivityInfo)
    }

    fun putServiceInfo(pluginServiceInfo: PluginServiceInfo) {
        mServices.add(pluginServiceInfo)
    }
}
