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

import static android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE;

class PpsBinder extends android.os.Binder implements SinglePps {

    static final int TRANSACTION_CODE_NO_EXCEPTION = 0;
    static final int TRANSACTION_CODE_FAILED_EXCEPTION = 1;

    static final int TRANSACTION_loadRuntime = (FIRST_CALL_TRANSACTION);
    static final int TRANSACTION_loadPluginLoader = (FIRST_CALL_TRANSACTION + 1);
    static final int TRANSACTION_setUuidManager = (FIRST_CALL_TRANSACTION + 2);
    static final int TRANSACTION_exit = (FIRST_CALL_TRANSACTION + 3);
    static final int TRANSACTION_getPpsStatus = (FIRST_CALL_TRANSACTION + 4);
    static final int TRANSACTION_getPluginLoader = (FIRST_CALL_TRANSACTION + 5);

    private final PluginProcessService mPps;

    PpsBinder(PluginProcessService pps) {
        attachInterface(this, DESCRIPTOR);
        mPps = pps;
    }

    @Override
    public boolean onTransact(int code, Parcel data, Parcel reply, int flags) {
        switch (code) {
            case INTERFACE_TRANSACTION: {
                reply.writeString(DESCRIPTOR);
                return true;
            }
            case TRANSACTION_loadRuntime: {
                data.enforceInterface(DESCRIPTOR);
                String _arg0;
                _arg0 = data.readString();
                try {
                    mPps.loadRuntime(_arg0);
                    reply.writeInt(TRANSACTION_CODE_NO_EXCEPTION);
                } catch (FailedException e) {
                    reply.writeInt(TRANSACTION_CODE_FAILED_EXCEPTION);
                    e.writeToParcel(reply, 0);
                }
                return true;
            }
            case TRANSACTION_loadPluginLoader: {
                data.enforceInterface(DESCRIPTOR);
                String _arg0;
                _arg0 = data.readString();
                try {
                    mPps.loadPluginLoader(_arg0);
                    reply.writeInt(TRANSACTION_CODE_NO_EXCEPTION);
                } catch (FailedException e) {
                    reply.writeInt(TRANSACTION_CODE_FAILED_EXCEPTION);
                    e.writeToParcel(reply, 0);
                }
                return true;
            }
            case TRANSACTION_setUuidManager: {
                data.enforceInterface(DESCRIPTOR);
                IBinder iBinder = data.readStrongBinder();
                UuidManager uuidManager = iBinder != null ? new BinderUuidManager(iBinder) : null;
                mPps.setUuidManager(uuidManager);
                reply.writeNoException();
                return true;
            }
            case TRANSACTION_exit: {
                data.enforceInterface(DESCRIPTOR);
                mPps.exit();
                reply.writeNoException();
                return true;
            }
            case TRANSACTION_getPpsStatus: {
                data.enforceInterface(DESCRIPTOR);
                PpsStatus ppsStatus = mPps.getPpsStatus();
                reply.writeNoException();
                ppsStatus.writeToParcel(reply, PARCELABLE_WRITE_RETURN_VALUE);
                return true;
            }
            case TRANSACTION_getPluginLoader: {
                data.enforceInterface(DESCRIPTOR);
                IBinder pluginLoader = mPps.getPluginLoader();
                reply.writeNoException();
                reply.writeStrongBinder(pluginLoader);
                return true;
            }
            default:
                return false;
        }
    }

    @Override
    public IBinder asBinder() {
        return this;
    }

    public static SinglePps asInterface(android.os.IBinder binder) {
        if (binder == null) {
            return null;
        }

        android.os.IInterface iin = binder.queryLocalInterface(DESCRIPTOR);
        if (iin instanceof SinglePps) {
            return (SinglePps) iin;
        }
        return new PpsController(binder);
    }

    @Override
    public void setUuidManager(IBinder binder) throws RemoteException {
        UuidManager uuidManager = binder != null ? new BinderUuidManager(binder) : null;
        mPps.setUuidManager(uuidManager);
    }

    @Override
    public IBinder getPluginLoader() throws RemoteException {
        return mPps.getPluginLoader();
    }

    @Override
    public PpsStatus getPpsStatus() throws RemoteException {
        return mPps.getPpsStatus();
    }

    @Override
    public void loadRuntime(String uuid) throws RemoteException, FailedException {
        mPps.loadRuntime(uuid);
    }

    @Override
    public void loadPluginLoader(String uuid) throws RemoteException, FailedException {
        mPps.loadPluginLoader(uuid);
    }
}
