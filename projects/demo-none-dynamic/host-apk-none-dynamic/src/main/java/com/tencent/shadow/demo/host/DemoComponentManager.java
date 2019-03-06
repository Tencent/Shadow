package com.tencent.shadow.demo.host;

import android.content.ComponentName;

import com.tencent.shadow.core.loader.infos.ContainerProviderInfo;
import com.tencent.shadow.core.loader.managers.ComponentManager;

import org.jetbrains.annotations.NotNull;

public class DemoComponentManager extends ComponentManager {
    final private static ComponentName sDefaultContainer = new ComponentName("com.tencent.shadow.demo_host", "com.tencent.shadow.demo.host.DefaultContainerActivity");
    final private static ComponentName sSingleTaskContainer = new ComponentName("com.tencent.shadow.demo_host", "com.tencent.shadow.demo.host.SingleTaskContainerActivity");


    @Override
    public ComponentName getLauncherActivity(String s) {
        return null;//还没去掉的无用方法
    }

    @Override
    public ComponentName onBindContainerActivity(ComponentName componentName) {
        if(componentName.getClassName().equals("com.tencent.shadow.demo.usecases.activity.TestActivityOrientation")){
            return sSingleTaskContainer;
        }
        return sDefaultContainer;
    }

    @Override
    public ComponentName onBindContainerService(ComponentName componentName) {
        return null;//还没去掉的无用方法
    }

    @NotNull
    @Override
    public ContainerProviderInfo onBindContainerContentProvider(@NotNull ComponentName pluginContentProvider) {
        return new ContainerProviderInfo("com.tencent.shadow.runtime.container.PluginContainerContentProvider","com.tencent.shadow.contentprovider.authority");
    }
}
