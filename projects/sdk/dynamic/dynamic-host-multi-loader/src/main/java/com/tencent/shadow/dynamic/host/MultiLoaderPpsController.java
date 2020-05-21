package com.tencent.shadow.dynamic.host;

import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;

public class MultiLoaderPpsController {
    final private IBinder mRemote;

    MultiLoaderPpsController(IBinder remote) {
        mRemote = remote;
    }

    public void exit() throws RemoteException {
        Parcel _data = Parcel.obtain();
        Parcel _reply = Parcel.obtain();
        try {
            _data.writeInterfaceToken(MultiLoaderPpsBinder.DESCRIPTOR);
            mRemote.transact(MultiLoaderPpsBinder.TRANSACTION_exit, _data, _reply, 0);
            _reply.readException();
        } finally {
            _reply.recycle();
            _data.recycle();
        }
    }

    public IBinder getSingleLoaderPps(String uuid) throws RemoteException {
        Parcel _data = Parcel.obtain();
        Parcel _reply = Parcel.obtain();
        IBinder _result;
        try {
            _data.writeInterfaceToken(MultiLoaderPpsBinder.DESCRIPTOR);
            _data.writeString(uuid);
            mRemote.transact(MultiLoaderPpsBinder.TRANSACTION_getSingleLoaderPps, _data, _reply, 0);
            _reply.readException();
            _result = _reply.readStrongBinder();
        } finally {
            _reply.recycle();
            _data.recycle();
        }
        return _result;
    }
}
