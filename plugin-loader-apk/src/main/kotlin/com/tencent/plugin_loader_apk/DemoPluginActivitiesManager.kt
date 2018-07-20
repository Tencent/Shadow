package com.tencent.plugin_loader_apk

import android.content.ComponentName
import com.tencent.shadow.pluginloader.managers.PluginActivitiesManager

class DemoPluginActivitiesManager : PluginActivitiesManager() {

    val launcherActivityClassName = "com.example.android.basicglsurfaceview.BasicGLSurfaceViewActivity"

    lateinit var _LauncherActivity: ComponentName

    override val launcherActivity: ComponentName
        get() = _LauncherActivity

    override fun onBindContainerActivity(pluginActivity: ComponentName): ComponentName {
        val containerActivity = ComponentName("com.tencent.libexample", "com.tencent.intervideo.sixgodcontainer.proxyactivitys.PluginDefaultProxyActivity")
        if (pluginActivity.className == launcherActivityClassName) {
            _LauncherActivity = pluginActivity
        }
        return containerActivity
    }

}