package com.teamsolo.swear.foundation.bean.resp;

import android.os.Parcel;

import com.teamsolo.base.bean.Response;

/**
 * description: load image response
 * author: Melody
 * date: 2016/8/22
 * version: 0.0.0.1
 */
@SuppressWarnings("WeakerAccess, unused")
public class LoadPicResp extends Response {

    /**
     * 大图资源
     */
    public String resourceMaxPath;

    /**
     * 小图资源，无效
     */
    public String resourceMinPath;

    /**
     * 时长，无效
     */
    public int duration;

    /**
     * id， 无效
     */
    public long startupId;

    // 以下字段与启动图跳转相关
    /**
     * 跳转链接
     */
    public String forwardUrl;

    /**
     * 是否跳转
     * 1 yes 0 no
     */
    public byte isJump;

    /**
     * 链接类型
     * 0或1，视为同一种类型，相当于广告
     */
    public int linkType;

    /**
     * 标题
     */
    public String title;

    /**
     * 是否使用本地导航，无效
     * 1 yes 0 no
     * Swear并不使用H5导航
     */
    public byte isNavigation;

    private LoadPicResp(Parcel in) {
        resourceMaxPath = in.readString();
        resourceMinPath = in.readString();
        duration = in.readInt();
        startupId = in.readLong();
        forwardUrl = in.readString();
        isJump = in.readByte();
        linkType = in.readInt();
        title = in.readString();
        isNavigation = in.readByte();
    }

    public static final Creator<LoadPicResp> CREATOR = new Creator<LoadPicResp>() {
        @Override
        public LoadPicResp createFromParcel(Parcel parcel) {
            return new LoadPicResp(parcel);
        }

        @Override
        public LoadPicResp[] newArray(int size) {
            return new LoadPicResp[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(resourceMaxPath);
        dest.writeString(resourceMinPath);
        dest.writeInt(duration);
        dest.writeLong(startupId);
        dest.writeString(forwardUrl);
        dest.writeByte(isJump);
        dest.writeInt(linkType);
        dest.writeString(title);
        dest.writeByte(isNavigation);
    }
}
