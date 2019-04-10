package com.tencent.shadow.core.loader;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 这个类是Loader中复制过来的，需要和Loader中的保持一致。
 * todo #26 这个类要抽出一个单独的模块避免这种一个文件写两份的问题
 */
public class LoadParametersInManager implements Parcelable {
    public final String partKey;
    public final int pluginFileType;
    public final String[] dependsOn;

    public LoadParametersInManager(String partKey, int pluginFileType, String[] dependsOn) {
        this.partKey = partKey;
        this.pluginFileType = pluginFileType;
        this.dependsOn = dependsOn;
    }

    protected LoadParametersInManager(Parcel in) {
        partKey = in.readString();
        pluginFileType = in.readInt();
        dependsOn = in.createStringArray();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(partKey);
        dest.writeInt(pluginFileType);
        dest.writeStringArray(dependsOn);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<LoadParametersInManager> CREATOR = new Creator<LoadParametersInManager>() {
        @Override
        public LoadParametersInManager createFromParcel(Parcel in) {
            return new LoadParametersInManager(in);
        }

        @Override
        public LoadParametersInManager[] newArray(int size) {
            return new LoadParametersInManager[size];
        }
    };
}
