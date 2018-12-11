package com.tencent.shadow.core.pluginmanager.pluginlauncher;

import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.RemoteException;

public interface IPluginLauncher {

    void callApplicationOnCreate(String partKey) throws RemoteException;

    void startPluginActivity(Context context, Intent pluginActivityIntent) throws RemoteException;

    void startPluginService(Intent pluginServiceIntent) throws RemoteException;

    void bindPluginService(Intent pluginServiceIntent, ServiceConnection connection, int flag) throws RemoteException;

    void unbindService(ServiceConnection conn) throws RemoteException;
}
