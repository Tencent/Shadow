package com.tencent.shadow.sdk.pluginloader;

import android.content.Context;

import com.tencent.shadow.core.loader.Reporter;
import com.tencent.shadow.core.loader.ShadowPluginLoader;
import com.tencent.shadow.core.loader.managers.ComponentManager;

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

    /**
     *
     * 配置插件中可以访问宿主类的白名单
     */
    @Override
    public String[] getWhiteList() {
        return new String[]{
                "androidx.test.espresso",//这个包添加是为了插件demo中可以访问测试框架的类
                "com.tencent.shadow.demo.interfaces"//测试插件访问宿主白名单类
        };
    }
}
