package com.tencent.shadow.dynamic.host;

import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;

import com.tencent.shadow.dynamic.host.FailedException;
import com.tencent.shadow.dynamic.host.MultiLoaderPpsBinder;
import com.tencent.shadow.dynamic.host.PpsStatus;

import static com.tencent.shadow.dynamic.host.MultiLoaderPpsBinder.TRANSACTION_CODE_FAILED_EXCEPTION;
import static com.tencent.shadow.dynamic.host.MultiLoaderPpsBinder.TRANSACTION_CODE_NO_EXCEPTION;

public class MultiLoaderPpsController {
    final private IBinder mRemote;

    MultiLoaderPpsController(IBinder remote) {
        mRemote = remote;
    }

    public void loadRuntimeForPlugin(String pluginKey, String uuid) throws RemoteException, FailedException {
        Parcel _data = Parcel.obtain();
        Parcel _reply = Parcel.obtain();
        try {
            _data.writeInterfaceToken(MultiLoaderPpsBinder.DESCRIPTOR);
            _data.writeString(pluginKey);
            _data.writeString(uuid);
            mRemote.transact(MultiLoaderPpsBinder.TRANSACTION_loadRuntimeForPlugin, _data, _reply, 0);
            int i = _reply.readInt();
            if (i == TRANSACTION_CODE_FAILED_EXCEPTION) {
                throw new FailedException(_reply);
            } else if (i != TRANSACTION_CODE_NO_EXCEPTION) {
                throw new RuntimeException("不认识的Code==" + i);
            }
        } finally {
            _reply.recycle();
            _data.recycle();
        }
    }

    public void loadPluginLoaderForPlugin(String pluginKey, String uuid) throws RemoteException, FailedException {
        Parcel _data = Parcel.obtain();
        Parcel _reply = Parcel.obtain();
        try {
            _data.writeInterfaceToken(MultiLoaderPpsBinder.DESCRIPTOR);
            _data.writeString(pluginKey);
            _data.writeString(uuid);
            mRemote.transact(MultiLoaderPpsBinder.TRANSACTION_loadPluginLoaderForPlugin, _data, _reply, 0);
            int i = _reply.readInt();
            if (i == TRANSACTION_CODE_FAILED_EXCEPTION) {
                throw new FailedException(_reply);
            } else if (i != TRANSACTION_CODE_NO_EXCEPTION) {
                throw new RuntimeException("不认识的Code==" + i);
            }
        } finally {
            _reply.recycle();
            _data.recycle();
        }
    }

    public void setUuidManagerForPlugin(String pluginKey, IBinder uuidManagerBinder) throws RemoteException {
        Parcel _data = Parcel.obtain();
        Parcel _reply = Parcel.obtain();
        try {
            _data.writeInterfaceToken(MultiLoaderPpsBinder.DESCRIPTOR);
            _data.writeString(pluginKey);
            _data.writeStrongBinder(uuidManagerBinder);
            mRemote.transact(MultiLoaderPpsBinder.TRANSACTION_setUuidManagerForPlugin, _data, _reply, 0);
            _reply.readException();
        } finally {
            _reply.recycle();
            _data.recycle();
        }
    }

    public PpsStatus getPpsStatusForPlugin(String pluginKey) throws RemoteException {
        Parcel _data = Parcel.obtain();
        Parcel _reply = Parcel.obtain();
        PpsStatus _result;
        try {
            _data.writeInterfaceToken(MultiLoaderPpsBinder.DESCRIPTOR);
            _data.writeString(pluginKey);
            mRemote.transact(MultiLoaderPpsBinder.TRANSACTION_getPpsStatusForPlugin, _data, _reply, 0);
            _reply.readException();
            _result = new PpsStatus(_reply);
        } finally {
            _reply.recycle();
            _data.recycle();
        }
        return _result;
    }

    public IBinder getPluginLoaderForPlugin(String pluginKey) throws RemoteException {
        Parcel _data = Parcel.obtain();
        Parcel _reply = Parcel.obtain();
        IBinder _result;
        try {
            _data.writeInterfaceToken(MultiLoaderPpsBinder.DESCRIPTOR);
            _data.writeString(pluginKey);
            mRemote.transact(MultiLoaderPpsBinder.TRANSACTION_getPluginLoaderForPlugin, _data, _reply, 0);
            _reply.readException();
            _result = _reply.readStrongBinder();
        } finally {
            _reply.recycle();
            _data.recycle();
        }
        return _result;
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
}
