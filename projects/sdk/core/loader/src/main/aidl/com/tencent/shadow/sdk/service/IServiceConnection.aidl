// IServiceConnection.aidl
// Same as android.app.IServiceConnection
package com.tencent.shadow.sdk.service;

import android.content.ComponentName;

interface IServiceConnection {

    void onServiceConnected(in ComponentName name, IBinder service);

    void onServiceDisconnected(in ComponentName name);
}
