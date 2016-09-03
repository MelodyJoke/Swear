package com.teamsolo.swear.foundation.bean.resp;

import android.os.Parcel;

import com.teamsolo.base.bean.Response;
import com.teamsolo.swear.foundation.bean.NewsDaily;

import java.util.ArrayList;

/**
 * description: news response
 * author: Melody
 * date: 2016/9/3
 * version: 0.0.0.1
 */
@SuppressWarnings("WeakerAccess, unused")
public class NewsResp extends Response {

    public ArrayList<NewsDaily> newsDateList = new ArrayList<>();

    public NewsResp() {

    }

    private NewsResp(Parcel in) {
        in.readTypedList(newsDateList, NewsDaily.CREATOR);
    }

    public static final Creator<NewsResp> CREATOR = new Creator<NewsResp>() {
        @Override
        public NewsResp createFromParcel(Parcel source) {
            return new NewsResp(source);
        }

        @Override
        public NewsResp[] newArray(int size) {
            return new NewsResp[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(newsDateList);
    }
}
