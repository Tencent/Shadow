package com.tencent.cubershi.plugin_loader.infos

import android.os.Parcel
import android.os.Parcelable

data class PluginActivityInfo(val className: String, val themeResource: Int) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readInt())

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(className)
        dest.writeInt(themeResource)
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