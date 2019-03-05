package com.tencent.shadow.demo.host.manager;

import com.tencent.shadow.dynamic.host.DynamicPluginManager;
import com.tencent.shadow.dynamic.host.PluginManager;

import java.io.File;

public class Shadow {

    public static PluginManager getPluginManager(File apk){
        final DataLocalTmpPmUpdater dataLocalTmpPmUpdater = new DataLocalTmpPmUpdater(apk);
        File tempPm = dataLocalTmpPmUpdater.getLatest();
        if (tempPm != null) {
            return new DynamicPluginManager(dataLocalTmpPmUpdater);
        }
        return null;
    }

}
