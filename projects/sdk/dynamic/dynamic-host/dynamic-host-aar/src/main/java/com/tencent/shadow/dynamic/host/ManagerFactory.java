package com.tencent.shadow.dynamic.host;

import android.content.Context;

import com.tencent.shadow.core.common.PluginManager;

public interface ManagerFactory {
    PluginManager buildManager(Context context);
}
