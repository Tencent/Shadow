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

package com.tencent.shadow.dynamic.loader.impl

import android.content.ComponentName
import android.os.IBinder
import android.os.Parcel
import android.os.RemoteException
import com.tencent.shadow.dynamic.loader.PluginServiceConnection

class BinderPluginServiceConnection internal constructor(internal val mRemote: IBinder) {

    @Throws(RemoteException::class)
    fun onServiceConnected(name: ComponentName?, service: IBinder) {
        val _data = Parcel.obtain()
        val _reply = Parcel.obtain()
        try {
            _data.writeInterfaceToken(PluginServiceConnection.DESCRIPTOR)
            if (name != null) {
                _data.writeInt(1)
                name.writeToParcel(_data, 0)
            } else {
                _data.writeInt(0)
            }
            _data.writeStrongBinder(service)
            mRemote.transact(PluginServiceConnection.TRANSACTION_onServiceConnected, _data, _reply, 0)
            _reply.readException()
        } finally {
            _reply.recycle()
            _data.recycle()
        }
    }

    @Throws(RemoteException::class)
    fun onServiceDisconnected(name: ComponentName?) {
        val _data = Parcel.obtain()
        val _reply = Parcel.obtain()
        try {
            _data.writeInterfaceToken(PluginServiceConnection.DESCRIPTOR)
            if (name != null) {
                _data.writeInt(1)
                name.writeToParcel(_data, 0)
            } else {
                _data.writeInt(0)
            }
            mRemote.transact(PluginServiceConnection.TRANSACTION_onServiceDisconnected, _data, _reply, 0)
            _reply.readException()
        } finally {
            _reply.recycle()
            _data.recycle()
        }
    }
}
