package com.tencent.shadow.loader.infos

import android.content.pm.ActivityInfo
import android.os.Parcel
import android.os.Parcelable

data class PluginActivityInfo(val className: String, val themeResource: Int, val activityInfo: ActivityInfo) : Parcelable {
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