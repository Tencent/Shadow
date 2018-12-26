package com.tencent.shadow.dynamic.host;

import android.os.IBinder;

public interface PluginLoaderImpl extends IBinder {
    void setUuidManager(UuidManager uuidManager);
}
