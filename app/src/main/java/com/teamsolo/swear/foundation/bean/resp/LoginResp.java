package com.teamsolo.swear.foundation.bean.resp;

import android.os.Parcel;

import com.teamsolo.base.bean.Response;
import com.teamsolo.swear.foundation.bean.Child;
import com.teamsolo.swear.foundation.bean.User;

import java.util.ArrayList;

/**
 * description: login response
 * author: Melody
 * date: 2016/8/23
 * version: 0.0.0.1
 */
@SuppressWarnings("WeakerAccess, unused")
public class LoginResp extends Response {

    public long userId;

    public String phone;

    public byte parentSex;

    public String parentsName;

    public String parentPath;

    public byte isActive;

    public byte isWXUser;

    public int attentionGrade;

    public int schoolYear;

    public String lastDeviceId;

    public ArrayList<Child> children = new ArrayList<>();

    public LoginResp() {

    }

    private LoginResp(Parcel in) {
        userId = in.readLong();
        phone = in.readString();
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

    public static final Creator<LoginResp> CREATOR = new Creator<LoginResp>() {
        @Override
        public LoginResp createFromParcel(Parcel parcel) {
            return new LoginResp(parcel);
        }

        @Override
        public LoginResp[] newArray(int size) {
            return new LoginResp[size];
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

    public User getUser() {
        User result = new User();
        result.userId = userId;
        result.phone = phone;
        result.parentSex = parentSex;
        result.parentsName = parentsName;
        result.parentPath = parentPath;
        result.isActive = isActive;
        result.isWXUser = isWXUser;
        result.attentionGrade = attentionGrade;
        result.schoolYear = schoolYear;
        result.lastDeviceId = lastDeviceId;
        result.children = children;

        return result;
    }
}
