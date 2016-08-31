package com.teamsolo.swear.foundation.bean.resp;

import android.os.Parcel;

import com.teamsolo.base.bean.Response;
import com.teamsolo.swear.foundation.bean.Order;

import java.util.ArrayList;

/**
 * description: orders list response
 * author: Melody
 * date: 2016/8/31
 * version: 0.0.0.1
 */
@SuppressWarnings("WeakerAccess, unused")
public class OrdersResp extends Response {

    public ArrayList<Order> orderList = new ArrayList<>();

    public OrdersResp() {

    }

    private OrdersResp(Parcel in) {
        in.readTypedList(orderList, Order.CREATOR);
    }

    public static final Creator<OrdersResp> CREATOR = new Creator<OrdersResp>() {
        @Override
        public OrdersResp createFromParcel(Parcel source) {
            return new OrdersResp(source);
        }

        @Override
        public OrdersResp[] newArray(int size) {
            return new OrdersResp[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(orderList);
    }
}
