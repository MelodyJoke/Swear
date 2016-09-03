package com.teamsolo.swear.foundation.bean.req;

import com.teamsolo.swear.foundation.bean.resp.NewsResp;
import com.teamsolo.swear.foundation.constant.NetConst;

import java.util.Map;

import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import rx.Observable;

/**
 * description: orders list request
 * author: Melody
 * date: 2016/8/31
 * version: 0.0.0.1
 */
@SuppressWarnings("unused")
public interface NewsReq {

    @POST(NetConst.PATH_PRE + NetConst.SERVICE_URL)
    @FormUrlEncoded
    Observable<NewsResp> getNews(@FieldMap Map<String, String> paras);
}
