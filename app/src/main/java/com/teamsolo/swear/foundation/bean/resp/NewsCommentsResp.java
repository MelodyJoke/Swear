package com.teamsolo.swear.foundation.bean.resp;

import android.os.Parcel;

import com.teamsolo.base.bean.Response;
import com.teamsolo.swear.foundation.bean.NewsComment;

import java.util.ArrayList;

/**
 * description: news comment list response
 * author: Melody
 * date: 2016/9/7
 * version: 0.0.0.1
 */
@SuppressWarnings("WeakerAccess, unused")
public class NewsCommentsResp extends Response {

    public ArrayList<NewsComment> newsCommentList = new ArrayList<>();

    public NewsCommentsResp() {

    }

    private NewsCommentsResp(Parcel in) {
        in.readTypedList(newsCommentList, NewsComment.CREATOR);
    }

    public static final Creator<NewsCommentsResp> CREATOR = new Creator<NewsCommentsResp>() {
        @Override
        public NewsCommentsResp createFromParcel(Parcel source) {
            return new NewsCommentsResp(source);
        }

        @Override
        public NewsCommentsResp[] newArray(int size) {
            return new NewsCommentsResp[size];
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
