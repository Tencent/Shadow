package com.tencent.shadow.runtime;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class PluginPartInfoManager {

    private static Map<ClassLoader, PluginPartInfo> sPluginInfos = new HashMap<>();

    public static void addPluginInfo(ClassLoader classLoader, PluginPartInfo pluginPartInfo) {
        sPluginInfos.put(classLoader, pluginPartInfo);
    }

    public static PluginPartInfo getPluginInfo(ClassLoader classLoader) {
        PluginPartInfo pluginPartInfo = sPluginInfos.get(classLoader);
        if (pluginPartInfo == null) {
            throw new RuntimeException("没有找到classLoader对应的pluginInfo classLoader:" + classLoader);
        }
        return pluginPartInfo;
    }


    public static Collection<PluginPartInfo> getAllPluginInfo() {
        return sPluginInfos.values();
    }


}
