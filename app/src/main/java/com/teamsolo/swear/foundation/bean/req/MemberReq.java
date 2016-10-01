package com.teamsolo.swear.foundation.bean.req;

import com.teamsolo.swear.foundation.bean.resp.MemberResp;
import com.teamsolo.swear.foundation.constant.NetConst;

import java.util.Map;

import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import rx.Observable;

/**
 * description: member info request
 * author: Melody
 * date: 2016/10/1
 * version: 0.0.0.1
 */
@SuppressWarnings("unused")
public interface MemberReq {

    @POST(NetConst.PATH_PRE + NetConst.SERVICE_URL)
    @FormUrlEncoded
    Observable<MemberResp> getMemberInfo(@FieldMap Map<String, String> paras);
}
