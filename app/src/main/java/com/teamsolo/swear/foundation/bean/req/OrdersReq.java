package com.teamsolo.swear.foundation.bean.req;

import com.teamsolo.swear.foundation.bean.resp.OrdersResp;
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
public interface OrdersReq {

    @POST(NetConst.PATH_PRE + NetConst.SERVICE_URL)
    @FormUrlEncoded
    Observable<OrdersResp> getOrders(@FieldMap Map<String, String> paras);
}
