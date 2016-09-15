package com.teamsolo.swear.foundation.bean.resp;

import android.os.Parcel;

import com.teamsolo.base.bean.Response;
import com.teamsolo.swear.foundation.bean.NewsComment;

import java.util.ArrayList;

/**
 * description: news detail response
 * author: Melody
 * date: 2016/9/5
 * version: 0.0.0.1
 */
@SuppressWarnings("WeakerAccess, unused")
public class NewsDetailResp extends Response {

    public byte isFavorite;

    public byte isFullComment;

    public byte isLike;

    public ArrayList<NewsComment> newsCommentList = new ArrayList<>();

    public NewsDetailResp() {

    }

    private NewsDetailResp(Parcel in) {
        isFavorite = in.readByte();
        isFullComment = in.readByte();
        isLike = in.readByte();
        in.readTypedList(newsCommentList, NewsComment.CREATOR);
    }

    public static final Creator<NewsDetailResp> CREATOR = new Creator<NewsDetailResp>() {
        @Override
        public NewsDetailResp createFromParcel(Parcel source) {
            return new NewsDetailResp(source);
        }

        @Override
        public NewsDetailResp[] newArray(int size) {
            return new NewsDetailResp[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(isFavorite);
        dest.writeByte(isFullComment);
        dest.writeByte(isLike);
        dest.writeTypedList(newsCommentList);
    }
}
