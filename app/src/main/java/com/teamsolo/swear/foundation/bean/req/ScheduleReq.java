package com.teamsolo.swear.foundation.bean.req;

import com.teamsolo.swear.foundation.bean.resp.UrlResp;
import com.teamsolo.swear.foundation.constant.NetConst;

import java.util.Map;

import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import rx.Observable;

/**
 * description schedule req
 * author Melo Chan
 * date 2016/11/3
 * version 0.0.0.1
 */
@SuppressWarnings("unused")
public interface ScheduleReq {

    @POST(NetConst.PATH_PRE + NetConst.SERVICE_URL)
    @FormUrlEncoded
    Observable<UrlResp> getUrl(@FieldMap Map<String, String> paras);
}
