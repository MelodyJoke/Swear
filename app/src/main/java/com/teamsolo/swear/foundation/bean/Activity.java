package com.teamsolo.swear.foundation.bean;

import android.os.Parcel;

import com.teamsolo.base.bean.Bean;
import com.teamsolo.swear.foundation.ui.widget.SlideShowView;

/**
 * description: activity bean
 * author: Melody
 * date: 2016/9/11
 * version: 0.0.0.1
 */
@SuppressWarnings("WeakerAccess, unused")
public class Activity extends Bean implements SlideShowView.SlideShowDummy {

    public String activityUuid;

    public int activityStatus;

    public String activityUrl;

    public String bigPicturePath;

    public byte isNavigation;

    public byte isOutUrl;

    public int limitNumber;

    public String middlePicturePath;

    public int reorder;

    public String smallPicturePath;

    public String title;

    public int type;

    public int visiblePositionType;

    public Activity() {

    }

    private Activity(Parcel in) {
        activityUuid = in.readString();
        activityStatus = in.readInt();
        activityUrl = in.readString();
        bigPicturePath = in.readString();
        isNavigation = in.readByte();
        isOutUrl = in.readByte();
        limitNumber = in.readInt();
        middlePicturePath = in.readString();
        reorder = in.readInt();
        smallPicturePath = in.readString();
        title = in.readString();
        type = in.readInt();
        visiblePositionType = in.readInt();
    }

    public static final Creator<Activity> CREATOR = new Creator<Activity>() {
        @Override
        public Activity createFromParcel(Parcel source) {
            return new Activity(source);
        }

        @Override
        public Activity[] newArray(int size) {
            return new Activity[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(activityUuid);
        dest.writeInt(activityStatus);
        dest.writeString(activityUrl);
        dest.writeString(bigPicturePath);
        dest.writeByte(isNavigation);
        dest.writeByte(isOutUrl);
        dest.writeInt(limitNumber);
        dest.writeString(middlePicturePath);
        dest.writeInt(reorder);
        dest.writeString(smallPicturePath);
        dest.writeString(title);
        dest.writeInt(type);
        dest.writeInt(visiblePositionType);
    }

    @Override
    public String getUrl() {
        return bigPicturePath;
    }
}
