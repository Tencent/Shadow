package com.tencent.shadow.dynamic.host;

import android.os.Parcel;
import android.os.Parcelable;

public class NotFoundException extends Exception implements Parcelable {
    public NotFoundException(String message) {
        super(message);
    }

    protected NotFoundException(Parcel in) {
        super(in.readString());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getMessage());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<NotFoundException> CREATOR = new Creator<NotFoundException>() {
        @Override
        public NotFoundException createFromParcel(Parcel in) {
            return new NotFoundException(in);
        }

        @Override
        public NotFoundException[] newArray(int size) {
            return new NotFoundException[size];
        }
    };
}
