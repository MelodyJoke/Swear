package com.teamsolo.swear.foundation.bean.req;

import com.teamsolo.swear.foundation.bean.resp.AccountsResp;
import com.teamsolo.swear.foundation.constant.NetConst;

import java.util.Map;

import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import rx.Observable;

/**
 * description: accounts list request
 * author: Melody
 * date: 2016/9/21
 * version: 0.0.0.1
 */
@SuppressWarnings("unused")
public interface AccountsReq {

    @POST(NetConst.PATH_PRE + NetConst.SERVICE_URL)
    @FormUrlEncoded
    Observable<AccountsResp> getAccounts(@FieldMap Map<String, String> paras);
}
