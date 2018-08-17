package com.tencent.shadow.loader.managers

import android.content.Intent
import com.tencent.shadow.loader.ShadowPluginLoader
import com.tencent.shadow.runtime.ShadowContext

class PendingIntentManager(private val mShadowPluginLoader: ShadowPluginLoader) : ShadowContext.PendingIntentConverter {

    override fun convertPluginActivityIntent(pluginIntent: Intent): Intent {
        return mShadowPluginLoader.getBusinessPluginActivitiesManager().convertActivityIntent(pluginIntent)
    }

    override fun convertPluginServiceIntent(pluginIntent: Intent): Intent {
        return mShadowPluginLoader.getBusinessPluginServiceManager().getContainerServiceIntent(pluginIntent, PluginServicesManager.Operate.START)
                ?: pluginIntent
    }


}
