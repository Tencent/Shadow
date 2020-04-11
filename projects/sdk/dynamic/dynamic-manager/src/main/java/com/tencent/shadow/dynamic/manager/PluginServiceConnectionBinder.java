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
                final ComponentName name;
                if (0 != data.readInt()) {
                    name = ComponentName.CREATOR.createFromParcel(data);
                } else {
                    name = null;
                }
                IBinder service;
                service = data.readStrongBinder();
                mPsc.onServiceConnected(name, service);

                service.linkToDeath(new DeathRecipient() {
                    @Override
                    public void binderDied() {
                        mPsc.onServiceDisconnected(name);
                    }
                }, 0);

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
