package com.tencent.shadow.core.loader.infos

import android.content.pm.ProviderInfo
import android.os.Parcel
import android.os.Parcelable

class PluginProviderInfo(className: String, val authority: String, val providerInfo: ProviderInfo) : Parcelable, PluginComponentInfo(className) {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readParcelable(ProviderInfo::class.java.classLoader)) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(className)
        parcel.writeString(authority)
        parcel.writeParcelable(providerInfo, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PluginProviderInfo> {
        override fun createFromParcel(parcel: Parcel): PluginProviderInfo {
            return PluginProviderInfo(parcel)
        }

        override fun newArray(size: Int): Array<PluginProviderInfo?> {
            return arrayOfNulls(size)
        }
    }
}