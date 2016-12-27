package com.teamsolo.swear.foundation.bean;

import android.os.Parcel;

import com.teamsolo.base.bean.Bean;

/**
 * description Unit of english follow
 * author Melo Chan
 * date 2016/12/26
 * version 0.0.0.1
 */
@SuppressWarnings("WeakerAccess, unused")
public class Unit extends Bean {

    public String id;

    public String unitName;

    public String unitNumber;

    public String coverImagePath;

    public String startPage;

    public String endPage;

    public Unit() {

    }

    private Unit(Parcel in) {
        id = in.readString();
        unitName = in.readString();
        unitNumber = in.readString();
        coverImagePath = in.readString();
        startPage = in.readString();
        endPage = in.readString();
    }

    public static final Creator<Unit> CREATOR = new Creator<Unit>() {
        @Override
        public Unit createFromParcel(Parcel source) {
            return new Unit(source);
        }

        @Override
        public Unit[] newArray(int size) {
            return new Unit[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(unitName);
        dest.writeString(unitNumber);
        dest.writeString(coverImagePath);
        dest.writeString(startPage);
        dest.writeString(endPage);
    }
}
