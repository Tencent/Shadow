package com.tencent.shadow.test.dynamic.loader;

import android.content.Context;

import com.tencent.shadow.core.loader.Reporter;
import com.tencent.shadow.core.loader.ShadowPluginLoader;
import com.tencent.shadow.core.loader.managers.ComponentManager;

public class TestPluginLoader extends ShadowPluginLoader {

    private final static String TAG = "shadow";

    private ComponentManager componentManager;

    public TestPluginLoader(Context hostAppContext) {
        super(hostAppContext);
        componentManager = new TestComponentManager(hostAppContext);
    }

    @Override
    public ComponentManager getComponentManager() {
        return componentManager;
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
