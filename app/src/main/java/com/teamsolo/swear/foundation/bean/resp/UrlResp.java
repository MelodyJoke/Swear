package com.teamsolo.swear.foundation.bean.resp;

import android.os.Parcel;

import com.teamsolo.base.bean.Response;

/**
 * description single url response
 * author Melo Chan
 * date 2016/11/3
 * version 0.0.0.1
 */
@SuppressWarnings("WeakerAccess, unused")
public class UrlResp extends Response {

    public String url;

    public UrlResp() {

    }

    private UrlResp(Parcel in) {
        url = in.readString();
    }

    public static final Creator<UrlResp> CREATOR = new Creator<UrlResp>() {
        @Override
        public UrlResp createFromParcel(Parcel source) {
            return new UrlResp(source);
        }

        @Override
        public UrlResp[] newArray(int size) {
            return new UrlResp[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(url);
    }
}
