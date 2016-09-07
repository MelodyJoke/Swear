package com.teamsolo.swear.foundation.bean.req;

import com.teamsolo.base.bean.CommonResponse;
import com.teamsolo.swear.foundation.constant.NetConst;

import java.util.Map;

import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import rx.Observable;

/**
 * description: common request
 * author: Melody
 * date: 2016/9/5
 * version: 0.0.0.1
 */

public interface CommonRequest {

    @POST(NetConst.PATH_PRE + NetConst.SERVICE_URL)
    @FormUrlEncoded
    Observable<CommonResponse> request(@FieldMap Map<String, String> paras);
}
