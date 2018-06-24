package com.tencent.plugin_loader_apk

import android.content.Context
import com.tencent.cubershi.plugin_loader.CuberPluginLoader
import com.tencent.cubershi.plugin_loader.managers.PluginActivitiesManager
import com.tencent.cubershi.plugin_loader.managers.PluginReceiverManager
import com.tencent.cubershi.plugin_loader.managers.PluginServicesManager

class DemoPluginLoader : CuberPluginLoader() {
    override val mAbi = "armeabi"
    private val mDemoPluginActivitiesManager = DemoPluginActivitiesManager()
    private val mDemoPluginServicesManager = DemoPluginServicesManager()
    private lateinit var mDemoPluginReceiverManager: DemoPluginReceiverManager
    private var mReceiverManagerInit = false

    override fun getBusinessPluginActivitiesManager(): PluginActivitiesManager {
        return mDemoPluginActivitiesManager
    }

    override fun getBusinessPluginServiceManager(): PluginServicesManager {
        return mDemoPluginServicesManager
    }

    override fun getBusinessPluginReceiverManger(hostAppContext: Context): PluginReceiverManager {
        if (!mReceiverManagerInit) {
            mDemoPluginReceiverManager = DemoPluginReceiverManager(hostAppContext)
        }
        return mDemoPluginReceiverManager
    }
}