package com.tencent.shadow.core.pluginmanager.pluginlauncher;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

import com.tencent.shadow.core.loader.IServiceConnection;


public class ServiceConnectionWrapper {

    private ServiceConnection mConnection;

    private IServiceConnection.Stub mIServiceConnection;

    public ServiceConnectionWrapper(ServiceConnection serviceConnection) {
        mConnection = serviceConnection;
        mIServiceConnection = new IServiceConnection.Stub() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) throws RemoteException {
                mConnection.onServiceConnected(name, service);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) throws RemoteException {
                mConnection.onServiceDisconnected(name);
            }
        };
    }

    public ServiceConnection getServiceConnection() {
        return mConnection;
    }

    public IServiceConnection getIServiceConnection() {
        return mIServiceConnection;
    }

}
