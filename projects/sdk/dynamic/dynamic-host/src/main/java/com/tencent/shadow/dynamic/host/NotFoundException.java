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

public class NotFoundException extends Exception implements Parcelable {
    public NotFoundException(String message) {
        super(message);
    }

    protected NotFoundException(Parcel in) {
        super(in.readString());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getMessage());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<NotFoundException> CREATOR = new Creator<NotFoundException>() {
        @Override
        public NotFoundException createFromParcel(Parcel in) {
            return new NotFoundException(in);
        }

        @Override
        public NotFoundException[] newArray(int size) {
            return new NotFoundException[size];
        }
    };
}
