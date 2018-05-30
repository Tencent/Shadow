package com.tencent.cubershi.plugin_loader.managers

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import com.tencent.cubershi.mock_interface.MockActivity
import com.tencent.cubershi.plugin_loader.infos.PluginInfo

class PluginActivitiesManager : MockActivity.PluginActivityLauncher {
    companion object {
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

    fun addPluginApkInfo(pluginInfo: PluginInfo) {
        val containerActivity = ComponentName("com.tencent.libexample", "com.tencent.hydevteam.pluginframework.plugincontainer.PluginContainerActivity")

        pluginInfo.mActivities.forEach {
            activitiesMap.put(ComponentName(pluginInfo.packageName, it.className), containerActivity)
            packageNameMap.put(it.className, pluginInfo.packageName)
        }
    }

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
        containerActivityIntent.putExtra(PLUGIN_ACTIVITY_CLASS_NAME_KEY, className)
        context.startActivity(containerActivityIntent)
        return true
    }
}