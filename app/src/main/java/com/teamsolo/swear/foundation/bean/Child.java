package com.teamsolo.swear.foundation.bean;

import android.os.Parcel;

import com.teamsolo.base.bean.Bean;

/**
 * description: child bean
 * author: Melody
 * date: 2016/8/27
 * version: 0.0.0.1
 */
@SuppressWarnings("WeakerAccess, unused")
public class Child extends Bean {

    public long studentId;

    public String studentName;

    public String portraitPath;

    public byte sex;

    public String appellation;

    public String groupId;

    public long classinfoId;

    public String classinfoName;

    public long schoolId;

    public String schoolName;

    public int attentionGrade;

    public long createTime;

    public byte teachphoVisibleToParent;

    public String schoolRollCard;

    public Child() {

    }

    private Child(Parcel in) {
        studentId = in.readLong();
        schoolName = in.readString();
        portraitPath = in.readString();
        sex = in.readByte();
        appellation = in.readString();
        groupId = in.readString();
        classinfoId = in.readLong();
        classinfoName = in.readString();
        schoolId = in.readLong();
        schoolName = in.readString();
        attentionGrade = in.readInt();
        createTime = in.readLong();
        teachphoVisibleToParent = in.readByte();
        schoolRollCard = in.readString();
    }

    public static final Creator<Child> CREATOR = new Creator<Child>() {
        @Override
        public Child createFromParcel(Parcel source) {
            return new Child(source);
        }

        @Override
        public Child[] newArray(int size) {
            return new Child[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(studentId);
        dest.writeString(studentName);
        dest.writeString(portraitPath);
        dest.writeByte(sex);
        dest.writeString(appellation);
        dest.writeString(groupId);
        dest.writeLong(classinfoId);
        dest.writeString(classinfoName);
        dest.writeLong(schoolId);
        dest.writeString(schoolName);
        dest.writeInt(attentionGrade);
        dest.writeLong(createTime);
        dest.writeByte(teachphoVisibleToParent);
        dest.writeString(schoolRollCard);
    }
}
