package com.teamsolo.swear.structure.request;

import com.teamsolo.base.bean.CommonResponse;
import com.teamsolo.swear.foundation.bean.req.ChildChooseReq;
import com.teamsolo.swear.foundation.bean.req.CommentsReq;
import com.teamsolo.swear.foundation.bean.req.CommonRequest;
import com.teamsolo.swear.foundation.bean.req.NewsDetailReq;
import com.teamsolo.swear.foundation.bean.req.NewsReq;
import com.teamsolo.swear.foundation.bean.req.OrdersReq;
import com.teamsolo.swear.foundation.bean.resp.ChildChooseResp;
import com.teamsolo.swear.foundation.bean.resp.CommentsResp;
import com.teamsolo.swear.foundation.bean.resp.NewsDetailResp;
import com.teamsolo.swear.foundation.bean.resp.NewsResp;
import com.teamsolo.swear.foundation.bean.resp.OrdersResp;
import com.teamsolo.swear.foundation.util.RetrofitConfig;
import com.teamsolo.swear.foundation.bean.req.LoadPicReq;
import com.teamsolo.swear.foundation.bean.req.LoginReq;
import com.teamsolo.swear.foundation.bean.resp.LoadPicResp;
import com.teamsolo.swear.foundation.bean.resp.LoginResp;
import com.teamsolo.swear.foundation.constant.NetConst;

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
 * date: 2016/8/22
 * version: 0.0.0.1
 */
@SuppressWarnings("WeakerAccess, unused")
public class BaseHttpUrlRequests {

    private static final int TIMEOUT = 5;

    private Retrofit retrofit;

    private BaseHttpUrlRequests() {
        retrofit = new Retrofit.Builder()
                .baseUrl(RetrofitConfig.releaseHttps
                        ? (NetConst.HTTPS + NetConst.getBaseHttpsUrl())
                        : (NetConst.HTTP + NetConst.getBaseHttpUrl()))
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
        private static final BaseHttpUrlRequests INSTANCE = new BaseHttpUrlRequests();
    }

    @Contract(pure = true)
    public static BaseHttpUrlRequests getInstance() {
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
     * 获取启动图
     */
    public Subscriber<LoadPicResp> getLoadPic(Map<String, String> paras, Subscriber<LoadPicResp> subscriber) {
        retrofit.create(LoadPicReq.class)
                .getLoadPicResp(paras)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
        return subscriber;
    }

    /**
     * 登录
     */
    public Subscriber<LoginResp> getLoginInfo(Map<String, String> paras, Subscriber<LoginResp> subscriber) {
        retrofit.create(LoginReq.class)
                .getLoginInfo(paras)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
        return subscriber;
    }

    /**
     * 选择孩子
     */
    public Subscriber<ChildChooseResp> getChildInfo(Map<String, String> paras, Subscriber<ChildChooseResp> subscriber) {
        retrofit.create(ChildChooseReq.class)
                .getChildInfo(paras)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
        return subscriber;
    }

    /**
     * 获取资讯列表
     */
    public Subscriber<NewsResp> getNews(Map<String, String> paras, Subscriber<NewsResp> subscriber) {
        retrofit.create(NewsReq.class)
                .getNews(paras)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
        return subscriber;
    }

    /**
     * 获取资讯详情
     */
    public Subscriber<NewsDetailResp> getNewsDetail(Map<String, String> paras, Subscriber<NewsDetailResp> subscriber) {
        retrofit.create(NewsDetailReq.class)
                .getNewsDetail(paras)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
        return subscriber;
    }

    /**
     * 获取评论列表
     */
    public Subscriber<CommentsResp> getNewsComments(Map<String, String> paras, Subscriber<CommentsResp> subscriber) {
        retrofit.create(CommentsReq.class)
                .getComments(paras)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
        return subscriber;
    }

    /**
     * 获取订单列表
     */
    public Subscriber<OrdersResp> getOrders(Map<String, String> paras, Subscriber<OrdersResp> subscriber) {
        retrofit.create(OrdersReq.class)
                .getOrders(paras)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
        return subscriber;
    }
}
