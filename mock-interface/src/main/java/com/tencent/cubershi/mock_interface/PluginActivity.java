package com.tencent.cubershi.mock_interface;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.view.ContextThemeWrapper;

import com.tencent.hydevteam.pluginframework.plugincontainer.HostActivityDelegator;

abstract class PluginActivity extends ContextThemeWrapper {
    HostActivityDelegator mHostActivityDelegator;

    Resources mPluginResources;

    ClassLoader mPluginClassLoader;

    PluginActivityLauncher mPluginActivityLauncher;

    MockApplication mPluginApplication;

    public final void setPluginResources(Resources resources) {
        mPluginResources = resources;
    }

    public final void setHostContextAsBase(Context context) {
        attachBaseContext(context);
    }

    public final void setPluginClassLoader(ClassLoader classLoader) {
        mPluginClassLoader = classLoader;
    }

    public void setContainerActivity(HostActivityDelegator delegator) {
        mHostActivityDelegator = delegator;
    }

    public void setPluginActivityLauncher(MockActivity.PluginActivityLauncher pluginActivityLauncher) {
        mPluginActivityLauncher = pluginActivityLauncher;
    }

    public void setPluginApplication(MockApplication pluginApplication) {
        mPluginApplication = pluginApplication;
    }

    public interface PluginActivityLauncher {
        /**
         * 启动Actvity
         *
         * @param context 启动context
         * @param intent  插件内传来的Intent.
         * @return <code>true</code>表示该Intent是为了启动插件内Activity的,已经被正确消费了.
         * <code>false</code>表示该Intent不是插件内的Activity.
         */
        boolean startActivity(Context context, Intent intent);

    }
}
