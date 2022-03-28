package com.leelu.shadow.service;

import android.content.pm.ApplicationInfo;
import android.content.res.Resources;
/**
 * CreateDate: 2022/3/15 17:39
 * Author: 李露
 * Email: lilu2@haier.com
 * Version: 1.0
 * Description:
 */
public class LoadPluginCallback {
    private static Callback sCallback;
    public static void setCallback(Callback callback) {
        sCallback = callback;
    }
    public static Callback getCallback() {
        return sCallback;
    }
    public interface Callback {
        void beforeLoadPlugin(String partKey);
        void afterLoadPlugin(String partKey, ApplicationInfo applicationInfo, ClassLoader pluginClassLoader, Resources pluginResources);
    }
}
