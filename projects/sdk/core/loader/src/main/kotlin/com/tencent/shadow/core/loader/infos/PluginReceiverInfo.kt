package com.tencent.shadow.core.loader.infos

import android.content.pm.ActivityInfo
import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator

/**
 * 插件广播数据
 * @author xuedizi2009@163.com
 */
class PluginReceiverInfo(
    className: String?,
    private val activityInfo: ActivityInfo?,
    val actions: List<String>?
) : Parcelable, PluginComponentInfo(className) {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readParcelable(ActivityInfo::class.java.classLoader),
        parcel.createStringArrayList()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(className)
        parcel.writeParcelable(activityInfo, flags)
        parcel.writeStringList(actions)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Creator<PluginReceiverInfo> {
        override fun createFromParcel(parcel: Parcel): PluginReceiverInfo {
            return PluginReceiverInfo(parcel)
        }

        override fun newArray(size: Int): Array<PluginReceiverInfo?> {
            return arrayOfNulls(size)
        }
    }
}