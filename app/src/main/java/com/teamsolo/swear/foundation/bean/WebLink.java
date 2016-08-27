package com.teamsolo.swear.foundation.bean;

import android.os.Parcel;

import com.teamsolo.base.bean.Bean;

/**
 * description: web link bean
 * author: Melody
 * date: 2016/8/24
 * version: 0.0.0.1
 */
@SuppressWarnings("WeakerAccess, unused")
public class WebLink extends Bean {

    public String forwardUrl;

    public byte isJump;

    public int linkType;

    public String title;

    public byte isNavigation;

    public WebLink() {

    }

    private WebLink(Parcel in) {
        forwardUrl = in.readString();
        isJump = in.readByte();
        linkType = in.readInt();
        title = in.readString();
        isNavigation = in.readByte();
    }

    public static final Creator<WebLink> CREATOR = new Creator<WebLink>() {
        @Override
        public WebLink createFromParcel(Parcel parcel) {
            return new WebLink(parcel);
        }

        @Override
        public WebLink[] newArray(int size) {
            return new WebLink[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(forwardUrl);
        dest.writeByte(isJump);
        dest.writeInt(linkType);
        dest.writeString(title);
        dest.writeByte(isNavigation);
    }
}
