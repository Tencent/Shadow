package com.tencent.shadow.dynamic.host;

import android.content.pm.ApplicationInfo;
import android.content.res.Resources;

public interface LoadPluginCallback {
	void beforeLoadPlugin(String partKey);

	void afterLoadPlugin(String partKey, ApplicationInfo applicationInfo, ClassLoader pluginClassLoader, Resources pluginResources);
}
