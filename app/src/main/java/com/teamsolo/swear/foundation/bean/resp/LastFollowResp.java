package com.teamsolo.swear.foundation.bean.resp;

import android.os.Parcel;

import com.teamsolo.base.bean.Response;

/**
 * description Last follow unit response bean
 * author Melo Chan
 * date 2016/12/29
 * version 0.0.0.1
 */
@SuppressWarnings("WeakerAccess, unused")
public class LastFollowResp extends Response {

    public String teachingMaterialsId;

    public LastFollowResp() {

    }

    private LastFollowResp(Parcel in) {
        teachingMaterialsId = in.readString();
    }

    public static final Creator<LastFollowResp> CREATOR = new Creator<LastFollowResp>() {
        @Override
        public LastFollowResp createFromParcel(Parcel source) {
            return new LastFollowResp(source);
        }

        @Override
        public LastFollowResp[] newArray(int size) {
            return new LastFollowResp[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(teachingMaterialsId);
    }
}
