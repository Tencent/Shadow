package com.tencent.shadow.test.none_dynamic.host;

import android.content.Context;

import com.tencent.shadow.core.loader.Reporter;
import com.tencent.shadow.core.loader.ShadowPluginLoader;
import com.tencent.shadow.core.loader.managers.ComponentManager;

import org.jetbrains.annotations.NotNull;


public class TestPluginLoader extends ShadowPluginLoader {

    final private ComponentManager mCM = new TestComponentManager();

    public TestPluginLoader(@NotNull Context context) {
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
