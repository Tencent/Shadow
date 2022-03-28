package com.leelu.shadow.manager;

import com.tencent.shadow.dynamic.host.DynamicPluginManager;
import com.tencent.shadow.dynamic.host.PluginManager;

import java.io.File;

/**
 * CreateDate: 2022/3/15 17:30
 * Author: 李露
 * Email: lilu2@haier.com
 * Version: 1.0
 * UseDes:获取PluginManager
 */
public class Shadow {

    public static PluginManager getPluginManager(File apk){
        final FixedPathPmUpdater fixedPathPmUpdater = new FixedPathPmUpdater(apk);
        File tempPm = fixedPathPmUpdater.getLatest();
        if (tempPm != null) {
            return new DynamicPluginManager(fixedPathPmUpdater);
        }
        return null;
    }

}

