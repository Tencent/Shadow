package com.tencent.shadow.dynamic.impl;

/**
 * CreateDate: 2022/3/16 16:26
 * Author: 李露
 * Email: lilu2@haier.com
 * Version: 1.0
 * 此类包名及类名固定
 * classLoader的白名单
 * PluginLoader可以加载宿主中位于白名单内的类
 */
public interface WhiteList {
    String[] sWhiteList = new String[]{
            "com.a.b",
    };
}