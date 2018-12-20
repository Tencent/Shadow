package com.tencent.shadow.core.interface_;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 安装完成的apk
 */
public class InstalledApk implements Parcelable {

    public final String apkFilePath;

    public final String oDexPath;

    public final String libraryPath;

    public InstalledApk(String apkFilePath, String oDexPath, String libraryPath) {
        this.apkFilePath = apkFilePath;
        this.oDexPath = oDexPath;
        this.libraryPath = libraryPath;
    }

    protected InstalledApk(Parcel in) {
        apkFilePath = in.readString();
        oDexPath = in.readString();
        libraryPath = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(apkFilePath);
        dest.writeString(oDexPath);
        dest.writeString(libraryPath);
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
