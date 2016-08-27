package com.teamsolo.swear.foundation.bean.req;

import com.teamsolo.swear.foundation.bean.resp.LoginResp;
import com.teamsolo.swear.foundation.constant.NetConst;

import java.util.Map;

import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import rx.Observable;

/**
 * description: 登录
 * author: Melody
 * date: 2016/8/23
 * version: 0.0.0.1
 */
@SuppressWarnings("unused")
public interface LoginReq {

    @POST(NetConst.PATH_PRE + NetConst.LOGIN_URL)
    @FormUrlEncoded
    Observable<LoginResp> getLoginInfo(@FieldMap Map<String, String> paras);
}
