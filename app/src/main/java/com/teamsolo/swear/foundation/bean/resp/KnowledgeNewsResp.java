package com.teamsolo.swear.foundation.bean.resp;

import android.os.Parcel;

import com.teamsolo.base.bean.Response;
import com.teamsolo.swear.foundation.bean.KnowledgeNews;

import java.util.ArrayList;

/**
 * description: knowledge news list response
 * author: Melody
 * date: 2016/9/14
 * version: 0.0.0.1
 */
@SuppressWarnings("WeakerAccess, unused")
public class KnowledgeNewsResp extends Response {

    public ArrayList<KnowledgeNews> newsMsgEntityList = new ArrayList<>();

    public KnowledgeNewsResp() {

    }

    private KnowledgeNewsResp(Parcel in) {
        in.readTypedList(newsMsgEntityList, KnowledgeNews.CREATOR);
    }

    public static final Creator<KnowledgeNewsResp> CREATOR = new Creator<KnowledgeNewsResp>() {
        @Override
        public KnowledgeNewsResp createFromParcel(Parcel source) {
            return new KnowledgeNewsResp(source);
        }

        @Override
        public KnowledgeNewsResp[] newArray(int size) {
            return new KnowledgeNewsResp[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(newsMsgEntityList);
    }
}
