package com.tencent.shadow.loader.managers

import android.content.Context
import android.content.Intent
import android.util.Pair

import com.tencent.shadow.runtime.ShadowContext

class PendingIntentManager(private val mHostContext: Context, private val mPluginActivitiesManager: PluginActivitiesManager, private val mPluginServicesManager: PluginServicesManager) : ShadowContext.PendingIntentConverter {

    override fun convertPluginActivityIntent(pluginIntent: Intent): Pair<Context, Intent> {
        return Pair(mHostContext, mPluginActivitiesManager.convertActivityIntent(pluginIntent))
    }

    override fun convertPluginServiceIntent(pluginIntent: Intent): Pair<Context, Intent> {
        return Pair<Context, Intent>(mHostContext, mPluginServicesManager.getContainerServiceIntent(pluginIntent, PluginServicesManager.Operate.START))
    }


}
