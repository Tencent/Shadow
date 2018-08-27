package com.tencent.shadow.loader.managers

import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import com.tencent.hydevteam.pluginframework.plugincontainer.HostActivityDelegator
import com.tencent.hydevteam.pluginframework.plugincontainer.PluginContainerActivity
import com.tencent.shadow.loader.infos.PluginActivityInfo
import com.tencent.shadow.loader.infos.PluginInfo
import com.tencent.shadow.runtime.ShadowContext

abstract class PluginActivitiesManager : ShadowContext.PluginActivityLauncher {
    companion object {
        val AVOID_CLASS_VERIFY_EXCEPTION = PluginContainerActivity::class
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
            activitiesMap[componentName] = onBindContainerActivity(componentName)
            packageNameMap[it.className] = pluginInfo.packageName
            activityInfoMap[componentName] = it
        }
    }

    /**
     * @param pluginActivity 插件Activity
     * @return 容器Activity
     */
    abstract fun onBindContainerActivity(pluginActivity: ComponentName): ComponentName

    private fun getContainerActivity(pluginActivity: ComponentName): ComponentName =
            activitiesMap[pluginActivity]!!

    override fun startActivity(shadowContext: ShadowContext, pluginIntent: Intent): Boolean {
        return if (pluginIntent.isPluginComponent()) {
            shadowContext.superStartActivity(pluginIntent.toContainerIntent())
            true
        } else {
            false
        }
    }

    override fun startActivityForResult(delegator: HostActivityDelegator, pluginIntent: Intent, requestCode: Int): Boolean {
        return if (pluginIntent.isPluginComponent()) {
            delegator.startActivityForResult(pluginIntent.toContainerIntent(), requestCode)
            true
        } else {
            false
        }
    }

    fun convertActivityIntent(pluginIntent: Intent): Intent {
        return if (pluginIntent.isPluginComponent()) {
            pluginIntent.toContainerIntent()
        } else {
            pluginIntent
        }
    }

    private fun Intent.isPluginComponent(): Boolean {
        if (component == null) {
            return false
        }
        val className = component.className ?: return false
        return packageNameMap.containsKey(className)
    }

    /**
     * 构造pluginIntent对应的ContainerIntent
     * 调用前必须先调用isPluginComponent判断pluginIntent确实一个插件内的组件
     */
    private fun Intent.toContainerIntent(): Intent {
        val className = component.className
        component = ComponentName(packageNameMap[className], className)
        val containerActivity = getContainerActivity(component)
        val containerActivityIntent = Intent(this)
        containerActivityIntent.component = containerActivity

        val bundleForPluginLoader = Bundle()

        bundleForPluginLoader.putString(PLUGIN_ACTIVITY_CLASS_NAME_KEY, className)
        bundleForPluginLoader.putParcelable(PLUGIN_ACTIVITY_INFO_KEY, activityInfoMap[component])

        containerActivityIntent.putExtra(PLUGIN_LOADER_BUNDLE_KEY, bundleForPluginLoader)
        return containerActivityIntent
    }

    abstract val launcherActivity: ComponentName
}