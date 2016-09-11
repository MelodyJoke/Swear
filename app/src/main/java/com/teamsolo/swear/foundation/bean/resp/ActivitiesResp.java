package com.teamsolo.swear.foundation.bean.resp;

import android.os.Parcel;

import com.teamsolo.base.bean.Response;
import com.teamsolo.swear.foundation.bean.Activity;

import java.util.ArrayList;

/**
 * description: activity list response
 * author: Melody
 * date: 2016/9/11
 * version: 0.0.0.1
 */
@SuppressWarnings("WeakerAccess, unused")
public class ActivitiesResp extends Response {

    public ArrayList<Activity> activityEntities = new ArrayList<>();

    public ActivitiesResp() {

    }

    private ActivitiesResp(Parcel in) {
        in.readTypedList(activityEntities, Activity.CREATOR);
    }

    public static final Creator<ActivitiesResp> CREATOR = new Creator<ActivitiesResp>() {
        @Override
        public ActivitiesResp createFromParcel(Parcel source) {
            return new ActivitiesResp(source);
        }

        @Override
        public ActivitiesResp[] newArray(int size) {
            return new ActivitiesResp[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(activityEntities);
    }
}
