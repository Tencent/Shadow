package com.leelu.plugin_loader;

import android.content.Context;

import com.tencent.shadow.core.loader.ShadowPluginLoader;
import com.tencent.shadow.core.loader.managers.ComponentManager;


/**
 * SimpleDes:
 * Creator: Sindi
 * Date: 2021-06-21
 * UseDes:
 */
public class SamplePluginLoader extends ShadowPluginLoader {
    private final static String TAG = "shadow";
    private ComponentManager componentManager;

    public SamplePluginLoader(Context hostAppContext) {
        super(hostAppContext);
        componentManager = new SampleComponentManager(hostAppContext);
    }

    @Override
    public ComponentManager getComponentManager() {
        return componentManager;
    }
}