package com.tencent.shadow.runtime;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

public class ShadowLayoutInflater extends LayoutInflater {

    private static final String[] sClassPrefixList = {
            "android.widget.",
            "android.webkit.",
            "android.app."
    };

    private static final String AndroidWebView = "android.webkit.WebView";

    private static final String ShadowPackagePrefix = "com.tencent.shadow.runtime.";

    private static final String ShadowWebView = "ShadowWebView";

    public ShadowLayoutInflater(Context context) {
        super(context);
    }

    public ShadowLayoutInflater(LayoutInflater original, Context newContext) {
        super(original, newContext);
    }

    @Override
    protected View onCreateView(String name, AttributeSet attrs) throws ClassNotFoundException {
        for (String prefix : sClassPrefixList) {
            try {
                if (AndroidWebView.equals(prefix + name)) {
                    prefix = ShadowPackagePrefix;
                    name = ShadowWebView;
                }
                View view = createView(name, prefix, attrs);
                if (view != null) {
                    return view;
                }
            } catch (ClassNotFoundException e) {
                // In this case we want to let the base class take a crack
                // at it.
            }
        }

        return super.onCreateView(name, attrs);
    }

    @Override
    public LayoutInflater cloneInContext(Context newContext) {
        return this;
    }
}
