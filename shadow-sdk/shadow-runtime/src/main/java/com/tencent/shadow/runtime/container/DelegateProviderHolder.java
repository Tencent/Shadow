package com.tencent.shadow.runtime.container;


/**
 * DelegateProvider依赖注入类
 * <p>
 * dynamic-pluginloader通过这个类实现将PluginLoader中的DelegateProvider实现注入到plugincontainer中。
 *
 * @author cubershi
 */
public class DelegateProviderHolder {
    static DelegateProvider delegateProvider;

    public static void setDelegateProvider(DelegateProvider delegateProvider) {
        DelegateProviderHolder.delegateProvider = delegateProvider;
    }
}
