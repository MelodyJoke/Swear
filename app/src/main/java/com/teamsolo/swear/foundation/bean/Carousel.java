package com.teamsolo.swear.foundation.bean;

import android.os.Parcel;

import com.teamsolo.base.bean.Bean;
import com.teamsolo.swear.foundation.ui.widget.SlideShowView;

/**
 * description: knowledge carousel bean
 * author: Melody
 * date: 2016/9/13
 * version: 0.0.0.1
 */
@SuppressWarnings("WeakerAccess, unused")
public class Carousel extends Bean implements SlideShowView.SlideShowDummy {

    public long carouselId;

    public String carouselName;

    public String imgPath;

    public byte isJump;

    public int linkType;

    public int resourceType;

    public String resourceId;

    public String videoType;

    public String link;

    public byte isNavigation;

    public Carousel() {

    }

    private Carousel(Parcel in) {
        carouselId = in.readLong();
        carouselName = in.readString();
        imgPath = in.readString();
        isJump = in.readByte();
        linkType = in.readInt();
        resourceType = in.readInt();
        resourceId = in.readString();
        videoType = in.readString();
        link = in.readString();
        isNavigation = in.readByte();
    }

    public static final Creator<Carousel> CREATOR = new Creator<Carousel>() {
        @Override
        public Carousel createFromParcel(Parcel source) {
            return new Carousel(source);
        }

        @Override
        public Carousel[] newArray(int size) {
            return new Carousel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(carouselId);
        dest.writeString(carouselName);
        dest.writeString(imgPath);
        dest.writeByte(isJump);
        dest.writeInt(linkType);
        dest.writeInt(resourceType);
        dest.writeString(resourceId);
        dest.writeString(videoType);
        dest.writeString(link);
        dest.writeByte(isNavigation);
    }

    @Override
    public String getUrl() {
        return imgPath;
    }
}
