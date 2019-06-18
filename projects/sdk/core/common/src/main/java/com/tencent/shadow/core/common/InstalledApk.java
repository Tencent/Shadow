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

package com.tencent.shadow.core.common;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 安装完成的apk
 */
public class InstalledApk implements Parcelable {

    public final String apkFilePath;

    public final String oDexPath;

    public final String libraryPath;

    public final byte[] parcelExtras;

    public InstalledApk(String apkFilePath, String oDexPath, String libraryPath) {
        this(apkFilePath, oDexPath, libraryPath, null);
    }

    public InstalledApk(String apkFilePath, String oDexPath, String libraryPath, byte[] parcelExtras) {
        this.apkFilePath = apkFilePath;
        this.oDexPath = oDexPath;
        this.libraryPath = libraryPath;
        this.parcelExtras = parcelExtras;
    }

    protected InstalledApk(Parcel in) {
        apkFilePath = in.readString();
        oDexPath = in.readString();
        libraryPath = in.readString();
        int parcelExtrasLength = in.readInt();
        if (parcelExtrasLength > 0) {
            parcelExtras = new byte[parcelExtrasLength];
        } else {
            parcelExtras = null;
        }
        if (parcelExtras != null) {
            in.readByteArray(parcelExtras);
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(apkFilePath);
        dest.writeString(oDexPath);
        dest.writeString(libraryPath);
        dest.writeInt(parcelExtras == null ? 0 : parcelExtras.length);
        if (parcelExtras != null) {
            dest.writeByteArray(parcelExtras);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<InstalledApk> CREATOR = new Creator<InstalledApk>() {
        @Override
        public InstalledApk createFromParcel(Parcel in) {
            return new InstalledApk(in);
        }

        @Override
        public InstalledApk[] newArray(int size) {
            return new InstalledApk[size];
        }
    };
}
