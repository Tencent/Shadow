package com.tencent.shadow.dynamic.impl;

/**
 * 此类包名及类名固定
 * classLoader的白名单
 * PluginManager可以加载宿主中位于白名单内的类
 */
public interface WhiteList {
    String[] sWhiteList = new String[]
            {
            };
}
