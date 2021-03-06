package com.teamsolo.swear.foundation.bean.req;

import com.teamsolo.swear.foundation.bean.resp.UnitsResp;
import com.teamsolo.swear.foundation.constant.NetConst;

import java.util.Map;

import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import rx.Observable;

/**
 * description: teaching material units request
 * author: Melody
 * date: 2016/12/27
 * version: 0.0.0.1
 */
@SuppressWarnings("unused")
public interface UnitsReq {

    @POST(NetConst.PATH_PRE + NetConst.SERVICE_URL)
    @FormUrlEncoded
    Observable<UnitsResp> getUnits(@FieldMap Map<String, String> paras);
}
