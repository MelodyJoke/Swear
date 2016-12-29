package com.teamsolo.swear.foundation.bean.resp;

import android.os.Parcel;

import com.teamsolo.base.bean.Response;
import com.teamsolo.swear.foundation.bean.Unit;

import java.util.ArrayList;

/**
 * description Units response bean
 * author Melo Chan
 * date 2016/12/27
 * version 0.0.0.1
 */
@SuppressWarnings("WeakerAccess, unused")
public class UnitsResp extends Response {

    public ArrayList<Unit> courseUnitList = new ArrayList<>();

    public String teachingMaterialsName;

    public String teachingMaterialsTypeName;

    public byte isTransferTeachingMaterialsList;

    public UnitsResp() {

    }

    private UnitsResp(Parcel in) {
        in.readTypedList(courseUnitList, Unit.CREATOR);
        teachingMaterialsName = in.readString();
        teachingMaterialsTypeName = in.readString();
        isTransferTeachingMaterialsList = in.readByte();
    }

    public static final Creator<UnitsResp> CREATOR = new Creator<UnitsResp>() {
        @Override
        public UnitsResp createFromParcel(Parcel source) {
            return new UnitsResp(source);
        }

        @Override
        public UnitsResp[] newArray(int size) {
            return new UnitsResp[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(courseUnitList);
        dest.writeString(teachingMaterialsName);
        dest.writeString(teachingMaterialsTypeName);
        dest.writeByte(isTransferTeachingMaterialsList);
    }
}
