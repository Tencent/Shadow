package com.tencent.shadow.dynamic.host;

import android.os.Parcel;

import com.tencent.shadow.core.interface_.InstalledApk;

/**
 * 安装完成的apk
 */
public class InstalledPart extends InstalledApk {

    final public String partKey;

    final public int partType;

    final public String UUID;

    final public String[] dependsOn;

    public InstalledPart(
            String UUID,
            String partKey,
            int partType,
            String filePath,
            String oDexPath,
            String libraryPath,
            String[] dependsOn
    ) {
        super(filePath, oDexPath, libraryPath);
        this.UUID = UUID;
        this.partKey = partKey;
        this.partType = partType;
        this.dependsOn = dependsOn;
    }


    @Override
    public int describeContents() {
        return super.describeContents();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.partKey);
        dest.writeInt(this.partType);
        dest.writeString(this.UUID);
        dest.writeStringArray(this.dependsOn);
    }

    protected InstalledPart(Parcel in) {
        super(in);
        this.partKey = in.readString();
        this.partType = in.readInt();
        this.UUID = in.readString();
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
