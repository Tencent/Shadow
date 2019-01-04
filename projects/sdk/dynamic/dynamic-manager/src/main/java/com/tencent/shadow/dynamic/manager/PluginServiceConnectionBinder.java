package com.tencent.shadow.dynamic.manager;

import android.content.ComponentName;
import android.os.Binder;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;

import com.tencent.shadow.dynamic.loader.PluginServiceConnection;

import static com.tencent.shadow.dynamic.loader.PluginServiceConnection.DESCRIPTOR;
import static com.tencent.shadow.dynamic.loader.PluginServiceConnection.TRANSACTION_onServiceConnected;
import static com.tencent.shadow.dynamic.loader.PluginServiceConnection.TRANSACTION_onServiceDisconnected;

/**
 * Local-side IPC implementation stub class.
 */
class PluginServiceConnectionBinder extends Binder {

    private final PluginServiceConnection mPsc;

    /**
     * Construct the stub at attach it to the interface.
     */
    PluginServiceConnectionBinder(PluginServiceConnection psc) {
        mPsc = psc;
    }

    @Override
    public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
        switch (code) {
            case INTERFACE_TRANSACTION: {
                reply.writeString(DESCRIPTOR);
                return true;
            }
            case TRANSACTION_onServiceConnected: {
                data.enforceInterface(DESCRIPTOR);
                ComponentName _arg0;
                if (0 != data.readInt()) {
                    _arg0 = ComponentName.CREATOR.createFromParcel(data);
                } else {
                    _arg0 = null;
                }
                IBinder _arg1;
                _arg1 = data.readStrongBinder();
                mPsc.onServiceConnected(_arg0, _arg1);
                reply.writeNoException();
                return true;
            }
            case TRANSACTION_onServiceDisconnected: {
                data.enforceInterface(DESCRIPTOR);
                ComponentName _arg0;
                if (0 != data.readInt()) {
                    _arg0 = ComponentName.CREATOR.createFromParcel(data);
                } else {
                    _arg0 = null;
                }
                mPsc.onServiceDisconnected(_arg0);
                reply.writeNoException();
                return true;
            }
        }
        return super.onTransact(code, data, reply, flags);
    }
}
