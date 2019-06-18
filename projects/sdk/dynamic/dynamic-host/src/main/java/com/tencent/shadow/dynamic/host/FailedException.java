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

import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteException;

public class FailedException extends Exception implements Parcelable {
    public static final int ERROR_CODE_REMOTE_EXCEPTION = 1;
    public static final int ERROR_CODE_RUNTIME_EXCEPTION = 2;
    public static final int ERROR_CODE_FILE_NOT_FOUND_EXCEPTION = 3;
    public static final int ERROR_CODE_UUID_MANAGER_NULL_EXCEPTION = 4;
    public static final int ERROR_CODE_UUID_MANAGER_DEAD_EXCEPTION = 5;
    public static final int ERROR_CODE_RESET_UUID_EXCEPTION = 6;
    public static final int ERROR_CODE_RELOAD_RUNTIME_EXCEPTION = 7;
    public static final int ERROR_CODE_RELOAD_LOADER_EXCEPTION = 8;

    public final int errorCode;
    public final String errorMessage;

    public FailedException(RemoteException e) {
        this.errorCode = ERROR_CODE_REMOTE_EXCEPTION;
        this.errorMessage = e.getClass().getSimpleName() + ":" + e.getMessage();
    }

    public FailedException(RuntimeException e) {
        this.errorCode = ERROR_CODE_RUNTIME_EXCEPTION;
        this.errorMessage = e.getClass().getSimpleName() + ":" + e.getMessage();
    }

    public FailedException(int errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    protected FailedException(Parcel in) {
        errorCode = in.readInt();
        errorMessage = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(errorCode);
        dest.writeString(errorMessage);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<FailedException> CREATOR = new Creator<FailedException>() {
        @Override
        public FailedException createFromParcel(Parcel in) {
            return new FailedException(in);
        }

        @Override
        public FailedException[] newArray(int size) {
            return new FailedException[size];
        }
    };
}
