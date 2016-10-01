package com.teamsolo.swear.foundation.bean.req;

import com.teamsolo.swear.foundation.bean.resp.PointsResp;
import com.teamsolo.swear.foundation.constant.NetConst;

import java.util.Map;

import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import rx.Observable;

/**
 * description: points info request
 * author: Melody
 * date: 2016/10/1
 * version: 0.0.0.1
 */
@SuppressWarnings("unused")
public interface PointsReq {

    @POST(NetConst.PATH_PRE + NetConst.SERVICE_URL)
    @FormUrlEncoded
    Observable<PointsResp> getPointsInfo(@FieldMap Map<String, String> paras);
}
