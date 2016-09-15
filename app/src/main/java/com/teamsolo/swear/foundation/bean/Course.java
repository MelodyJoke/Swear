package com.teamsolo.swear.foundation.bean;

import android.os.Parcel;

import com.teamsolo.base.bean.Bean;

/**
 * description: course bean
 * author: Melody
 * date: 2016/9/15
 * version: 0.0.0.1
 */
@SuppressWarnings("WeakerAccess, unused")
public class Course extends Bean {

    public long classHour;

    public String courseId;

    public String courseMoney;

    public String courseName;

    public String logoPath;

    public String money;

    public int payType;

    public Course() {

    }

    private Course(Parcel in) {
        classHour = in.readLong();
        courseId = in.readString();
        courseMoney = in.readString();
        courseName = in.readString();
        logoPath = in.readString();
        money = in.readString();
        payType = in.readInt();
    }

    public static final Creator<Course> CREATOR = new Creator<Course>() {
        @Override
        public Course createFromParcel(Parcel source) {
            return new Course(source);
        }

        @Override
        public Course[] newArray(int size) {
            return new Course[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(classHour);
        dest.writeString(courseId);
        dest.writeString(courseMoney);
        dest.writeString(courseName);
        dest.writeString(logoPath);
        dest.writeString(money);
        dest.writeInt(payType);
    }
}
