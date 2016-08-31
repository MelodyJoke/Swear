package com.teamsolo.swear.foundation.bean;

import android.os.Parcel;

import com.teamsolo.base.bean.Bean;

/**
 * description: order bean
 * author: Melody
 * date: 2016/8/31
 * version: 0.0.0.1
 */
@SuppressWarnings("WeakerAccess, unused")
public class Order extends Bean {

    public int applyRefundTimes;

    public int classHour;

    public long invalidTime;

    public byte isShowCancel;

    public byte isShowPay;

    public byte isShowPrePay;

    public byte isShowRefund;

    public String logoResource;

    public String orderNo;

    public int orderStatus;

    public long orderTime;

    public int orderType;

    public double originalPrice;

    public int payType;

    public String serviceId;

    public String serviceName;

    public double servicePrice;

    public int unitCount;

    public Order() {

    }

    private Order(Parcel in) {
        applyRefundTimes = in.readInt();
        classHour = in.readInt();
        invalidTime = in.readLong();
        isShowCancel = in.readByte();
        isShowPay = in.readByte();
        isShowPrePay = in.readByte();
        isShowRefund = in.readByte();
        logoResource = in.readString();
        orderNo = in.readString();
        orderStatus = in.readInt();
        orderTime = in.readLong();
        orderType = in.readInt();
        originalPrice = in.readDouble();
        payType = in.readInt();
        serviceId = in.readString();
        serviceName = in.readString();
        servicePrice = in.readDouble();
        unitCount = in.readInt();
    }

    public static final Creator<Order> CREATOR = new Creator<Order>() {
        @Override
        public Order createFromParcel(Parcel source) {
            return new Order(source);
        }

        @Override
        public Order[] newArray(int size) {
            return new Order[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(applyRefundTimes);
        dest.writeInt(classHour);
        dest.writeLong(invalidTime);
        dest.writeByte(isShowCancel);
        dest.writeByte(isShowPay);
        dest.writeByte(isShowPrePay);
        dest.writeByte(isShowRefund);
        dest.writeString(logoResource);
        dest.writeString(orderNo);
        dest.writeInt(orderStatus);
        dest.writeLong(orderTime);
        dest.writeInt(orderType);
        dest.writeDouble(originalPrice);
        dest.writeInt(payType);
        dest.writeString(serviceId);
        dest.writeString(serviceName);
        dest.writeDouble(servicePrice);
        dest.writeInt(unitCount);
    }
}
