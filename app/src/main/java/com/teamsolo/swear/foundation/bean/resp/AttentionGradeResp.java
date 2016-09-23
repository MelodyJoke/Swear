package com.teamsolo.swear.foundation.bean.resp;

import android.os.Parcel;

import com.teamsolo.base.bean.Response;
import com.teamsolo.swear.foundation.bean.GradeType;

import java.util.ArrayList;

/**
 * description: attention grade response
 * author: Melody
 * date: 2016/9/23
 * version: 0.0.0.1
 */
@SuppressWarnings("WeakerAccess, unused")
public class AttentionGradeResp extends Response {

    public int attentionGradeId;

    public ArrayList<GradeType> gradeTypes = new ArrayList<>();

    public AttentionGradeResp() {

    }

    private AttentionGradeResp(Parcel in) {
        attentionGradeId = in.readInt();
        in.readTypedList(gradeTypes, GradeType.CREATOR);
    }

    public static final Creator<AttentionGradeResp> CREATOR = new Creator<AttentionGradeResp>() {
        @Override
        public AttentionGradeResp createFromParcel(Parcel source) {
            return new AttentionGradeResp(source);
        }

        @Override
        public AttentionGradeResp[] newArray(int size) {
            return new AttentionGradeResp[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(attentionGradeId);
        dest.writeTypedList(gradeTypes);
    }
}
