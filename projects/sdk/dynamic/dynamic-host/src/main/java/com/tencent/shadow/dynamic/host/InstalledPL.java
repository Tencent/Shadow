package com.tencent.shadow.dynamic.host;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 安装完成的pluginloader 或者 runtime
 */
public class InstalledPL implements Parcelable {

    public String UUID;

    public String filePath;

    public String oDexPath;

    public String libraryPath;

    public InstalledPL(String UUID, String filePath, String oDexPath, String libraryPath) {
        this.UUID = UUID;
        this.filePath = filePath;
        this.oDexPath = oDexPath;
        this.libraryPath = libraryPath;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.UUID);
        dest.writeString(this.filePath);
        dest.writeString(this.oDexPath);
        dest.writeString(this.libraryPath);
    }

    public InstalledPL() {
    }

    protected InstalledPL(Parcel in) {
        this.UUID = in.readString();
        this.filePath = in.readString();
        this.oDexPath = in.readString();
        this.libraryPath = in.readString();
    }

    public static final Parcelable.Creator<InstalledPL> CREATOR = new Parcelable.Creator<InstalledPL>() {
        @Override
        public InstalledPL createFromParcel(Parcel source) {
            return new InstalledPL(source);
        }

        @Override
        public InstalledPL[] newArray(int size) {
            return new InstalledPL[size];
        }
    };
}
