package com.tencent.shadow.core.runtime.container;


/**
 * ContentProviderDelegateProvider依赖注入类
 * <p>
 * dynamic-pluginloader通过这个类实现将PluginLoader中的ContentProviderDelegateProvider实现注入到plugincontainer中。
 *
 * @author owenguo
 */
public class ContentProviderDelegateProviderHolder {
    static ContentProviderDelegateProvider contentProviderDelegateProvider;


    public static void setContentProviderDelegateProvider(ContentProviderDelegateProvider contentProviderDelegateProvider) {
        ContentProviderDelegateProviderHolder.contentProviderDelegateProvider = contentProviderDelegateProvider;
        notifyDelegateProviderHolderPrepare();
    }

    private static DelegateProviderHolderPrepareListener sPrepareListener;

    public static void setDelegateProviderHolderPrepareListener(DelegateProviderHolderPrepareListener prepareListener) {
        sPrepareListener = prepareListener;
    }

    private static void notifyDelegateProviderHolderPrepare() {
        if (sPrepareListener != null) {
            sPrepareListener.onPrepare();
        }
    }

    interface DelegateProviderHolderPrepareListener {
         void onPrepare();
    }

}
