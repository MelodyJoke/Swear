package com.teamsolo.swear.foundation.bean.resp;

import android.os.Parcel;

import com.teamsolo.base.bean.Response;
import com.teamsolo.swear.foundation.bean.Comment;

import java.util.ArrayList;

/**
 * description: news comment list response
 * author: Melody
 * date: 2016/9/7
 * version: 0.0.0.1
 */
@SuppressWarnings("WeakerAccess, unused")
public class CommentsResp extends Response {

    public ArrayList<Comment> newsCommentList = new ArrayList<>();

    public CommentsResp() {

    }

    private CommentsResp(Parcel in) {
        in.readTypedList(newsCommentList, Comment.CREATOR);
    }

    public static final Creator<CommentsResp> CREATOR = new Creator<CommentsResp>() {
        @Override
        public CommentsResp createFromParcel(Parcel source) {
            return new CommentsResp(source);
        }

        @Override
        public CommentsResp[] newArray(int size) {
            return new CommentsResp[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(newsCommentList);
    }
}
