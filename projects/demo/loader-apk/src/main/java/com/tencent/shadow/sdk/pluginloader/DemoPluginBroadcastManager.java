package com.tencent.shadow.sdk.pluginloader;

import com.tencent.shadow.core.loader.managers.PluginBroadcastManager;

import java.util.ArrayList;
import java.util.List;

/**
 * 插件静态广播配置
 * todo #38 将广播配置移到ComponentManager
 * todo #39 动态解析AndroidManifest.xml 中的广播信息动态注册
 */
public class DemoPluginBroadcastManager extends PluginBroadcastManager {

    /**
     * 当含有多个part apk时根据partKey来配置对应的广播
     *
     * @param partKey apk的别名
     */
    @Override
    public List<BroadcastInfo> getBroadcastInfoList(String partKey) {
        List<BroadcastInfo> broadcastInfos = new ArrayList<>();
        broadcastInfos.add(new BroadcastInfo("com.tencent.shadow.demo.usecases.receiver.MyReceiver",
                new String[]{"com.tencent.test.action"}));
        return broadcastInfos;
    }
}
