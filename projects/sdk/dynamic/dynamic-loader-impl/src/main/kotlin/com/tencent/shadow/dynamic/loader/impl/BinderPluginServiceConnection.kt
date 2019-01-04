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
