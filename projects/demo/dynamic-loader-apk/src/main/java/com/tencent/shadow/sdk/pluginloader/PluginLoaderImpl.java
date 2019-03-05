package com.tencent.shadow.sdk.pluginloader;

import android.content.Context;

import com.tencent.shadow.core.loader.Reporter;
import com.tencent.shadow.core.loader.ShadowPluginLoader;
import com.tencent.shadow.core.loader.managers.ComponentManager;
import com.tencent.shadow.core.loader.managers.PluginBroadcastManager;

/**
 * 这里的类名和包名需要固定
 * com.tencent.shadow.sdk.pluginloader.PluginLoaderImpl
 */
public class PluginLoaderImpl extends ShadowPluginLoader {

    private final static String TAG = "shadow";

    private ComponentManager componentManager;

    public PluginLoaderImpl(Context hostAppContext) {
        super(hostAppContext);
        componentManager = new DemoComponentManager(hostAppContext);
    }

    @Override
    public ComponentManager getComponentManager() {
        return componentManager;
    }

    @Override
    public PluginBroadcastManager getBusinessPluginReceiverManager(Context hostAppContext) {
        return new DemoPluginBroadcastManager();
    }

    @Override
    public Reporter getMExceptionReporter() {
        return new Reporter() {
            @Override
            public void reportException(Exception exception) {
                android.util.Log.e(TAG, "reportException", exception);
            }

            @Override
            public void log(String msg) {
                android.util.Log.i(TAG, msg);
            }
        };
    }

    @Override
    public String getMAbi() {
        return "";
    }
}
