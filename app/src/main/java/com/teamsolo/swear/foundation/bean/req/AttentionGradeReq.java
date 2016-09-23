package com.teamsolo.swear.foundation.bean.req;

import com.teamsolo.swear.foundation.bean.resp.AttentionGradeResp;
import com.teamsolo.swear.foundation.constant.NetConst;

import java.util.Map;

import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import rx.Observable;

/**
 * description: attention grade request
 * author: Melody
 * date: 2016/9/23
 * version: 0.0.0.1
 */
@SuppressWarnings("unused")
public interface AttentionGradeReq {

    @POST(NetConst.PATH_PRE + NetConst.SERVICE_URL)
    @FormUrlEncoded
    Observable<AttentionGradeResp> getAttentionGrade(@FieldMap Map<String, String> paras);
}
