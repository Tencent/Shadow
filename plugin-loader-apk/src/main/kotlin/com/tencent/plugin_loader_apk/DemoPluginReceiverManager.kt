package com.tencent.plugin_loader_apk

import android.content.Context
import com.tencent.shadow.pluginloader.managers.PluginReceiverManager

/**
 * Created by tracyluo on 2018/6/19.
 */
class DemoPluginReceiverManager(mTotalContext: Context) : PluginReceiverManager(mTotalContext) {
    init{
        initReceiverInfo(listOf(ReceiverInfo("testBroadCast", "com.example.receiver.MyReceiver", "com.example.android.basicglsurfaceview.MyApplication")))
    }
}