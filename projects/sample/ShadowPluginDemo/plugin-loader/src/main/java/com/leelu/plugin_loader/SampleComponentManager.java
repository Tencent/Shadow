package com.leelu.plugin_loader;

/**
 * SimpleDes:
 * Creator: Sindi
 * Date: 2021-06-21
 * UseDes:
 */

import android.content.ComponentName;
import android.content.Context;
import com.tencent.shadow.core.loader.infos.ContainerProviderInfo;
import com.tencent.shadow.core.loader.managers.ComponentManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.ArrayList;


public class SampleComponentManager extends ComponentManager {
    /**
     * sample-runtime 模块中定义的壳子Activity，需要在宿主AndroidManifest.xml注册
     */

    private static final String DEFAULT_ACTIVITY = "com.leelu.plugin_runtime.PluginDefaultProxyActivity";
    private static final String SINGLE_INSTANCE_ACTIVITY = "com.leelu.plugin_runtime.PluginSingleInstance1ProxyActivity";
    private static final String SINGLE_TASK_ACTIVITY = "com.leelu.plugin_runtime.PluginSingleTask1ProxyActivity";
    private Context context;

    public SampleComponentManager(Context context) {
        this.context = context;
    }

    /**
     * 配置插件Activity 到 壳子Activity的对应关系
     *
     * @param pluginActivity 插件Activity
     * @return 壳子Activity
     */
    @Override
    public ComponentName onBindContainerActivity(ComponentName pluginActivity) {
        switch (pluginActivity.getClassName()) {

            //这里配置对应的对应关系
        }
        System.out.println("壳子：PluginDefaultProxyActivity");
        return new ComponentName(context, DEFAULT_ACTIVITY);
    }

    /**
     * 配置对应宿主中预注册的壳子contentProvider的信息
     */
    @Override
    public ContainerProviderInfo onBindContainerContentProvider(ComponentName pluginContentProvider) {
        return new ContainerProviderInfo(
                "com.tencent.shadow.core.runtime.container.PluginContainerContentProvider",
                context.getPackageName() + ".contentprovider.authority.dynamic");
    }
}
