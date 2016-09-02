package com.teamsolo.swear.foundation.bean.req;

import com.teamsolo.swear.foundation.bean.resp.ChildChooseResp;
import com.teamsolo.swear.foundation.constant.NetConst;

import java.util.Map;

import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import rx.Observable;

/**
 * description: child choose request
 * author: Melody
 * date: 2016/9/3
 * version: 0.0.0.1
 */
@SuppressWarnings("unused")
public interface ChildChooseReq {

    @POST(NetConst.PATH_PRE + NetConst.CHILD_CHOOSE)
    @FormUrlEncoded
    Observable<ChildChooseResp> getChildInfo(@FieldMap Map<String, String> paras);
}
