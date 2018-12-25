package com.tencent.shadow.dynamic.host;

import android.os.IBinder;
import android.os.Parcel;

import com.tencent.shadow.core.common.InstalledApk;

class BinderUuidManager implements UuidManager {
    private IBinder mRemote;

    BinderUuidManager(IBinder remote) {
        mRemote = remote;
    }

    private void checkException(Parcel _reply) throws FailedException, NotFoundException {
        int i = _reply.readInt();
        if (i == UuidManager.TRANSACTION_CODE_FAILED_EXCEPTION) {
            throw new FailedException(_reply);
        } else if (i == UuidManager.TRANSACTION_CODE_NOT_FOUND_EXCEPTION) {
            throw new NotFoundException(_reply);
        } else if (i != UuidManager.TRANSACTION_CODE_NO_EXCEPTION) {
            throw new RuntimeException("不认识的Code==" + i);
        }
    }

    @Override
    public InstalledApk getPlugin(String uuid, String partKey) throws android.os.RemoteException, FailedException, NotFoundException {
        Parcel _data = Parcel.obtain();
        Parcel _reply = Parcel.obtain();
        InstalledApk _result;
        try {
            _data.writeInterfaceToken(UuidManager.DESCRIPTOR);
            _data.writeString(uuid);
            _data.writeString(partKey);
            mRemote.transact(UuidManager.TRANSACTION_getPlugin, _data, _reply, 0);
            checkException(_reply);
            if ((0 != _reply.readInt())) {
                _result = InstalledApk.CREATOR.createFromParcel(_reply);
            } else {
                _result = null;
            }
        } finally {
            _reply.recycle();
            _data.recycle();
        }
        return _result;
    }

    @Override
    public InstalledApk getPluginLoader(String uuid) throws android.os.RemoteException, NotFoundException, FailedException {
        Parcel _data = Parcel.obtain();
        Parcel _reply = Parcel.obtain();
        InstalledApk _result;
        try {
            _data.writeInterfaceToken(UuidManager.DESCRIPTOR);
            _data.writeString(uuid);
            mRemote.transact(UuidManager.TRANSACTION_getPluginLoader, _data, _reply, 0);
            checkException(_reply);
            if ((0 != _reply.readInt())) {
                _result = InstalledApk.CREATOR.createFromParcel(_reply);
            } else {
                _result = null;
            }
        } finally {
            _reply.recycle();
            _data.recycle();
        }
        return _result;
    }

    @Override
    public InstalledApk getRuntime(String uuid) throws android.os.RemoteException, NotFoundException, FailedException {
        Parcel _data = Parcel.obtain();
        Parcel _reply = Parcel.obtain();
        InstalledApk _result;
        try {
            _data.writeInterfaceToken(UuidManager.DESCRIPTOR);
            _data.writeString(uuid);
            mRemote.transact(UuidManager.TRANSACTION_getRuntime, _data, _reply, 0);
            checkException(_reply);
            if ((0 != _reply.readInt())) {
                _result = InstalledApk.CREATOR.createFromParcel(_reply);
            } else {
                _result = null;
            }
        } finally {
            _reply.recycle();
            _data.recycle();
        }
        return _result;
    }
}
