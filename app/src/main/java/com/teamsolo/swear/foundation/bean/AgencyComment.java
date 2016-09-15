package com.teamsolo.swear.foundation.bean;

import android.os.Parcel;

import com.teamsolo.base.bean.Bean;

/**
 * description: agency comment bean
 * author: Melody
 * date: 2016/9/15
 * version: 0.0.0.1
 */
@SuppressWarnings("WeakerAccess, unused")
public class AgencyComment extends Bean {

    public String commentContent;

    public long commentId;

    public long createTime;

    public int overallComment;

    public String parentName;

    public String parentPath;

    public String schoolId;

    public AgencyComment() {

    }

    private AgencyComment(Parcel in) {
        commentContent = in.readString();
        commentId = in.readLong();
        createTime = in.readLong();
        overallComment = in.readInt();
        parentName = in.readString();
        parentPath = in.readString();
        schoolId = in.readString();
    }

    public static final Creator<AgencyComment> CREATOR = new Creator<AgencyComment>() {
        @Override
        public AgencyComment createFromParcel(Parcel source) {
            return new AgencyComment(source);
        }

        @Override
        public AgencyComment[] newArray(int size) {
            return new AgencyComment[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(commentContent);
        dest.writeLong(commentId);
        dest.writeLong(createTime);
        dest.writeInt(overallComment);
        dest.writeString(parentName);
        dest.writeString(parentPath);
        dest.writeString(schoolId);
    }
}
