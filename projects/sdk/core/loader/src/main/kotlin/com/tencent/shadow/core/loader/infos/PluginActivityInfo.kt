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

package com.tencent.shadow.core.loader.infos

import android.content.pm.ActivityInfo
import android.os.Parcel
import android.os.Parcelable

class PluginActivityInfo(className: String?, val themeResource: Int, val activityInfo: ActivityInfo?) : Parcelable, PluginComponentInfo(className) {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readInt(),
            parcel.readParcelable(ActivityInfo::class.java.classLoader)) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(className)
        parcel.writeInt(themeResource)
        parcel.writeParcelable(activityInfo, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PluginActivityInfo> {
        override fun createFromParcel(parcel: Parcel): PluginActivityInfo {
            return PluginActivityInfo(parcel)
        }

        override fun newArray(size: Int): Array<PluginActivityInfo?> {
            return arrayOfNulls(size)
        }
    }
}