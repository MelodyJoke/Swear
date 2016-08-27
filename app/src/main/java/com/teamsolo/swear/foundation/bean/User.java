package com.teamsolo.swear.foundation.bean;

import android.os.Parcel;

import com.teamsolo.base.bean.Bean;

import java.util.ArrayList;

/**
 * description: user bean
 * author: Melody
 * date: 2016/8/27
 * version: 0.0.0.1
 */
@SuppressWarnings("WeakerAccess, unused")
public class User extends Bean {

    public long userId;

    public String phone;

    public String password;

    public byte parentSex;

    public String parentsName;

    public String parentPath;

    public byte isActive;

    public byte isWXUser;

    public int attentionGrade;

    public int schoolYear;

    public String lastDeviceId;

    public ArrayList<Child> children = new ArrayList<>();

    public User() {

    }

    private User(Parcel in) {
        userId = in.readLong();
        phone = in.readString();
        password = in.readString();
        parentSex = in.readByte();
        parentsName = in.readString();
        parentPath = in.readString();
        isActive = in.readByte();
        isWXUser = in.readByte();
        attentionGrade = in.readInt();
        schoolYear = in.readInt();
        lastDeviceId = in.readString();
        in.readTypedList(children, Child.CREATOR);
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(userId);
        dest.writeString(phone);
        dest.writeString(password);
        dest.writeByte(parentSex);
        dest.writeString(parentsName);
        dest.writeString(parentPath);
        dest.writeByte(isActive);
        dest.writeByte(isWXUser);
        dest.writeInt(attentionGrade);
        dest.writeInt(schoolYear);
        dest.writeString(lastDeviceId);
        dest.writeTypedList(children);
    }

    public User merge(String password) {
        this.password = password;
        return this;
    }
}
