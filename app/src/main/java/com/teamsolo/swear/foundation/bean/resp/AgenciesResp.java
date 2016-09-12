package com.teamsolo.swear.foundation.bean.resp;

import android.os.Parcel;

import com.teamsolo.base.bean.Response;
import com.teamsolo.swear.foundation.bean.Agency;

import java.util.ArrayList;

/**
 * description: agency list response
 * author: Melody
 * date: 2016/9/12
 * version: 0.0.0.1
 */
@SuppressWarnings("WeakerAccess, unused")
public class AgenciesResp extends Response {

    public ArrayList<Agency> schoolList = new ArrayList<>();

    public AgenciesResp() {

    }

    private AgenciesResp(Parcel in) {
        in.readTypedList(schoolList, Agency.CREATOR);
    }

    public static final Creator<AgenciesResp> CREATOR = new Creator<AgenciesResp>() {
        @Override
        public AgenciesResp createFromParcel(Parcel source) {
            return new AgenciesResp(source);
        }

        @Override
        public AgenciesResp[] newArray(int size) {
            return new AgenciesResp[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(schoolList);
    }
}
