package com.tencent.shadow.dynamic.manager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.RemoteException;

import com.tencent.shadow.dynamic.loader.PluginLoader;


public class PluginLauncher {

    private PluginLoader mPluginLoader;

    PluginLauncher(PluginLoader mPluginLoader) {
        this.mPluginLoader = mPluginLoader;
    }


    public void callApplicationOnCreate(String partKey) throws RemoteException {
        mPluginLoader.callApplicationOnCreate(partKey);
    }

    public void startPluginActivity(Context context, Intent pluginActivityIntent) throws RemoteException {
        Intent activityIntent = mPluginLoader.convertActivityIntent(pluginActivityIntent);
        if (!(context instanceof Activity)) {
            activityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(activityIntent);
    }

    public void startPluginService(Intent pluginServiceIntent) throws RemoteException {
        mPluginLoader.startPluginService(pluginServiceIntent);
    }

    public void bindPluginService(Intent pluginServiceIntent, final ServiceConnection connection, int flag) throws RemoteException {
        ServiceConnectionWrapper serviceConnectionWrapper = PluginServiceConnectManager.getServiceConnectionWrapper(connection);
        mPluginLoader.bindPluginService(pluginServiceIntent, serviceConnectionWrapper.getIServiceConnection(), flag);
    }

    public void unbindService(ServiceConnection conn) throws RemoteException {
        ServiceConnectionWrapper serviceConnectionWrapper = PluginServiceConnectManager.getServiceConnectionWrapper(conn);
        mPluginLoader.unbindService(serviceConnectionWrapper.getIServiceConnection());
    }
}
