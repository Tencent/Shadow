package com.tencent.cubershi.plugin_loader.infos

data class PluginInfo(val packageName: String, val applicationClassName: String, val launcherActivityClassName: String) {
    //todo cubershi 找个语法让外部查询这个mActivities不能修改它.
    internal val mActivities: MutableSet<PluginActivityInfo> = HashSet()

    fun putActivityInfo(pluginActivityInfo: PluginActivityInfo) {
        mActivities.add(pluginActivityInfo)
    }

}
