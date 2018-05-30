package com.tencent.cubershi.plugin_loader.managers

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import com.tencent.cubershi.mock_interface.MockActivity
import com.tencent.cubershi.plugin_loader.infos.ApkInfo
import com.tencent.cubershi.plugin_loader.test.FakeRunningPlugin

class PluginActivitiesManager : MockActivity.PluginActivityLauncher {

    val activityMap: MutableMap<ComponentName, ComponentName> = HashMap()

    fun addPluginApkInfo(apkInfo: ApkInfo) {
        val pluginActivity1 = ComponentName("com.example.android.basicglsurfaceview", "com.example.android.basicglsurfaceview.BasicGLSurfaceViewActivity")
        val pluginActivity2 = ComponentName("com.example.android.basicglsurfaceview", "com.example.android.basicglsurfaceview.TestSoLoadActivity")
        val containerActivity = ComponentName("com.tencent.libexample", "com.tencent.hydevteam.pluginframework.plugincontainer.PluginContainerActivity")

        activityMap.put(pluginActivity1, containerActivity)
        activityMap.put(pluginActivity2, containerActivity)

    }

    private fun getContainerActivity(pluginActivity: ComponentName): ComponentName? {
        return activityMap.get(pluginActivity)
    }

    override fun startActivity(context: Context, pluginIntent: Intent): Boolean {
        val containerActivity = getContainerActivity(pluginIntent.component)
        if (containerActivity == null) {
            return false
        } else {
            val containerActivityIntent = Intent(pluginIntent)
            containerActivityIntent.setComponent(containerActivity)
            containerActivityIntent.putExtra(FakeRunningPlugin.ARG, pluginIntent.component.className)
            context.startActivity(containerActivityIntent)
            return true
        }
    }
}