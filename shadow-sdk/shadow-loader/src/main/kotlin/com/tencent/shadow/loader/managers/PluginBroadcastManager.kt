package com.tencent.shadow.loader.managers

/**
 * Created by tracyluo on 2018/6/19.
 */
abstract class PluginBroadcastManager {
    class BroadcastInfo(val className: String, val actions: Array<String>)

    private var application2broadcastInfo: MutableMap
    <String, MutableMap<String, List<String>>> = HashMap()
    abstract fun getBroadcastInfoList(application: String): List<BroadcastInfo>
    fun getBroadcastsByApplication(application: String):MutableMap<String, List<String>>{
        if (application2broadcastInfo[application] == null){
            application2broadcastInfo[application] = HashMap()
            val broadcastInfoList = getBroadcastInfoList(application)
            for (broadcastInfo in broadcastInfoList){
                application2broadcastInfo[application]!![broadcastInfo.className] =
                        broadcastInfo.actions.toList()
            }
        }
        return application2broadcastInfo[application]!!
    }
}