package com.tencent.shadow.runtime;

import android.content.Context;
import android.view.LayoutInflater;


/**
 * 本类主要有2个目的
 * 1.替换xml里面的WebView为ShadowWebView
 * 2.给插件自定义View加上特定的前缀，防止插件切换的时候由于多插件自定义view重名，LayoutInflater缓存类构造器导致view冲突
 */
public class ShadowLayoutInflater extends ShadowWebViewLayoutInflater {


    public static ShadowLayoutInflater build(LayoutInflater original, Context newContext, String partKey) {
        InnerInflater innerLayoutInflater = new InnerInflater(original, newContext, partKey);
        return new ShadowLayoutInflater(innerLayoutInflater, newContext, partKey);
    }

    private static class InnerInflater extends ShadowLayoutInflater {
        private InnerInflater(LayoutInflater original, Context newContext, String partKey) {
            super(original, newContext, partKey);
            setFactory2(new ShadowFactory2(partKey,this));
        }
    }

    private ShadowLayoutInflater(LayoutInflater original, Context newContext, String partKey) {
        super(original, newContext);
    }


}
