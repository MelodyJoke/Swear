package com.teamsolo.swear.foundation.bean.resp;

import android.os.Parcel;

import com.teamsolo.base.bean.Response;
import com.teamsolo.swear.foundation.bean.Agency;
import com.teamsolo.swear.foundation.bean.AgencyComment;
import com.teamsolo.swear.foundation.bean.Course;

import java.util.ArrayList;

/**
 * description: agency detail response
 * author: Melody
 * date: 2016/9/15
 * version: 0.0.0.1
 */
@SuppressWarnings("WeakerAccess, unused")
public class AgencyDetailResp extends Response {

    public ArrayList<AgencyComment> commentList = new ArrayList<>();

    public ArrayList<Course> courseList = new ArrayList<>();

    public Agency school;

    public String operationKey;

    public AgencyDetailResp() {

    }

    private AgencyDetailResp(Parcel in) {
        in.readTypedList(commentList, AgencyComment.CREATOR);
        in.readTypedList(courseList, Course.CREATOR);
        school = in.readParcelable(Agency.class.getClassLoader());
        operationKey = in.readString();
    }

    public static final Creator<AgencyDetailResp> CREATOR = new Creator<AgencyDetailResp>() {
        @Override
        public AgencyDetailResp createFromParcel(Parcel source) {
            return new AgencyDetailResp(source);
        }

        @Override
        public AgencyDetailResp[] newArray(int size) {
            return new AgencyDetailResp[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(commentList);
        dest.writeTypedList(courseList);
        dest.writeParcelable(school, flags);
        dest.writeString(operationKey);
    }
}
