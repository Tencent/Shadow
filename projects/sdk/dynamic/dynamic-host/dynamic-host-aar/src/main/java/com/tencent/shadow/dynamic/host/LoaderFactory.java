package com.tencent.shadow.dynamic.host;

import android.content.Context;
import android.os.IBinder;

public interface LoaderFactory {
    IBinder buildLoader(String uuid, UuidManager uuidManager, Context context);
}
