package com.teamsolo.swear.foundation.bean;

import android.os.Parcel;

import com.teamsolo.base.bean.Bean;

/**
 * description: news in knowledge
 * author: Melody
 * date: 2016/9/14
 * version: 0.0.0.1
 */
@SuppressWarnings("WeakerAccess, unused")
public class KnowledgeNews extends Bean {

    public String author;

    public long browseNumber;

    public long commentNumber;

    public String keyword;

    public String newsId;

    public String title;

    public String titleColor;

    public KnowledgeNews() {

    }

    private KnowledgeNews(Parcel in) {
        author = in.readString();
        browseNumber = in.readLong();
        commentNumber = in.readLong();
        keyword = in.readString();
        newsId = in.readString();
        title = in.readString();
        titleColor = in.readString();
    }

    public static final Creator<KnowledgeNews> CREATOR = new Creator<KnowledgeNews>() {
        @Override
        public KnowledgeNews createFromParcel(Parcel source) {
            return new KnowledgeNews(source);
        }

        @Override
        public KnowledgeNews[] newArray(int size) {
            return new KnowledgeNews[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(author);
        dest.writeLong(browseNumber);
        dest.writeLong(commentNumber);
        dest.writeString(keyword);
        dest.writeString(newsId);
        dest.writeString(title);
        dest.writeString(titleColor);
    }
}
