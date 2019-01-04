package com.tencent.shadow.dynamic.host;

import android.content.Context;

public interface ManagerFactory {
    PluginManagerImpl buildManager(Context context);
}
