package com.tencent.shadow.sample.loader;

import android.content.Context;

import com.tencent.shadow.core.loader.ShadowPluginLoader;
import com.tencent.shadow.core.loader.managers.ComponentManager;
import com.tencent.shadow.sample.loader.SampleComponentManager;

/**
 * 这里的类名和包名需要固定
 * com.tencent.shadow.sdk.pluginloader.PluginLoaderImpl
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
