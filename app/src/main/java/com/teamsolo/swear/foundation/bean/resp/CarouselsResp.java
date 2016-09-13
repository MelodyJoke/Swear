package com.teamsolo.swear.foundation.bean.resp;

import android.os.Parcel;

import com.teamsolo.base.bean.Response;
import com.teamsolo.swear.foundation.bean.Carousel;

import java.util.ArrayList;

/**
 * description: carousel list response
 * author: Melody
 * date: 2016/9/13
 * version: 0.0.0.1
 */
@SuppressWarnings("WeakerAccess, unused")
public class CarouselsResp extends Response {

    public ArrayList<Carousel> carouselList = new ArrayList<>();

    public CarouselsResp() {

    }

    private CarouselsResp(Parcel in) {
        in.readTypedList(carouselList, Carousel.CREATOR);
    }

    public static final Creator<CarouselsResp> CREATOR = new Creator<CarouselsResp>() {
        @Override
        public CarouselsResp createFromParcel(Parcel source) {
            return new CarouselsResp(source);
        }

        @Override
        public CarouselsResp[] newArray(int size) {
            return new CarouselsResp[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(carouselList);
    }
}
