package com.teamsolo.swear.foundation.bean;

import android.os.Parcel;

import com.teamsolo.base.bean.Bean;

import java.util.ArrayList;

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

    public int overallComment;

    public String schoolId;

    public String schoolImagePath;

    public String schoolName;

    public String customerPhone;

    public double educate;

    public double teachQuality;

    public double teacherLevel;

    public String lat;

    public String lng;

    public ArrayList<String> schoolImages = new ArrayList<>();

    public String schoolProfile;

    public String schoolShortName;

    public Agency() {

    }

    private Agency(Parcel in) {
        address = in.readString();
        isCooperate = in.readByte();
        overallComment = in.readInt();
        schoolId = in.readString();
        schoolImagePath = in.readString();
        schoolName = in.readString();
        customerPhone = in.readString();
        educate = in.readDouble();
        teachQuality = in.readDouble();
        teacherLevel = in.readDouble();
        lat = in.readString();
        lng = in.readString();
        in.readStringList(schoolImages);
        schoolProfile = in.readString();
        schoolShortName = in.readString();
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
        dest.writeInt(overallComment);
        dest.writeString(schoolId);
        dest.writeString(schoolImagePath);
        dest.writeString(schoolName);
        dest.writeString(customerPhone);
        dest.writeDouble(educate);
        dest.writeDouble(teachQuality);
        dest.writeDouble(teacherLevel);
        dest.writeString(lat);
        dest.writeString(lng);
        dest.writeStringList(schoolImages);
        dest.writeString(schoolProfile);
        dest.writeString(schoolShortName);
    }

    public Agency merge(Agency detail) {
        this.customerPhone = detail.customerPhone;
        this.educate = detail.educate;
        this.teachQuality = detail.teachQuality;
        this.teacherLevel = detail.teacherLevel;
        this.lat = detail.lat;
        this.lng = detail.lng;
        this.schoolImages = detail.schoolImages;
        this.schoolProfile = detail.schoolProfile;
        this.schoolShortName = detail.schoolShortName;

        return this;
    }
}
