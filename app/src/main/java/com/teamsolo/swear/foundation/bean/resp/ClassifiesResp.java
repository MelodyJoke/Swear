package com.teamsolo.swear.foundation.bean.resp;

import android.os.Parcel;

import com.teamsolo.base.bean.Response;
import com.teamsolo.swear.foundation.bean.Classify;

import java.util.ArrayList;

/**
 * description: classify list response
 * author: Melody
 * date: 2016/9/11
 * version: 0.0.0.1
 */
@SuppressWarnings("WeakerAccess, unused")
public class ClassifiesResp extends Response {

    public ArrayList<Classify> classifyList = new ArrayList<>();

    public ClassifiesResp() {

    }

    private ClassifiesResp(Parcel in) {
        in.readTypedList(classifyList, Classify.CREATOR);
    }

    public static final Creator<ClassifiesResp> CREATOR = new Creator<ClassifiesResp>() {
        @Override
        public ClassifiesResp createFromParcel(Parcel source) {
            return new ClassifiesResp(source);
        }

        @Override
        public ClassifiesResp[] newArray(int size) {
            return new ClassifiesResp[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(classifyList);
    }
}
