package com.tencent.plugin_loader_apk

import com.tencent.cubershi.plugin_loader.CuberPluginLoader
import com.tencent.cubershi.plugin_loader.managers.PluginActivitiesManager
import com.tencent.cubershi.plugin_loader.managers.PluginServicesManager

class DemoPluginLoader : CuberPluginLoader() {
    override val mAbi = "armeabi"
    private val mDemoPluginActivitiesManager = DemoPluginActivitiesManager()
    private val mDemoPluginServicesManager = DemoPluginServicesManager()

    override fun getBusinessPluginActivitiesManager(): PluginActivitiesManager {
        return mDemoPluginActivitiesManager
    }

    override fun getBusinessPluginServiceManager(): PluginServicesManager {
        return mDemoPluginServicesManager
    }
}