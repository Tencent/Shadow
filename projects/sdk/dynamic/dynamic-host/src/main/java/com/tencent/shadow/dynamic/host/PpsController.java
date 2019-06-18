/*
 * Tencent is pleased to support the open source community by making Tencent Shadow available.
 * Copyright (C) 2019 THL A29 Limited, a Tencent company.  All rights reserved.
 *
 * Licensed under the BSD 3-Clause License (the "License"); you may not use
 * this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 *     https://opensource.org/licenses/BSD-3-Clause
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.tencent.shadow.dynamic.host;

import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;

import static com.tencent.shadow.dynamic.host.PpsBinder.TRANSACTION_CODE_FAILED_EXCEPTION;
import static com.tencent.shadow.dynamic.host.PpsBinder.TRANSACTION_CODE_NO_EXCEPTION;

public class PpsController {
    final private IBinder mRemote;

    PpsController(IBinder remote) {
        mRemote = remote;
    }

    public void loadRuntime(String uuid) throws RemoteException, FailedException {
        Parcel _data = Parcel.obtain();
        Parcel _reply = Parcel.obtain();
        try {
            _data.writeInterfaceToken(PpsBinder.DESCRIPTOR);
            _data.writeString(uuid);
            mRemote.transact(PpsBinder.TRANSACTION_loadRuntime, _data, _reply, 0);
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

    public void loadPluginLoader(String uuid) throws RemoteException, FailedException {
        Parcel _data = Parcel.obtain();
        Parcel _reply = Parcel.obtain();
        try {
            _data.writeInterfaceToken(PpsBinder.DESCRIPTOR);
            _data.writeString(uuid);
            mRemote.transact(PpsBinder.TRANSACTION_loadPluginLoader, _data, _reply, 0);
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

    public void setUuidManager(IBinder uuidManagerBinder) throws RemoteException {
        Parcel _data = Parcel.obtain();
        Parcel _reply = Parcel.obtain();
        try {
            _data.writeInterfaceToken(PpsBinder.DESCRIPTOR);
            _data.writeStrongBinder(uuidManagerBinder);
            mRemote.transact(PpsBinder.TRANSACTION_setUuidManager, _data, _reply, 0);
            _reply.readException();
        } finally {
            _reply.recycle();
            _data.recycle();
        }
    }

    public void exit() throws RemoteException {
        Parcel _data = Parcel.obtain();
        Parcel _reply = Parcel.obtain();
        try {
            _data.writeInterfaceToken(PpsBinder.DESCRIPTOR);
            mRemote.transact(PpsBinder.TRANSACTION_exit, _data, _reply, 0);
            _reply.readException();
        } finally {
            _reply.recycle();
            _data.recycle();
        }
    }

    public PpsStatus getPpsStatus() throws RemoteException {
        Parcel _data = Parcel.obtain();
        Parcel _reply = Parcel.obtain();
        PpsStatus _result;
        try {
            _data.writeInterfaceToken(PpsBinder.DESCRIPTOR);
            mRemote.transact(PpsBinder.TRANSACTION_getPpsStatus, _data, _reply, 0);
            _reply.readException();
            _result = new PpsStatus(_reply);
        } finally {
            _reply.recycle();
            _data.recycle();
        }
        return _result;
    }

    public IBinder getPluginLoader() throws RemoteException {
        Parcel _data = Parcel.obtain();
        Parcel _reply = Parcel.obtain();
        IBinder _result;
        try {
            _data.writeInterfaceToken(PpsBinder.DESCRIPTOR);
            mRemote.transact(PpsBinder.TRANSACTION_getPluginLoader, _data, _reply, 0);
            _reply.readException();
            _result = _reply.readStrongBinder();
        } finally {
            _reply.recycle();
            _data.recycle();
        }
        return _result;
    }
}
