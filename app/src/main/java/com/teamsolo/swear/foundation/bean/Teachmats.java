package com.teamsolo.swear.foundation.bean;

import android.os.Parcel;

import com.teamsolo.base.bean.Bean;

import java.util.ArrayList;

/**
 * description Teaching materials of a grade
 * author Melo Chan
 * date 2016/12/26
 * version 0.0.0.1
 */
@SuppressWarnings("WeakerAccess, unused")
public class Teachmats extends Bean {

    public String gradeId;

    public String gradeName;

    public ArrayList<Teachmat> teachingMaterialsList = new ArrayList<>();

    public Teachmats() {

    }

    private Teachmats(Parcel in) {
        gradeId = in.readString();
        gradeName = in.readString();
        in.readTypedList(teachingMaterialsList, Teachmat.CREATOR);
    }

    public static final Creator<Teachmats> CREATOR = new Creator<Teachmats>() {
        @Override
        public Teachmats createFromParcel(Parcel source) {
            return new Teachmats(source);
        }

        @Override
        public Teachmats[] newArray(int size) {
            return new Teachmats[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(gradeId);
        dest.writeString(gradeName);
        dest.writeTypedList(teachingMaterialsList);
    }
}
