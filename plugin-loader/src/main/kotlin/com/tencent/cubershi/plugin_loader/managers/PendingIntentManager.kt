package com.tencent.cubershi.plugin_loader.managers

import android.content.Context
import android.content.Intent
import android.util.Pair

import com.tencent.cubershi.mock_interface.MockContext

class PendingIntentManager(private val mHostContext: Context, private val mPluginActivitiesManager: PluginActivitiesManager, private val mPluginServicesManager: PluginServicesManager) : MockContext.PendingIntentConverter {

    override fun convertPluginActivityIntent(pluginIntent: Intent): Pair<Context, Intent> {
        return Pair(mHostContext, mPluginActivitiesManager.convertActivityIntent(pluginIntent))
    }

    override fun convertPluginServiceIntent(pluginIntent: Intent): Pair<Context, Intent> {
        return Pair<Context, Intent>(mHostContext, mPluginServicesManager.getContainerServiceIntent(pluginIntent, PluginServicesManager.Operate.START))
    }


}
