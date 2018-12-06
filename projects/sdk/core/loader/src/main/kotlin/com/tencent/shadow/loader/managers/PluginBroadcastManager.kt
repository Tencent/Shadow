package com.tencent.shadow.loader.managers

/**
 * Created by tracyluo on 2018/6/19.
 */
abstract class PluginBroadcastManager {
    class BroadcastInfo(val className: String, val actions: Array<String>)

    private var application2broadcastInfo: MutableMap
    <String, MutableMap<String, List<String>>> = HashMap()

    abstract fun getBroadcastInfoList(partKey: String): List<BroadcastInfo>?
    fun getBroadcastsByPartKey(partKey: String): MutableMap<String, List<String>> {
        if (application2broadcastInfo[partKey] == null) {
            application2broadcastInfo[partKey] = HashMap()
            val broadcastInfoList = getBroadcastInfoList(partKey)
            if (broadcastInfoList != null) {
                for (broadcastInfo in broadcastInfoList) {
                    application2broadcastInfo[partKey]!![broadcastInfo.className] =
                            broadcastInfo.actions.toList()
                }
            }
        }
        return application2broadcastInfo[partKey]!!
    }
}