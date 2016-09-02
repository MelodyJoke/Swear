package com.teamsolo.swear.foundation.bean.resp;

import android.os.Parcel;

import com.teamsolo.base.bean.Response;

/**
 * description: child choose response
 * author: Melody
 * date: 2016/9/3
 * version: 0.0.0.1
 */
@SuppressWarnings("WeakerAccess, unused")
public class ChildChooseResp extends Response {

    public long classinfoId;

    public String classinfoName;

    public int attentionGrade;

    public String groupId;

    public long schoolId;

    public String schoolName;

    public String schoolRollCard;

    public ChildChooseResp() {

    }

    private ChildChooseResp(Parcel in) {
        classinfoId = in.readLong();
        classinfoName = in.readString();
        attentionGrade = in.readInt();
        groupId = in.readString();
        schoolId = in.readLong();
        schoolName = in.readString();
        schoolRollCard = in.readString();
    }

    public static final Creator<ChildChooseResp> CREATOR = new Creator<ChildChooseResp>() {
        @Override
        public ChildChooseResp createFromParcel(Parcel source) {
            return new ChildChooseResp(source);
        }

        @Override
        public ChildChooseResp[] newArray(int size) {
            return new ChildChooseResp[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(classinfoId);
        dest.writeString(classinfoName);
        dest.writeInt(attentionGrade);
        dest.writeString(groupId);
        dest.writeLong(schoolId);
        dest.writeString(schoolName);
        dest.writeString(schoolRollCard);
    }
}
