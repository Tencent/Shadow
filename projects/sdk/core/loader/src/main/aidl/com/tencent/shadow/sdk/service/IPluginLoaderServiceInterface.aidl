// IPluginLoaderServiceInterface.aidl
package com.tencent.shadow.sdk.service;

import android.content.Intent;
import com.tencent.shadow.sdk.service.IServiceConnection;

interface IPluginLoaderServiceInterface {

    void loadPlugin(String partKey);

    void callApplicationOnCreate(String partKey);

    Intent convertActivityIntent(in Intent pluginActivityIntent);

    void startPluginService(in Intent pluginServiceIntent);

    IBinder bindPluginService(in Intent pluginServiceIntent,in IServiceConnection connection);

    void unbindService(in IServiceConnection conn);
}
