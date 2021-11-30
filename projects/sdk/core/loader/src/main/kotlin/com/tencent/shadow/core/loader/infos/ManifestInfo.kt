package com.tencent.shadow.core.loader.infos

import android.content.IntentFilter

/**
 * 插件AndroidManifest.xml信息存储类
 *
 * @author xuedizi2009@163.com
 */
class ManifestInfo {
    val receivers = mutableListOf<Receiver>()

    class Receiver(val name: String) {
        var intents = mutableListOf<ReceiverIntentInfo>()

        fun actions(): MutableList<String> {
            val actions = mutableListOf<String>()
            this.intents.forEach { intentInfo ->
                intentInfo.actionsIterator().forEach { action ->
                    actions.add(action)
                }
            }
            return actions
        }
    }

    class ReceiverIntentInfo : IntentFilter()
}