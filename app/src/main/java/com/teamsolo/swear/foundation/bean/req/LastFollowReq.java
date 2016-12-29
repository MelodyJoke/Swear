package com.teamsolo.swear.foundation.bean.req;

import com.teamsolo.swear.foundation.bean.resp.LastFollowResp;
import com.teamsolo.swear.foundation.constant.NetConst;

import java.util.Map;

import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import rx.Observable;

/**
 * description: last follow unit request
 * author: Melody
 * date: 2016/12/29
 * version: 0.0.0.1
 */
@SuppressWarnings("unused")
public interface LastFollowReq {

    @POST(NetConst.PATH_PRE + NetConst.SERVICE_URL)
    @FormUrlEncoded
    Observable<LastFollowResp> getLastFollow(@FieldMap Map<String, String> paras);
}
