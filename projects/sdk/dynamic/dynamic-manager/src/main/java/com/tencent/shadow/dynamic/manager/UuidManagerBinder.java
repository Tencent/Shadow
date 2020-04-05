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

import android.os.Parcel;

import com.tencent.shadow.core.common.InstalledApk;
import com.tencent.shadow.dynamic.host.FailedException;
import com.tencent.shadow.dynamic.host.NotFoundException;
import com.tencent.shadow.dynamic.host.UuidManager;

class UuidManagerBinder extends android.os.Binder {

    final private UuidManagerImpl mUuidManager;

    UuidManagerBinder(UuidManagerImpl uuidManager) {
        mUuidManager = uuidManager;
    }

    @Override
    public boolean onTransact(int code, Parcel data, Parcel reply, int flags) {
        switch (code) {
            case INTERFACE_TRANSACTION: {
                reply.writeString(UuidManager.DESCRIPTOR);
                return true;
            }
            case UuidManager.TRANSACTION_getPlugin: {
                data.enforceInterface(UuidManager.DESCRIPTOR);
                String _arg0;
                _arg0 = data.readString();
                String _arg1;
                _arg1 = data.readString();
                try {
                    InstalledApk _result = mUuidManager.getPlugin(_arg0, _arg1);
                    reply.writeInt(UuidManager.TRANSACTION_CODE_NO_EXCEPTION);
                    if ((_result != null)) {
                        reply.writeInt(1);
                        _result.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
                    } else {
                        reply.writeInt(0);
                    }
                } catch (NotFoundException e) {
                    reply.writeInt(UuidManager.TRANSACTION_CODE_NOT_FOUND_EXCEPTION);
                    e.writeToParcel(reply, 0);
                } catch (FailedException e) {
                    reply.writeInt(UuidManager.TRANSACTION_CODE_FAILED_EXCEPTION);
                    e.writeToParcel(reply, 0);
                }
                return true;
            }
            case UuidManager.TRANSACTION_getPluginLoader: {
                data.enforceInterface(UuidManager.DESCRIPTOR);
                String _arg0;
                _arg0 = data.readString();
                try {
                    InstalledApk _result = mUuidManager.getPluginLoader(_arg0);
                    reply.writeInt(UuidManager.TRANSACTION_CODE_NO_EXCEPTION);
                    if ((_result != null)) {
                        reply.writeInt(1);
                        _result.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
                    } else {
                        reply.writeInt(0);
                    }
                } catch (NotFoundException e) {
                    reply.writeInt(UuidManager.TRANSACTION_CODE_NOT_FOUND_EXCEPTION);
                    e.writeToParcel(reply, 0);
                } catch (FailedException e) {
                    reply.writeInt(UuidManager.TRANSACTION_CODE_FAILED_EXCEPTION);
                    e.writeToParcel(reply, 0);
                }
                return true;
            }
            case UuidManager.TRANSACTION_getRuntime: {
                data.enforceInterface(UuidManager.DESCRIPTOR);
                String _arg0;
                _arg0 = data.readString();
                try {
                    InstalledApk _result = mUuidManager.getRuntime(_arg0);
                    reply.writeInt(UuidManager.TRANSACTION_CODE_NO_EXCEPTION);
                    if ((_result != null)) {
                        reply.writeInt(1);
                        _result.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
                    } else {
                        reply.writeInt(0);
                    }
                } catch (NotFoundException e) {
                    reply.writeInt(UuidManager.TRANSACTION_CODE_NOT_FOUND_EXCEPTION);
                    e.writeToParcel(reply, 0);
                } catch (FailedException e) {
                    reply.writeInt(UuidManager.TRANSACTION_CODE_FAILED_EXCEPTION);
                    e.writeToParcel(reply, 0);
                }
                return true;
            }
            default:
                return false;
        }
    }

}
