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
