package com.teamsolo.swear.foundation.bean.resp;

import android.os.Parcel;

import com.teamsolo.base.bean.Response;
import com.teamsolo.swear.foundation.bean.Relationship;

import java.util.ArrayList;

/**
 * description: accounts response
 * author: Melody
 * date: 2016/9/21
 * version: 0.0.0.1
 */
@SuppressWarnings("WeakerAccess, unused")
public class AccountsResp extends Response {

    public String memberRightTablePictureUrl;

    public ArrayList<Relationship> stuParentRelaList = new ArrayList<>();

    public AccountsResp() {

    }

    private AccountsResp(Parcel in) {
        memberRightTablePictureUrl = in.readString();
        in.readTypedList(stuParentRelaList, Relationship.CREATOR);
    }

    public static final Creator<AccountsResp> CREATOR = new Creator<AccountsResp>() {
        @Override
        public AccountsResp createFromParcel(Parcel source) {
            return new AccountsResp(source);
        }

        @Override
        public AccountsResp[] newArray(int size) {
            return new AccountsResp[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(memberRightTablePictureUrl);
        dest.writeTypedList(stuParentRelaList);
    }
}
