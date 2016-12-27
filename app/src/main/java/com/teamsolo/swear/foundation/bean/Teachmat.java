package com.teamsolo.swear.foundation.bean;

import android.os.Parcel;

import com.teamsolo.base.bean.Bean;

/**
 * description Teaching material bean
 * author Melo Chan
 * date 2016/12/26
 * version 0.0.0.1
 */
@SuppressWarnings("WeakerAccess, unused")
public class Teachmat extends Bean {

    public String id;

    public String teachingMaterialsName;

    public String teachingMaterialsTypeName;

    public String coverImagePath;

    public Teachmat() {

    }

    private Teachmat(Parcel in) {
        id = in.readString();
        teachingMaterialsName = in.readString();
        teachingMaterialsTypeName = in.readString();
        coverImagePath = in.readString();
    }

    public static final Creator<Teachmat> CREATOR = new Creator<Teachmat>() {
        @Override
        public Teachmat createFromParcel(Parcel source) {
            return new Teachmat(source);
        }

        @Override
        public Teachmat[] newArray(int size) {
            return new Teachmat[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(teachingMaterialsName);
        dest.writeString(teachingMaterialsTypeName);
        dest.writeString(coverImagePath);
    }
}
