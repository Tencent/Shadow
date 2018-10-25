package com.tencent.shadow.runtime;

import android.content.Context;
import android.util.Pair;
import android.view.LayoutInflater;

import com.tencent.hydevteam.pluginframework.plugincontainer.PluginContainerActivity;
import com.tencent.hydevteam.pluginframework.plugincontainer.PluginContainerService;

/**
 * 1.模拟PhoneLayoutInflater的系统view构造过程
 * 2.将xml中的webview替换成shadowWebView
 */
public class ShadowWebViewLayoutInflater extends FixedContextLayoutInflater{

    private static final String AndroidWebView = "android.webkit.WebView";

    private static final String ShadowPackagePrefix = "com.tencent.shadow.runtime.";

    private static final String ShadowWebView = "ShadowWebView";

    public ShadowWebViewLayoutInflater(LayoutInflater original, Context newContext) {
        super(original, newContext);
    }

    @Override
    LayoutInflater createNewContextLayoutInflater(Context newContext) {
        if (newContext instanceof PluginContainerService) {
            //走到这里只有可能是系统在壳子service里面调用了，预期系统不应该需要插件的资源，直接使用系统的context构造
            return new ShadowWebViewLayoutInflater(this, newContext);
        } else if (newContext instanceof PluginContainerActivity) {
            Object pluginActivity = ((PluginContainerActivity) newContext).getPluginActivity();
            return new ShadowWebViewLayoutInflater(this, (Context)pluginActivity);
        } else {
            //context有2种可能，1种是ShadowContext,一种是其他context
            return new ShadowWebViewLayoutInflater(this, newContext);
        }
    }

    @Override
    Pair<String,String> changeViewNameAndPrefix(String prefix,String name) {
        if (AndroidWebView.equals(prefix + name)) {
            prefix = ShadowPackagePrefix;
            name = ShadowWebView;
        }
        return new Pair<>(name,prefix);
    }


}
