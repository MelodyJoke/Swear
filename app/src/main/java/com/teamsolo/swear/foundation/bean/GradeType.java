package com.teamsolo.swear.foundation.bean;

import android.os.Parcel;

import com.teamsolo.base.bean.Bean;

import java.util.ArrayList;

/**
 * description: grade type bean
 * author: Melody
 * date: 2016/9/23
 * version: 0.0.0.1
 */
@SuppressWarnings("WeakerAccess, unused")
public class GradeType extends Bean {

    public String gradeTpyeName;

    public int gradeType;

    public ArrayList<Grade> grades = new ArrayList<>();

    public GradeType() {

    }

    private GradeType(Parcel in) {
        gradeTpyeName = in.readString();
        gradeType = in.readInt();
        in.readTypedList(grades, Grade.CREATOR);
    }

    public static final Creator<GradeType> CREATOR = new Creator<GradeType>() {
        @Override
        public GradeType createFromParcel(Parcel source) {
            return new GradeType(source);
        }

        @Override
        public GradeType[] newArray(int size) {
            return new GradeType[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(gradeTpyeName);
        dest.writeInt(gradeType);
        dest.writeTypedList(grades);
    }
}
