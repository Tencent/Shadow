package com.leelu.shadow.service;


import android.content.pm.ApplicationInfo;
import android.content.res.Resources;
import android.util.Log;

import com.tencent.shadow.dynamic.host.PluginProcessService;
/**
 * CreateDate: 2022/3/15 17:40
 * Author: 李露
 * Email: lilu2@haier.com
 * Version: 1.0
 * Description:
 */
public class OtherPluginProcessService extends PluginProcessService {
    public OtherPluginProcessService() {

        LoadPluginCallback.setCallback(new LoadPluginCallback.Callback() {
            @Override
            public void beforeLoadPlugin(String partKey) {
                Log.e("OtherPPS", "beforeLoadPlugin====>" +
                        "\tpartKey = [" + partKey + "]");
            }

            @Override
            public void afterLoadPlugin(String partKey, ApplicationInfo applicationInfo, ClassLoader pluginClassLoader, Resources pluginResources) {
                Log.e("OtherPPS", "afterLoadPlugin=====>" +
                        "\tpartKey = [" + partKey + "], " +
                        "applicationInfo.className = [" + applicationInfo.className + "], " +
                        "applicationInfo.metaData = [" + applicationInfo.metaData + "], " +
                        "pluginClassLoader = [" + pluginClassLoader + "]," +
                        "pluginResources = [" + pluginResources + "]");
            }
        });
    }
}
