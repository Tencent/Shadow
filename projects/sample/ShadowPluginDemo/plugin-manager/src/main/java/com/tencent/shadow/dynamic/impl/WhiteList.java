package com.tencent.shadow.dynamic.impl;

/**
 * CreateDate: 2022/3/17 17:59
 * Author: 李露
 * Email: lilu2@haier.com
 * 此类包名及类名固定
 * classLoader的白名单
 * PluginManager可以加载宿主中位于白名单内的类
 * 这个白名单类暂时没啥用，可以在插件打包的时候配置白名单
 */
public interface WhiteList {
    String[] sWhiteList = new String[]
            {
                    //"com.tencent.host.shadow",
                    //"com.tencent.shadow.test.lib.constant",
            };
}
