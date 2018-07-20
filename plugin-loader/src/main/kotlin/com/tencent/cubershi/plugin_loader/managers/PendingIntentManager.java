package com.tencent.cubershi.plugin_loader.managers;

import android.content.Context;
import android.content.Intent;
import android.util.Pair;

import com.tencent.cubershi.mock_interface.MockContext;

public class PendingIntentManager implements MockContext.PendingIntentConverter{


    private PluginActivitiesManager mPluginActivitiesManager;

    private PluginServicesManager mPluginServicesManager;

    private Context mHostContext;


    public PendingIntentManager(Context context,PluginActivitiesManager pluginActivitiesManager, PluginServicesManager pluginServicesManager) {
        this.mHostContext = context;
        this.mPluginActivitiesManager = pluginActivitiesManager;
        this.mPluginServicesManager = pluginServicesManager;
    }

    public Pair<Context,Intent> convertPluginActivityIntent(Intent pluginIntent) {
        return new Pair<Context, Intent>(mHostContext,mPluginActivitiesManager.convertActivityIntent(pluginIntent));
    }

    public Pair<Context,Intent> convertPluginServiceIntent(Intent pluginIntent) {
        return new Pair<Context, Intent>(mHostContext,mPluginServicesManager.getContainerServiceIntent(pluginIntent, PluginServicesManager.Operate.START));
    }


}
