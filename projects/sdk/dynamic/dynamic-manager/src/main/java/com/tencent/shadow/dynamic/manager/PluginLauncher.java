package com.tencent.shadow.dynamic.manager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.RemoteException;

import com.tencent.shadow.dynamic.host.IPluginLauncher;
import com.tencent.shadow.dynamic.loader.PluginLoader;


class PluginLauncher implements IPluginLauncher {

    private PluginLoader mPluginLoader;

    PluginLauncher(PluginLoader mPluginLoader) {
        this.mPluginLoader = mPluginLoader;
    }


    @Override
    public void callApplicationOnCreate(String partKey) throws RemoteException {
        mPluginLoader.callApplicationOnCreate(partKey);
    }

    @Override
    public void startPluginActivity(Context context, Intent pluginActivityIntent) throws RemoteException {
        Intent activityIntent = mPluginLoader.convertActivityIntent(pluginActivityIntent);
        if (!(context instanceof Activity)) {
            activityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(activityIntent);
    }

    @Override
    public void startPluginService(Intent pluginServiceIntent) throws RemoteException {
        mPluginLoader.startPluginService(pluginServiceIntent);
    }

    @Override
    public void bindPluginService(Intent pluginServiceIntent, final ServiceConnection connection, int flag) throws RemoteException {
        ServiceConnectionWrapper serviceConnectionWrapper = PluginServiceConnectManager.getServiceConnectionWrapper(connection);
        mPluginLoader.bindPluginService(pluginServiceIntent, serviceConnectionWrapper.getIServiceConnection(), flag);
    }

    @Override
    public void unbindService(ServiceConnection conn) throws RemoteException {
        ServiceConnectionWrapper serviceConnectionWrapper = PluginServiceConnectManager.getServiceConnectionWrapper(conn);
        mPluginLoader.unbindService(serviceConnectionWrapper.getIServiceConnection());
    }
}
