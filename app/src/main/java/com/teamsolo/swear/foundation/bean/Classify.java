package com.teamsolo.swear.foundation.bean;

import android.os.Parcel;

import com.teamsolo.base.bean.Bean;

import java.util.ArrayList;

/**
 * description: training classify bean
 * author: Melody
 * date: 2016/9/11
 * version: 0.0.0.1
 */
@SuppressWarnings("WeakerAccess, unused")
public class Classify extends Bean {

    public long classificationId;

    public String name;

    public String resourcePath;

    public int classificationType;

    public ArrayList<Classify> childClassifyList = new ArrayList<>();

    public Classify() {

    }

    private Classify(Parcel in) {
        classificationId = in.readLong();
        name = in.readString();
        resourcePath = in.readString();
        classificationType = in.readInt();
        in.readTypedList(childClassifyList, Classify.CREATOR);
    }

    public static final Creator<Classify> CREATOR = new Creator<Classify>() {
        @Override
        public Classify createFromParcel(Parcel source) {
            return new Classify(source);
        }

        @Override
        public Classify[] newArray(int size) {
            return new Classify[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(classificationId);
        dest.writeString(name);
        dest.writeString(resourcePath);
        dest.writeInt(classificationType);
        dest.writeTypedList(childClassifyList);
    }
}
