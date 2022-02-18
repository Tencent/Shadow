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

internal class PluginLoaderBinder(private val mDynamicPluginLoader: DynamicPluginLoader) :
    android.os.Binder(), PluginLoaderImpl {
    override fun setUuidManager(uuidManager: UuidManager?) {
        mDynamicPluginLoader.setUuidManager(uuidManager)
    }

    @Throws(android.os.RemoteException::class)
    public override fun onTransact(
        code: Int,
        data: android.os.Parcel,
        reply: android.os.Parcel?,
        flags: Int
    ): Boolean {
        if (reply == null) {
            throw NullPointerException("reply == null")
        }
        when (code) {
            IBinder.INTERFACE_TRANSACTION -> {
                reply.writeString(PluginLoader.DESCRIPTOR)
                return true
            }
            PluginLoader.TRANSACTION_loadPlugin -> {
                data.enforceInterface(PluginLoader.DESCRIPTOR)
                val _arg0: String
                _arg0 = data.readString()!!
                try {
                    mDynamicPluginLoader.loadPlugin(_arg0)
                    reply.writeNoException()
                } catch (e: Exception) {
                    reply.writeException(wrapExceptionForBinder(e))
                }
                return true
            }
            PluginLoader.TRANSACTION_getLoadedPlugin -> {
                data.enforceInterface(PluginLoader.DESCRIPTOR)
                try {
                    val _result = mDynamicPluginLoader.getLoadedPlugin()
                    reply.writeNoException()
                    reply.writeMap(_result as Map<*, *>?)
                } catch (e: Exception) {
                    reply.writeException(wrapExceptionForBinder(e))
                }

                return true
            }
            PluginLoader.TRANSACTION_callApplicationOnCreate -> {
                data.enforceInterface(PluginLoader.DESCRIPTOR)
                val _arg0: String
                _arg0 = data.readString()!!
                try {
                    mDynamicPluginLoader.callApplicationOnCreate(_arg0)
                    reply.writeNoException()
                } catch (e: Exception) {
                    reply.writeException(wrapExceptionForBinder(e))
                }

                return true
            }
            PluginLoader.TRANSACTION_convertActivityIntent -> {
                data.enforceInterface(PluginLoader.DESCRIPTOR)
                val intent = if (0 != data.readInt()) {
                    Intent.CREATOR.createFromParcel(data)
                } else {
                    reply.writeException(NullPointerException("intent==null"))
                    return true
                }

                try {
                    val _result =
                        mDynamicPluginLoader.convertActivityIntent(intent)
                    reply.writeNoException()
                    if (_result != null) {
                        reply.writeInt(1)
                        _result.writeToParcel(
                            reply,
                            android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE
                        )
                    } else {
                        reply.writeInt(0)
                    }
                } catch (e: Exception) {
                    reply.writeException(wrapExceptionForBinder(e))
                }
                return true
            }
            PluginLoader.TRANSACTION_startPluginService -> {
                data.enforceInterface(PluginLoader.DESCRIPTOR)
                val intent = if (0 != data.readInt()) {
                    Intent.CREATOR.createFromParcel(data)
                } else {
                    reply.writeException(NullPointerException("intent==null"))
                    return true
                }

                try {
                    val _result = mDynamicPluginLoader.startPluginService(intent)
                    reply.writeNoException()
                    if (_result != null) {
                        reply.writeInt(1)
                        _result.writeToParcel(
                            reply,
                            android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE
                        )
                    } else {
                        reply.writeInt(0)
                    }
                } catch (e: Exception) {
                    reply.writeException(wrapExceptionForBinder(e))
                }
                return true
            }
            PluginLoader.TRANSACTION_stopPluginService -> {
                data.enforceInterface(PluginLoader.DESCRIPTOR)
                val intent = if (0 != data.readInt()) {
                    Intent.CREATOR.createFromParcel(data)
                } else {
                    reply.writeException(NullPointerException("intent==null"))
                    return true
                }
                try {
                    val _result = mDynamicPluginLoader.stopPluginService(intent)
                    reply.writeNoException()
                    reply.writeInt(if (_result) 1 else 0)
                } catch (e: Exception) {
                    reply.writeException(wrapExceptionForBinder(e))
                }
                return true
            }
            PluginLoader.TRANSACTION_bindPluginService -> {
                data.enforceInterface(PluginLoader.DESCRIPTOR)
                val intent = if (0 != data.readInt()) {
                    Intent.CREATOR.createFromParcel(data)
                } else {
                    reply.writeException(NullPointerException("intent==null"))
                    return true
                }
                val _arg1 = BinderPluginServiceConnection(data.readStrongBinder())
                val _arg2: Int
                _arg2 = data.readInt()
                try {
                    val _result = mDynamicPluginLoader.bindPluginService(
                        intent,
                        _arg1,
                        _arg2
                    )
                    reply.writeNoException()
                    reply.writeInt(if (_result) 1 else 0)
                } catch (e: Exception) {
                    reply.writeException(wrapExceptionForBinder(e))
                }
                return true
            }
            PluginLoader.TRANSACTION_unbindService -> {
                data.enforceInterface(PluginLoader.DESCRIPTOR)
                try {
                    mDynamicPluginLoader.unbindService(data.readStrongBinder())
                    reply.writeNoException()
                } catch (e: Exception) {
                    reply.writeException(wrapExceptionForBinder(e))
                }
                return true
            }
            PluginLoader.TRANSACTION_startActivityInPluginProcess -> {
                data.enforceInterface(PluginLoader.DESCRIPTOR)
                try {
                    mDynamicPluginLoader.startActivityInPluginProcess(
                        Intent.CREATOR.createFromParcel(
                            data
                        )
                    )
                    reply.writeNoException()
                } catch (e: Exception) {
                    reply.writeException(wrapExceptionForBinder(e))
                }
                return true
            }
        }
        return super.onTransact(code, data, reply, flags)
    }

    /**
     * Binder的内置writeException方法只支持特定几种Exception
     * https://developer.android.com/reference/android/os/Parcel.html#writeException(java.lang.Exception)
     */
    private fun wrapExceptionForBinder(e: Exception): Exception {
        return IllegalStateException(e.message, e.cause)
    }

}
