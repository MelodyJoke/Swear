package com.teamsolo.swear.structure.request;

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
     * 获取启动图
     *
     * @param paras      参数
     * @param subscriber 回调
     * @return subscriber，用作取消请求
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
     *
     * @param paras      参数
     * @param subscriber 回调
     * @return subscriber，用作取消请求
     */
    public Subscriber<LoginResp> getLoginInfo(Map<String, String> paras, Subscriber<LoginResp> subscriber) {
        retrofit.create(LoginReq.class)
                .getLoginInfo(paras)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
        return subscriber;
    }
}
