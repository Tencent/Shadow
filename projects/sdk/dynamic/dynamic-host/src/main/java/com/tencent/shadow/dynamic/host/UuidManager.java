/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: /Users/shifujun/Codes/Android/now-shadow/projects/sdk/dynamic/dynamic-host/dynamic-host-aar/src/main/aidl/com/tencent/shadow/dynamic/host/UuidManager.aidl
 */
package com.tencent.shadow.dynamic.host;

import android.os.RemoteException;

import com.tencent.shadow.core.common.InstalledApk;

public interface UuidManager {

    int TRANSACTION_CODE_NO_EXCEPTION = 0;
    int TRANSACTION_CODE_FAILED_EXCEPTION = 1;
    int TRANSACTION_CODE_NOT_FOUND_EXCEPTION = 2;
    String DESCRIPTOR = UuidManager.class.getName();
    int TRANSACTION_getPlugin = (android.os.IBinder.FIRST_CALL_TRANSACTION);
    int TRANSACTION_getPluginLoader = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
    int TRANSACTION_getRuntime = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);

    InstalledApk getPlugin(String uuid, String partKey) throws RemoteException, NotFoundException, FailedException;

    InstalledApk getPluginLoader(String uuid) throws RemoteException, NotFoundException, FailedException;

    InstalledApk getRuntime(String uuid) throws RemoteException, NotFoundException, FailedException;
}
