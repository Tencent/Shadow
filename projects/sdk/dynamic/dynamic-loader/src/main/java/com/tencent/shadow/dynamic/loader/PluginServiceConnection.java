/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: /Users/shifujun/Codes/Android/shadow/projects/sdk/dynamic/dynamic-loader/dynamic-loader-aar/src/main/aidl/com/tencent/shadow/dynamic/loader/IServiceConnection.aidl
 */
package com.tencent.shadow.dynamic.loader;

import android.content.ComponentName;
import android.os.IBinder;

public interface PluginServiceConnection {
    String DESCRIPTOR = PluginServiceConnection.class.getName();
    int TRANSACTION_onServiceConnected = IBinder.FIRST_CALL_TRANSACTION;
    int TRANSACTION_onServiceDisconnected = IBinder.FIRST_CALL_TRANSACTION + 1;

    void onServiceConnected(ComponentName name, IBinder service);

    void onServiceDisconnected(ComponentName name);

}
