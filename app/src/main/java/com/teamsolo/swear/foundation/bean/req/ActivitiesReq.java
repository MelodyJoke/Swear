package com.teamsolo.swear.foundation.bean.req;

import com.teamsolo.swear.foundation.bean.resp.ActivitiesResp;
import com.teamsolo.swear.foundation.constant.NetConst;

import java.util.Map;

import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import rx.Observable;

/**
 * description: activity list request
 * author: Melody
 * date: 2016/9/11
 * version: 0.0.0.1
 */
@SuppressWarnings("unused")
public interface ActivitiesReq {

    @POST(NetConst.PATH_PRE + NetConst.SERVICE_URL)
    @FormUrlEncoded
    Observable<ActivitiesResp> getActivities(@FieldMap Map<String, String> paras);
}
