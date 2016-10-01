package com.teamsolo.swear.foundation.bean.resp;

import android.os.Parcel;

import com.teamsolo.base.bean.Response;

/**
 * description: points response
 * author: Melody
 * date: 2016/10/1
 * version: 0.0.0.1
 */
@SuppressWarnings("WeakerAccess, unused")
public class PointsResp extends Response {

    public String currentPoints;

    public byte isSignIn;

    public PointsResp() {

    }

    private PointsResp(Parcel in) {
        currentPoints = in.readString();
        isSignIn = in.readByte();
    }

    public static final Creator<PointsResp> CREATOR = new Creator<PointsResp>() {
        @Override
        public PointsResp createFromParcel(Parcel source) {
            return new PointsResp(source);
        }

        @Override
        public PointsResp[] newArray(int size) {
            return new PointsResp[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(currentPoints);
        dest.writeByte(isSignIn);
    }
}
