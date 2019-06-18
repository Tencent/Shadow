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

public final class PpsStatus implements Parcelable {
    final public String uuid;
    final public boolean runtimeLoaded;
    final public boolean loaderLoaded;
    final public boolean uuidManagerSet;

    PpsStatus(String uuid, boolean runtimeLoaded, boolean loaderLoaded, boolean uuidManagerSet) {
        this.uuid = uuid;
        this.runtimeLoaded = runtimeLoaded;
        this.loaderLoaded = loaderLoaded;
        this.uuidManagerSet = uuidManagerSet;
    }

    PpsStatus(Parcel in) {
        uuid = in.readString();
        runtimeLoaded = in.readByte() != 0;
        loaderLoaded = in.readByte() != 0;
        uuidManagerSet = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(uuid);
        dest.writeByte((byte) (runtimeLoaded ? 1 : 0));
        dest.writeByte((byte) (loaderLoaded ? 1 : 0));
        dest.writeByte((byte) (uuidManagerSet ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PpsStatus> CREATOR = new Creator<PpsStatus>() {
        @Override
        public PpsStatus createFromParcel(Parcel in) {
            return new PpsStatus(in);
        }

        @Override
        public PpsStatus[] newArray(int size) {
            return new PpsStatus[size];
        }
    };
}
