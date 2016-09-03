package com.teamsolo.swear.foundation.bean;

import android.os.Parcel;

import com.teamsolo.base.bean.Bean;
import com.teamsolo.swear.foundation.bean.dummy.NewsDummy;

import java.util.ArrayList;
import java.util.List;

/**
 * description: news list daily
 * author: Melody
 * date: 2016/9/3
 * version: 0.0.0.1
 */
@SuppressWarnings("WeakerAccess, unused")
public class NewsDaily extends Bean {

    public String date;

    public ArrayList<News> newsList = new ArrayList<>();

    public NewsDaily() {

    }

    private NewsDaily(Parcel in) {
        date = in.readString();
        in.readTypedList(newsList, News.CREATOR);
    }

    public static final Creator<NewsDaily> CREATOR = new Creator<NewsDaily>() {
        @Override
        public NewsDaily createFromParcel(Parcel source) {
            return new NewsDaily(source);
        }

        @Override
        public NewsDaily[] newArray(int size) {
            return new NewsDaily[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(date);
        dest.writeTypedList(newsList);
    }

    @SuppressWarnings("Convert2streamapi")
    public List<NewsDummy> extractDummies() {
        List<NewsDummy> dummies = new ArrayList<>();

        NewsDummy dummyTitle = new NewsDummy();
        dummyTitle.type = 0;
        dummyTitle.title = date;
        dummies.add(dummyTitle);

        for (News news :
                newsList)
            dummies.add(news.extractDummy());

        return dummies;
    }
}
