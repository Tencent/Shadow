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

import android.content.Intent
import android.os.IBinder
import com.tencent.shadow.dynamic.host.PluginLoaderImpl
import com.tencent.shadow.dynamic.host.UuidManager
import com.tencent.shadow.dynamic.loader.PluginLoader

internal class PluginLoaderBinder(private val mDynamicPluginLoader: DynamicPluginLoader) : android.os.Binder(), PluginLoaderImpl {
    override fun setUuidManager(uuidManager: UuidManager?) {
        mDynamicPluginLoader.setUuidManager(uuidManager)
    }

    @Throws(android.os.RemoteException::class)
    public override fun onTransact(code: Int, data: android.os.Parcel, reply: android.os.Parcel?, flags: Int): Boolean {
        when (code) {
            IBinder.INTERFACE_TRANSACTION -> {
                reply!!.writeString(PluginLoader.DESCRIPTOR)
                return true
            }
            PluginLoader.TRANSACTION_loadPlugin -> {
                data.enforceInterface(PluginLoader.DESCRIPTOR)
                val _arg0: String
                _arg0 = data.readString()!!
                mDynamicPluginLoader.loadPlugin(_arg0)
                reply!!.writeNoException()
                return true
            }
            PluginLoader.TRANSACTION_getLoadedPlugin -> {
                data.enforceInterface(PluginLoader.DESCRIPTOR)
                val _result = mDynamicPluginLoader.getLoadedPlugin()
                reply!!.writeNoException()
                reply.writeMap(_result as Map<*, *>?)
                return true
            }
            PluginLoader.TRANSACTION_callApplicationOnCreate -> {
                data.enforceInterface(PluginLoader.DESCRIPTOR)
                val _arg0: String
                _arg0 = data.readString()!!
                mDynamicPluginLoader.callApplicationOnCreate(_arg0)
                reply!!.writeNoException()
                return true
            }
            PluginLoader.TRANSACTION_convertActivityIntent -> {
                data.enforceInterface(PluginLoader.DESCRIPTOR)
                val _arg0: android.content.Intent?
                if (0 != data.readInt()) {
                    _arg0 = android.content.Intent.CREATOR.createFromParcel(data)
                } else {
                    _arg0 = null
                }
                val _result = mDynamicPluginLoader.convertActivityIntent(_arg0!!)//todo #32 去掉这个不安全的!!
                reply!!.writeNoException()
                if (_result != null) {
                    reply.writeInt(1)
                    _result.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE)
                } else {
                    reply.writeInt(0)
                }
                return true
            }
            PluginLoader.TRANSACTION_startPluginService -> {
                data.enforceInterface(PluginLoader.DESCRIPTOR)
                val _arg0: android.content.Intent?
                if (0 != data.readInt()) {
                    _arg0 = android.content.Intent.CREATOR.createFromParcel(data)
                } else {
                    _arg0 = null
                }
                val _result = mDynamicPluginLoader.startPluginService(_arg0!!)//todo #32 去掉这个不安全的!!
                reply!!.writeNoException()
                if (_result != null) {
                    reply.writeInt(1)
                    _result.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE)
                } else {
                    reply.writeInt(0)
                }
                return true
            }
            PluginLoader.TRANSACTION_stopPluginService -> {
                data.enforceInterface(PluginLoader.DESCRIPTOR)
                val _arg0: android.content.Intent?
                if (0 != data.readInt()) {
                    _arg0 = android.content.Intent.CREATOR.createFromParcel(data)
                } else {
                    _arg0 = null
                }
                val _result = mDynamicPluginLoader.stopPluginService(_arg0!!)//todo #32 去掉这个不安全的!!
                reply!!.writeNoException()
                reply.writeInt(if (_result) 1 else 0)
                return true
            }
            PluginLoader.TRANSACTION_bindPluginService -> {
                data.enforceInterface(PluginLoader.DESCRIPTOR)
                val _arg0: android.content.Intent?
                if (0 != data.readInt()) {
                    _arg0 = android.content.Intent.CREATOR.createFromParcel(data)
                } else {
                    _arg0 = null
                }
                val _arg1 = BinderPluginServiceConnection(data.readStrongBinder())
                val _arg2: Int
                _arg2 = data.readInt()
                val _result = mDynamicPluginLoader.bindPluginService(_arg0!!, _arg1, _arg2)//todo #32 去掉这个不安全的!!
                reply!!.writeNoException()
                reply.writeInt(if (_result) 1 else 0)
                return true
            }
            PluginLoader.TRANSACTION_unbindService -> {
                data.enforceInterface(PluginLoader.DESCRIPTOR)
                mDynamicPluginLoader.unbindService(data.readStrongBinder())
                reply!!.writeNoException()
                return true
            }
            PluginLoader.TRANSACTION_startActivityInPluginProcess -> {
                data.enforceInterface(PluginLoader.DESCRIPTOR)
                mDynamicPluginLoader.startActivityInPluginProcess(Intent.CREATOR.createFromParcel(data))
                reply!!.writeNoException()
                return true
            }
        }
        return super.onTransact(code, data, reply, flags)
    }
}
