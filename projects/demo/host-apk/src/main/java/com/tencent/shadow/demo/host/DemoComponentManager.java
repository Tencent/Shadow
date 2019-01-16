package com.tencent.shadow.demo.host;

import android.content.ComponentName;

import com.tencent.shadow.core.loader.managers.ComponentManager;

public class DemoComponentManager extends ComponentManager {
    final private static ComponentName sDefaultContainer = new ComponentName("com.tencent.shadow.demo_host", "com.tencent.shadow.demo.host.DefaultContainerActivity");

    @Override
    public ComponentName getInitActivity(String s) {
        return null;//还没去掉的无用方法
    }

    @Override
    public ComponentName getLauncherActivity(String s) {
        return null;//还没去掉的无用方法
    }

    @Override
    public ComponentName onBindContainerActivity(ComponentName componentName) {
        return sDefaultContainer;
    }

    @Override
    public ComponentName onBindContainerService(ComponentName componentName) {
        return null;//还没去掉的无用方法
    }
}
