package com.tencent.shadow.demo.host;

import android.content.Context;

import com.tencent.shadow.core.loader.Reporter;
import com.tencent.shadow.core.loader.ShadowPluginLoader;
import com.tencent.shadow.core.loader.managers.ComponentManager;

import org.jetbrains.annotations.NotNull;


public class DemoPluginLoader extends ShadowPluginLoader {

    final private ComponentManager mCM = new DemoComponentManager();

    public DemoPluginLoader(@NotNull Context context) {
        super(context);
    }

    @Override
    public String getMAbi() {
        return "armeabi";
    }

    @Override
    public ComponentManager getComponentManager() {
        return mCM;
    }

    @Override
    public Reporter getMExceptionReporter() {
        return new Reporter() {
            @Override
            public void reportException(Exception e) {

            }

            @Override
            public void log(String s) {

            }
        };
    }
}
