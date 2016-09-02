package com.teamsolo.swear.foundation.util;

import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.teamsolo.base.bean.Response;
import com.teamsolo.base.template.activity.BaseActivity;
import com.teamsolo.swear.R;
import com.teamsolo.swear.foundation.constant.DbConst;
import com.teamsolo.swear.structure.Application;
import com.teamsolo.swear.structure.ui.LoginActivity;
import com.teamsolo.swear.structure.util.db.CacheDbHelper;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;
import static com.teamsolo.base.template.application.BaseApplication.getInstanceContext;
import static com.teamsolo.swear.foundation.constant.SpConst.SP_COOKIE;

/**
 * description: retrofit config
 * author: Melody
 * date: 2016/8/21
 * version: 0.0.0.1
 * <p>
 * about cookie: three levels caches, memory cache, shared-preference cache and database cache.
 */
@SuppressWarnings("WeakerAccess, unused")
public class RetrofitConfig {

    public static boolean releaseHttps = false;

    /**
     * cookies cache
     */
    private static List<Cookie> cookies = new ArrayList<>();

    public static Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create();

    public static HttpLoggingInterceptor loggingInterceptor;

    public static RxJavaCallAdapterFactory rxJavaCallAdapterFactory = RxJavaCallAdapterFactory.create();

    public static CookieJar cookieJar = new CookieJar() {
        @Override
        public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
            if (url != null) {
                String urlStr = url.toString();
                if (urlStr.contains("parent/login") || urlStr.contains("parent%2Flogin"))
                    saveCookies(cookies);
            }
        }

        @Override
        public List<Cookie> loadForRequest(HttpUrl url) {
            return loadCookies();
        }
    };

    static {
        loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
    }

    public static void saveCookies(List<Cookie> cookies) {
        if (cookies != null && !cookies.isEmpty()) {
            for (Cookie cookie :
                    cookies)
                Log.i("Cookies", cookie.toString());

            // memory cache
            if (!RetrofitConfig.cookies.isEmpty()) RetrofitConfig.cookies.clear();
            RetrofitConfig.cookies.addAll(cookies);

            // sp cache
            String cache = new Gson().toJson(RetrofitConfig.cookies);
            getDefaultSharedPreferences(getInstanceContext())
                    .edit().putString(SP_COOKIE, cache).apply();

            // db cache
            new CacheDbHelper(getInstanceContext()).save(SP_COOKIE, cache, "");
        }
    }

    public static List<Cookie> loadCookies() {
        // load from memory cache
        if (cookies != null && !cookies.isEmpty()) return cookies;

        // load from sp cache
        String cookieJson = PreferenceManager.getDefaultSharedPreferences(getInstanceContext())
                .getString(SP_COOKIE, "");
        if (!TextUtils.isEmpty(cookieJson)) {
            cookies = new Gson().fromJson(cookieJson,
                    new TypeToken<List<Cookie>>() {
                    }.getType());
            return cookies;
        }

        // load from db cache
        Map<String, String> cacheMap = new CacheDbHelper(getInstanceContext()).load(SP_COOKIE);
        if (cacheMap != null) {
            String cacheJson = cacheMap.get(DbConst.TABLE_CACHE_FIELDS[2][0]);

            if (!TextUtils.isEmpty(cacheJson)) {
                cookies = new Gson().fromJson(cacheJson,
                        new TypeToken<List<Cookie>>() {
                        }.getType());
                return cookies;
            }
        }

        return cookies;
    }

    public static void clearCookies() {
        // memory cache
        if (!RetrofitConfig.cookies.isEmpty()) RetrofitConfig.cookies.clear();

        // sp cache
        String cache = new Gson().toJson(RetrofitConfig.cookies);
        getDefaultSharedPreferences(getInstanceContext())
                .edit().putString(SP_COOKIE, cache).apply();

        // db cache
        new CacheDbHelper(getInstanceContext()).save(SP_COOKIE, cache, "");
    }

    @Nullable
    public static String getSessionId() {
        List<Cookie> cookies = loadCookies();
        if (cookies == null || cookies.isEmpty()) return null;

        for (Cookie cookie :
                cookies)
            if ("JSESSIONID".equals(cookie.name())) return cookie.value();

        return null;
    }

    @NonNull
    public static String handleReqError(Throwable throwable) {
        Context context = Application.getInstanceContext();
        if (context == null) return "";

        // double levels below io exception
        if (throwable instanceof SocketTimeoutException)
            return context.getString(R.string.net_socket_timeout);

        if (throwable instanceof SSLHandshakeException)
            return context.getString(R.string.net_ssl_handshake);

        // single level below io exception
        if (throwable instanceof InterruptedIOException)
            return context.getString(R.string.net_interrupt_io);

        if (throwable instanceof UnknownHostException)
            return context.getString(R.string.net_unknown_host);

        if (throwable instanceof SSLException)
            return context.getString(R.string.net_ssl_exception);

        // io exception level
        if (throwable instanceof IOException)
            return context.getString(R.string.net_io_exception);

        // exception level
        if (throwable instanceof Exception)
            return context.getString(R.string.net_exception);

        // throwable level
        return context.getString(R.string.net_exception_unknown);
    }

    public static boolean handleResp(Response response, Context context) {
        switch (response.code) {
            case 303:
                if (context != null) {
                    Toast.makeText(context, response.message, Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(context, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    context.startActivity(intent);

                    if (context instanceof BaseActivity && !(context instanceof LoginActivity))
                        ((BaseActivity) context).finish();
                }
                return false;
        }

        return response.code == 200;
    }
}
