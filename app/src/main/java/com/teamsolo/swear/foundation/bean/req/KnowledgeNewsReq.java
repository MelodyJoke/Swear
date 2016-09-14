package com.teamsolo.swear.foundation.bean.req;

import com.teamsolo.swear.foundation.bean.resp.KnowledgeNewsResp;
import com.teamsolo.swear.foundation.constant.NetConst;

import java.util.Map;

import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import rx.Observable;

/**
 * description: knowledge news list request
 * author: Melody
 * date: 2016/9/14
 * version: 0.0.0.1
 */
@SuppressWarnings("unused")
public interface KnowledgeNewsReq {

    @POST(NetConst.PATH_PRE + NetConst.SERVICE_URL)
    @FormUrlEncoded
    Observable<KnowledgeNewsResp> getNews(@FieldMap Map<String, String> paras);
}
