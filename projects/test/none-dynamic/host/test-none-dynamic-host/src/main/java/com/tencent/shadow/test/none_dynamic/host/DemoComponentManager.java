package com.tencent.shadow.test.none_dynamic.host;

import android.content.ComponentName;

import com.tencent.shadow.core.loader.infos.ContainerProviderInfo;
import com.tencent.shadow.core.loader.managers.ComponentManager;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class DemoComponentManager extends ComponentManager {
    final private static ComponentName sDefaultContainer = new ComponentName(BuildConfig.APPLICATION_ID, "com.tencent.shadow.test.none_dynamic.host.DefaultContainerActivity");
    final private static ComponentName sSingleTaskContainer = new ComponentName(BuildConfig.APPLICATION_ID, "com.tencent.shadow.test.none_dynamic.host.SingleTaskContainerActivity");


    @Override
    public ComponentName onBindContainerActivity(ComponentName componentName) {
        if (componentName.getClassName().equals("com.tencent.shadow.test.plugin.general_cases.lib.usecases.activity.TestActivityOrientation")) {
            return sSingleTaskContainer;
        }
        return sDefaultContainer;
    }


    @NotNull
    @Override
    public ContainerProviderInfo onBindContainerContentProvider(@NotNull ComponentName pluginContentProvider) {
        return new ContainerProviderInfo("com.tencent.shadow.runtime.container.PluginContainerContentProvider","com.tencent.shadow.contentprovider.authority");
    }

    @Override
    public List<BroadcastInfo> getBroadcastInfoList(String partKey) {
        List<ComponentManager.BroadcastInfo> broadcastInfos = new ArrayList<>();
        broadcastInfos.add(new ComponentManager.BroadcastInfo("com.tencent.shadow.test.plugin.general_cases.lib.usecases.receiver.MyReceiver",
                new String[]{"com.tencent.test.action"}));
        return broadcastInfos;
    }
}
