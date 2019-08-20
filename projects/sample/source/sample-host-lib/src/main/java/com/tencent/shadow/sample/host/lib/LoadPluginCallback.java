package com.tencent.shadow.sample.host.lib;

import android.content.pm.ApplicationInfo;
import android.content.res.Resources;

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
