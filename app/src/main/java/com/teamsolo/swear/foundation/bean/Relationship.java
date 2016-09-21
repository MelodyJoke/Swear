package com.teamsolo.swear.foundation.bean;

import android.os.Parcel;

import com.teamsolo.base.bean.Bean;

/**
 * description: relationship between parent and child
 * author: Melody
 * date: 2016/9/20
 * version: 0.0.0.1
 */
@SuppressWarnings("WeakerAccess, unused")
public class Relationship extends Bean {

    public String appellation;

    public int contactsType;

    public long createTime;

    public long createUser;

    public byte isMain;

    public byte isPay;

    public String parentName;

    public String parentPhone;

    public long parentsId;

    public long parentsStudentId;

    public long studentId;

    public long updateTime;

    public long updateUser;

    public long expireTime;

    public Relationship() {

    }

    private Relationship(Parcel in) {
        appellation = in.readString();
        contactsType = in.readInt();
        createTime = in.readLong();
        createUser = in.readLong();
        isMain = in.readByte();
        isPay = in.readByte();
        parentName = in.readString();
        parentPhone = in.readString();
        parentsId = in.readLong();
        parentsStudentId = in.readLong();
        studentId = in.readLong();
        updateTime = in.readLong();
        updateUser = in.readLong();
        expireTime = in.readLong();
    }

    public static final Creator<Relationship> CREATOR = new Creator<Relationship>() {
        @Override
        public Relationship createFromParcel(Parcel source) {
            return new Relationship(source);
        }

        @Override
        public Relationship[] newArray(int size) {
            return new Relationship[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(appellation);
        dest.writeInt(contactsType);
        dest.writeLong(createTime);
        dest.writeLong(createUser);
        dest.writeByte(isMain);
        dest.writeByte(isPay);
        dest.writeString(parentName);
        dest.writeString(parentPhone);
        dest.writeLong(parentsId);
        dest.writeLong(parentsStudentId);
        dest.writeLong(studentId);
        dest.writeLong(updateTime);
        dest.writeLong(updateUser);
        dest.writeLong(expireTime);
    }

    // fields about ui
    public int type;

    public boolean isChecked;
}
