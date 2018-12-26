package com.tencent.shadow.dynamic.host;

import android.content.Context;

public interface LoaderFactory {
    PluginLoaderImpl buildLoader(String uuid, Context context);
}
