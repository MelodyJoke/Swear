package com.teamsolo.swear.foundation.bean;

import android.os.Parcel;

import com.teamsolo.base.bean.Bean;

/**
 * description: agency bean
 * author: Melody
 * date: 2016/9/12
 * version: 0.0.0.1
 */
@SuppressWarnings("WeakerAccess, unused")
public class Agency extends Bean {

    public String address;

    public byte isCooperate;

    public long overallComment;

    public String schoolId;

    public String schoolImagePath;

    public String schoolName;

    public Agency() {

    }

    private Agency(Parcel in) {
        address = in.readString();
        isCooperate = in.readByte();
        overallComment = in.readLong();
        schoolId = in.readString();
        schoolImagePath = in.readString();
        schoolName = in.readString();
    }

    public static final Creator<Agency> CREATOR = new Creator<Agency>() {
        @Override
        public Agency createFromParcel(Parcel source) {
            return new Agency(source);
        }

        @Override
        public Agency[] newArray(int size) {
            return new Agency[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(address);
        dest.writeByte(isCooperate);
        dest.writeLong(overallComment);
        dest.writeString(schoolId);
        dest.writeString(schoolImagePath);
        dest.writeString(schoolName);
    }
}
