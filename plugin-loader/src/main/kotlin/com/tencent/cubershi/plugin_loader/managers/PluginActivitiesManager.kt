package com.tencent.cubershi.plugin_loader.managers

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.os.Bundle
import com.tencent.cubershi.mock_interface.MockActivity
import com.tencent.cubershi.plugin_loader.infos.PluginActivityInfo
import com.tencent.cubershi.plugin_loader.infos.PluginInfo

abstract class PluginActivitiesManager : MockActivity.PluginActivityLauncher {
    companion object {
        const val PLUGIN_LOADER_BUNDLE_KEY = "PLUGIN_LOADER_BUNDLE_KEY"
        const val PLUGIN_ACTIVITY_INFO_KEY = "PLUGIN_ACTIVITY_INFO_KEY"
        const val PLUGIN_ACTIVITY_CLASS_NAME_KEY = "PLUGIN_ACTIVITY_CLASS_NAME_KEY"
    }

    /**
     * key:插件ComponentName
     * value:壳子ComponentName
     */
    private val activitiesMap: MutableMap<ComponentName, ComponentName> = HashMap()

    /**
     * key:插件Activity类名
     * value:插件PackageName
     */
    private val packageNameMap: MutableMap<String, String> = HashMap()

    /**
     * key:插件ComponentName
     * value:PluginActivityInfo
     */
    private val activityInfoMap: MutableMap<ComponentName, PluginActivityInfo> = HashMap()


    fun addPluginApkInfo(pluginInfo: PluginInfo) {
        pluginInfo.mActivities.forEach {
            val componentName = ComponentName(pluginInfo.packageName, it.className)
            activitiesMap.put(componentName, onBindContainerActivity(componentName))
            packageNameMap.put(it.className, pluginInfo.packageName)
            activityInfoMap.put(componentName, it)
        }
    }

    /**
     * @param pluginActivity 插件Activity
     * @return 容器Activity
     */
    abstract fun onBindContainerActivity(pluginActivity: ComponentName): ComponentName

    private fun getContainerActivity(pluginActivity: ComponentName): ComponentName {
        return activitiesMap.get(pluginActivity)!!
    }

    override fun startActivity(context: Context, pluginIntent: Intent): Boolean {
        val className = pluginIntent.component.className
        val packageName = packageNameMap[className]
        if (packageName == null) {
            return false
        }
        pluginIntent.component = ComponentName(packageName, className)
        val containerActivity = getContainerActivity(pluginIntent.component)
        val containerActivityIntent = Intent(pluginIntent)
        containerActivityIntent.setComponent(containerActivity)

        val bundleForPluginLoader = Bundle()

        bundleForPluginLoader.putString(PLUGIN_ACTIVITY_CLASS_NAME_KEY, className)
        bundleForPluginLoader.putParcelable(PLUGIN_ACTIVITY_INFO_KEY, activityInfoMap[pluginIntent.component])

        containerActivityIntent.putExtra(PLUGIN_LOADER_BUNDLE_KEY, bundleForPluginLoader)
        if (context is Application) {
            containerActivityIntent.setFlags(FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(containerActivityIntent)
        return true
    }

    abstract val launcherActivity: ComponentName
}