package com.teamsolo.swear.foundation.bean;

import android.os.Parcel;

import com.teamsolo.base.bean.Bean;

/**
 * description: grade bean
 * author: Melody
 * date: 2016/9/23
 * version: 0.0.0.1
 */
@SuppressWarnings("WeakerAccess, unused")
public class Grade extends Bean {

    public String gradeFullName;

    public int gradeId;

    public int gradeLevel;

    public String gradeName;

    public int gradeType;

    public Grade() {

    }

    private Grade(Parcel in) {
        gradeFullName = in.readString();
        gradeId = in.readInt();
        gradeLevel = in.readInt();
        gradeName = in.readString();
        gradeType = in.readInt();
    }

    public static final Creator<Grade> CREATOR = new Creator<Grade>() {
        @Override
        public Grade createFromParcel(Parcel source) {
            return new Grade(source);
        }

        @Override
        public Grade[] newArray(int size) {
            return new Grade[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(gradeFullName);
        dest.writeInt(gradeId);
        dest.writeInt(gradeLevel);
        dest.writeString(gradeName);
        dest.writeInt(gradeType);
    }

    // about ui
    public boolean isChecked;
}
