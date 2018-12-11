package com.tencent.shadow.dynamic.manager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.RemoteException;

import com.tencent.shadow.core.pluginmanager.pluginlauncher.IPluginLauncher;
import com.tencent.shadow.dynamic.loader.IPluginLoaderServiceInterface;


public class PluginLauncher implements IPluginLauncher {

    private IPluginLoaderServiceInterface pluginLoaderServiceInterface;

    public PluginLauncher(IPluginLoaderServiceInterface pluginLoaderServiceInterface) {
        this.pluginLoaderServiceInterface = pluginLoaderServiceInterface;
    }


    @Override
    public void callApplicationOnCreate(String partKey) throws RemoteException {
        pluginLoaderServiceInterface.callApplicationOnCreate(partKey);
    }

    @Override
    public void startPluginActivity(Context context, Intent pluginActivityIntent) throws RemoteException {
        Intent activityIntent = pluginLoaderServiceInterface.convertActivityIntent(pluginActivityIntent);
        if (!(context instanceof Activity)) {
            activityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(activityIntent);
    }

    @Override
    public void startPluginService(Intent pluginServiceIntent) throws RemoteException {
        pluginLoaderServiceInterface.startPluginService(pluginServiceIntent);
    }

    @Override
    public void bindPluginService(Intent pluginServiceIntent, final ServiceConnection connection, int flag) throws RemoteException {
        ServiceConnectionWrapper serviceConnectionWrapper = PluginServiceConnectManager.getServiceConnectionWrapper(connection);
        pluginLoaderServiceInterface.bindPluginService(pluginServiceIntent, serviceConnectionWrapper.getIServiceConnection(), flag);
    }

    @Override
    public void unbindService(ServiceConnection conn) throws RemoteException {
        ServiceConnectionWrapper serviceConnectionWrapper = PluginServiceConnectManager.getServiceConnectionWrapper(conn);
        pluginLoaderServiceInterface.unbindService(serviceConnectionWrapper.getIServiceConnection());
    }
}
