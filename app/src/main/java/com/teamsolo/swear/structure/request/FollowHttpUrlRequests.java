package com.teamsolo.swear.structure.request;

import com.teamsolo.base.bean.CommonResponse;
import com.teamsolo.swear.foundation.bean.req.CommonRequest;
import com.teamsolo.swear.foundation.bean.req.LastFollowReq;
import com.teamsolo.swear.foundation.bean.req.TeachmatsReq;
import com.teamsolo.swear.foundation.bean.req.UnitsReq;
import com.teamsolo.swear.foundation.bean.resp.LastFollowResp;
import com.teamsolo.swear.foundation.bean.resp.TeachmatsResp;
import com.teamsolo.swear.foundation.bean.resp.UnitsResp;
import com.teamsolo.swear.foundation.constant.NetConst;
import com.teamsolo.swear.foundation.util.RetrofitConfig;

import org.jetbrains.annotations.Contract;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * description: retrofit http requests with english follow base http url
 * author: Melody
 * date: 2016/12/27
 * version: 0.0.0.1
 */
@SuppressWarnings("WeakerAccess, unused")
public class FollowHttpUrlRequests {

    private static final int TIMEOUT = 5;

    private Retrofit retrofit;

    private FollowHttpUrlRequests() {
        retrofit = new Retrofit.Builder()
                .baseUrl(NetConst.HTTP + NetConst.getBaseHttpUrlForFollow())
                .addConverterFactory(GsonConverterFactory.create(RetrofitConfig.gson))
                .addCallAdapterFactory(RetrofitConfig.rxJavaCallAdapterFactory)
                .client(new OkHttpClient.Builder()
                        .connectTimeout(TIMEOUT, TimeUnit.SECONDS)
                        .addInterceptor(RetrofitConfig.loggingInterceptor)
                        .cookieJar(RetrofitConfig.cookieJar)
                        .build())
                .build();
    }

    private static class Holder {
        private static final FollowHttpUrlRequests INSTANCE = new FollowHttpUrlRequests();
    }

    @Contract(pure = true)
    public static FollowHttpUrlRequests getInstance() {
        return Holder.INSTANCE;
    }

    /**
     * 普通请求
     */
    public Subscriber<CommonResponse> commonReq(Map<String, String> paras, Subscriber<CommonResponse> subscriber) {
        retrofit.create(CommonRequest.class)
                .request(paras)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
        return subscriber;
    }

    /**
     * 获取上次选择的单元
     */
    public Subscriber<LastFollowResp> getLastFollow(Map<String, String> paras, Subscriber<LastFollowResp> subscriber) {
        retrofit.create(LastFollowReq.class)
                .getLastFollow(paras)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
        return subscriber;
    }

    /**
     * 获取年级教材列表
     */
    public Subscriber<TeachmatsResp> getTeachmats(Map<String, String> paras, Subscriber<TeachmatsResp> subscriber) {
        retrofit.create(TeachmatsReq.class)
                .getTeachmats(paras)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
        return subscriber;
    }

    /**
     * 获取单元教材列表
     */
    public Subscriber<UnitsResp> getUnits(Map<String, String> paras, Subscriber<UnitsResp> subscriber) {
        retrofit.create(UnitsReq.class)
                .getUnits(paras)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
        return subscriber;
    }
}
