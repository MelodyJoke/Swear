package com.teamsolo.swear.foundation.bean;

import android.os.Parcel;

import com.teamsolo.base.bean.Bean;

/**
 * description: comment bean
 * author: Melody
 * date: 2016/9/3
 * version: 0.0.0.1
 */
@SuppressWarnings("WeakerAccess, unused")
public class Comment extends Bean {

    public long createTimeStamp;

    public String createTimeStr;

    public long newsCommentId;

    public String newsUuid;

    public String portraitResourcePath;

    public String replyContent;

    public String replyName;

    public long replyUserId;

    public Comment() {

    }

    private Comment(Parcel in) {
        createTimeStamp = in.readLong();
        createTimeStr = in.readString();
        newsCommentId = in.readLong();
        newsUuid = in.readString();
        portraitResourcePath = in.readString();
        replyContent = in.readString();
        replyName = in.readString();
        replyUserId = in.readLong();
    }

    public static final Creator<Comment> CREATOR = new Creator<Comment>() {
        @Override
        public Comment createFromParcel(Parcel source) {
            return new Comment(source);
        }

        @Override
        public Comment[] newArray(int size) {
            return new Comment[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(createTimeStamp);
        dest.writeString(createTimeStr);
        dest.writeLong(newsCommentId);
        dest.writeString(newsUuid);
        dest.writeString(portraitResourcePath);
        dest.writeString(replyContent);
        dest.writeString(replyName);
        dest.writeLong(replyUserId);
    }
}
