// IPluginLoaderServiceInterface.aidl
package com.tencent.shadow.dynamic.loader;

import android.content.Intent;
import com.tencent.shadow.dynamic.loader.IServiceConnection;

interface IPluginLoaderServiceInterface {

    void loadPlugin(String partKey, String apkFilePath, boolean isInterface);

    void callApplicationOnCreate(String partKey);

    Intent convertActivityIntent(in Intent pluginActivityIntent);

    ComponentName startPluginService(in Intent pluginServiceIntent);

    boolean stopPluginService(in Intent pluginServiceIntent);

    boolean bindPluginService(in Intent pluginServiceIntent,in IServiceConnection connection, int flags);

    void unbindService(in IServiceConnection conn);
}
