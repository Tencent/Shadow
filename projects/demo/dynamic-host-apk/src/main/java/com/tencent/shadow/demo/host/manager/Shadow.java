package com.tencent.shadow.demo.host.manager;

import com.tencent.shadow.dynamic.host.DynamicPluginManager;
import com.tencent.shadow.dynamic.host.PluginManager;

import java.io.File;

public class Shadow {

    public static PluginManager getPluginManager(){
        final DataLocalTmpPmUpdater dataLocalTmpPmUpdater = new DataLocalTmpPmUpdater(null);
        File tempPm = dataLocalTmpPmUpdater.getLatest();
        if (tempPm != null) {
            return new DynamicPluginManager(dataLocalTmpPmUpdater);
        }
        return null;
    }

}
