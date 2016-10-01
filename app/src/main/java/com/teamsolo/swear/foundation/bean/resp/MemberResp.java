package com.teamsolo.swear.foundation.bean.resp;

import android.os.Parcel;

import com.teamsolo.base.bean.Response;

/**
 * description: member info response
 * author: Melody
 * date: 2016/10/1
 * version: 0.0.0.1
 */
@SuppressWarnings("WeakerAccess, unused")
public class MemberResp extends Response {

    public int memberStatus;

    public long expireTime;

    public String memberRightUrl;

    public MemberResp() {

    }

    private MemberResp(Parcel in) {
        memberStatus = in.readInt();
        expireTime = in.readLong();
        memberRightUrl = in.readString();
    }

    public static final Creator<MemberResp> CREATOR = new Creator<MemberResp>() {
        @Override
        public MemberResp createFromParcel(Parcel source) {
            return new MemberResp(source);
        }

        @Override
        public MemberResp[] newArray(int size) {
            return new MemberResp[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(memberStatus);
        dest.writeLong(expireTime);
        dest.writeString(memberRightUrl);
    }
}
