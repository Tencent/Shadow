package com.tencent.shadow.runtime;

import android.content.pm.PackageItemInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;

public class ShadowPackageItemInfo {

    /**
     * @param classLoader 对应插件所在的classLoader
     * @param packageItemInfo MetaData所在的组件
     * @param pm PackageManager
     * @param name metaData对应的name
     * @return  返回所在插件的xml对应的XmlResourceParser
     */
    public static XmlResourceParser loadXmlMetaData(ClassLoader classLoader, PackageItemInfo packageItemInfo, PackageManager pm, String name) {
        PluginPartInfo pluginPartInfo = PluginPartInfoManager.getPluginInfo(classLoader);
        Resources resources = pluginPartInfo.application.getResources();
        if (packageItemInfo.metaData != null) {
            int resid = packageItemInfo.metaData.getInt(name);
            if (resid != 0) {
                return resources.getXml(resid);
            }
        }
        return null;
    }

}
