package com.teamsolo.swear.foundation.bean.resp;

import android.os.Parcel;

import com.teamsolo.base.bean.Response;
import com.teamsolo.swear.foundation.bean.Teachmats;

import java.util.ArrayList;

/**
 * description: teaching material list response
 * author: Melody
 * date: 2016/12/27
 * version: 0.0.0.1
 */
@SuppressWarnings("WeakerAccess, unused")
public class TeachmatsResp extends Response {

    public ArrayList<Teachmats> gradeList = new ArrayList<>();

    public TeachmatsResp() {

    }

    private TeachmatsResp(Parcel in) {
        in.readTypedList(gradeList, Teachmats.CREATOR);
    }

    public static final Creator<TeachmatsResp> CREATOR = new Creator<TeachmatsResp>() {
        @Override
        public TeachmatsResp createFromParcel(Parcel source) {
            return new TeachmatsResp(source);
        }

        @Override
        public TeachmatsResp[] newArray(int size) {
            return new TeachmatsResp[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(gradeList);
    }
}
