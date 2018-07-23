package com.tencent.plugin_loader_apk

import android.content.ComponentName
import com.tencent.shadow.loader.managers.PluginServicesManager

/**
 * Created by tracyluo on 2018/6/7.
 */
class DemoPluginServicesManager : PluginServicesManager() {
    override fun onBindContainerService(mockService: ComponentName): ComponentName {
        return ComponentName("com.tencent.libexample", "com.tencent.intervideo.sixgodcontainer.proxyactivitys.PluginDefaultProxyService")
    }
}