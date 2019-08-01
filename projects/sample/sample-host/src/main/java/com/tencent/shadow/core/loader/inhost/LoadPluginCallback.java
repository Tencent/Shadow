package com.tencent.shadow.core.loader.inhost;

import android.content.pm.ApplicationInfo;
import android.content.res.Resources;
import android.util.Log;

public class LoadPluginCallback {
	public static void beforeLoadPlugin(String businessName, String partKey) {
		Log.d("LoadPluginCallback", "beforeLoadPlugin(" + businessName + "," + partKey + ")");
	}

	public static void afterLoadPlugin(String businessName, String partKey, ApplicationInfo applicationInfo, ClassLoader pluginClassLoader, Resources pluginResources) {
		Log.d("LoadPluginCallback", "afterLoadPlugin(" + businessName + "," + partKey + "," + applicationInfo.className + "," + pluginClassLoader + ")");
	}
}
