package com.teamsolo.swear.foundation.bean;

import android.os.Parcel;
import android.text.TextUtils;

import com.teamsolo.base.bean.Bean;
import com.teamsolo.swear.foundation.bean.dummy.NewsDummy;
import com.teamsolo.swear.foundation.bean.resp.NewsDetailResp;

import java.util.ArrayList;

/**
 * description: news bean
 * author: Melody
 * date: 2016/9/3
 * version: 0.0.0.1
 */
@SuppressWarnings("WeakerAccess, unused")
public class News extends Bean {

    public String newsUuid;

    public String title;

    public String author;

    public String content;

    public int type;

    public String publishTimeStr;

    public long publishTimeStamp;

    public long commentNumber;

    public long browseNumber;

    public long greatNumber;

    public String searchIndexs;

    public String gradeNames;

    /**
     * 1 无图
     * 2 图文（左图）
     * 3 大图
     * 4 多图(1, 2, 3张)
     */
    public int pictureType;

    public String pictureUrl;

    public byte isFullComment;

    public byte isFavorite;

    public byte isLike;

    public ArrayList<NewsComment> newsCommentList = new ArrayList<>();

    public News() {

    }

    private News(Parcel in) {
        newsUuid = in.readString();
        title = in.readString();
        author = in.readString();
        content = in.readString();
        type = in.readInt();
        publishTimeStr = in.readString();
        publishTimeStamp = in.readLong();
        commentNumber = in.readLong();
        browseNumber = in.readLong();
        greatNumber = in.readLong();
        searchIndexs = in.readString();
        gradeNames = in.readString();
        pictureType = in.readInt();
        pictureUrl = in.readString();
        isFullComment = in.readByte();
        isFavorite = in.readByte();
        isLike = in.readByte();
        in.readTypedList(newsCommentList, NewsComment.CREATOR);
    }

    public static final Creator<News> CREATOR = new Creator<News>() {
        @Override
        public News createFromParcel(Parcel source) {
            return new News(source);
        }

        @Override
        public News[] newArray(int size) {
            return new News[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(newsUuid);
        dest.writeString(title);
        dest.writeString(author);
        dest.writeString(content);
        dest.writeInt(type);
        dest.writeString(publishTimeStr);
        dest.writeLong(publishTimeStamp);
        dest.writeLong(commentNumber);
        dest.writeLong(browseNumber);
        dest.writeLong(greatNumber);
        dest.writeString(searchIndexs);
        dest.writeString(gradeNames);
        dest.writeInt(pictureType);
        dest.writeString(pictureUrl);
        dest.writeByte(isFullComment);
        dest.writeByte(isFavorite);
        dest.writeByte(isLike);
        dest.writeTypedList(newsCommentList);
    }

    // fields about ui
    public boolean inEditMode;

    public boolean isChecked;

    public NewsDummy extractDummy() {
        NewsDummy dummy = new NewsDummy();
        if (pictureType > 0 && pictureType < 5)
            dummy.type = pictureType;
        else dummy.type = 1;
        dummy.title = title;
        dummy.newsUUId = newsUuid;
        if (!TextUtils.isEmpty(searchIndexs))
            dummy.indexes = searchIndexs.split(",");
        dummy.author = author;
        dummy.browserCount = browseNumber;
        dummy.commentCount = commentNumber;
        if (!TextUtils.isEmpty(pictureUrl))
            dummy.pictures = pictureUrl.split(",");

        return dummy;
    }

    public News merge(NewsDetailResp resp) {
        this.isFavorite = resp.isFavorite;
        this.isFullComment = resp.isFullComment;
        this.isLike = resp.isLike;
        this.newsCommentList = resp.newsCommentList;

        return this;
    }
}
