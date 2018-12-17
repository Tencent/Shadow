package com.tencent.shadow.dynamic.host;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 安装完成的apk
 */
public class InstalledPart implements Parcelable {

    public String partKey;

    public int partType;

    public String UUID;

    public String filePath;

    public String oDexPath;

    public String libraryPath;

    public String[] dependsOn;

    public InstalledPart(String UUID, String partKey, int partType, String filePath, String oDexPath, String libraryPath, String[] dependsOn) {
        this.UUID = UUID;
        this.partKey = partKey;
        this.filePath = filePath;
        this.oDexPath = oDexPath;
        this.libraryPath = libraryPath;
        this.partType = partType;
        this.dependsOn = dependsOn;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.partKey);
        dest.writeInt(this.partType);
        dest.writeString(this.UUID);
        dest.writeString(this.filePath);
        dest.writeString(this.oDexPath);
        dest.writeString(this.libraryPath);
        dest.writeStringArray(this.dependsOn);
    }

    protected InstalledPart(Parcel in) {
        this.partKey = in.readString();
        this.partType = in.readInt();
        this.UUID = in.readString();
        this.filePath = in.readString();
        this.oDexPath = in.readString();
        this.libraryPath = in.readString();
        this.dependsOn = in.createStringArray();
    }

    public static final Creator<InstalledPart> CREATOR = new Creator<InstalledPart>() {
        @Override
        public InstalledPart createFromParcel(Parcel source) {
            return new InstalledPart(source);
        }

        @Override
        public InstalledPart[] newArray(int size) {
            return new InstalledPart[size];
        }
    };
}
