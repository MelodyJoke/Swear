package com.teamsolo.swear.structure.request;

import com.teamsolo.base.bean.CommonResponse;
import com.teamsolo.swear.foundation.bean.req.CarouselsReq;
import com.teamsolo.swear.foundation.bean.req.CommonRequest;
import com.teamsolo.swear.foundation.bean.req.KnowledgeNewsReq;
import com.teamsolo.swear.foundation.bean.resp.CarouselsResp;
import com.teamsolo.swear.foundation.bean.resp.KnowledgeNewsResp;
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
 * description: retrofit http requests with base http url
 * author: Melody
 * date: 2016/9/13
 * version: 0.0.0.1
 */
@SuppressWarnings("WeakerAccess, unused")
public class KnowledgeHttpUrlRequests {

    private static final int TIMEOUT = 5;

    private Retrofit retrofit;

    private KnowledgeHttpUrlRequests() {
        retrofit = new Retrofit.Builder()
                .baseUrl(NetConst.HTTP + NetConst.getBaseHttpUrlForKnowledge())
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
        private static final KnowledgeHttpUrlRequests INSTANCE = new KnowledgeHttpUrlRequests();
    }

    @Contract(pure = true)
    public static KnowledgeHttpUrlRequests getInstance() {
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
     * 获取活动列表
     */
    public Subscriber<CarouselsResp> getCarousels(Map<String, String> paras, Subscriber<CarouselsResp> subscriber) {
        retrofit.create(CarouselsReq.class)
                .getCarousels(paras)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
        return subscriber;
    }

    /**
     * 获取学习力/心理成长列表
     */
    public Subscriber<KnowledgeNewsResp> getNews(Map<String, String> paras, Subscriber<KnowledgeNewsResp> subscriber) {
        retrofit.create(KnowledgeNewsReq.class)
                .getNews(paras)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
        return subscriber;
    }
}
