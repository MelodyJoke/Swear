package com.teamsolo.swear.structure.ui.news;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.teamsolo.base.template.activity.HandlerActivity;
import com.teamsolo.base.util.BuildUtility;
import com.teamsolo.swear.R;
import com.teamsolo.swear.foundation.bean.News;
import com.teamsolo.swear.foundation.constant.DbConst;
import com.teamsolo.swear.structure.util.db.CacheDbHelper;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * description: news detail page
 * author: Melody
 * date: 2016/9/4
 * version: 0.0.0.1
 */
public class NewsDetailActivity extends HandlerActivity {

    private String mNewsUUId;

    private News mItem;

    private CacheDbHelper helper;

    private int attemptCount;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);

        getBundle(getIntent());
        initViews();
        bindListeners();

        if (!TextUtils.isEmpty(mNewsUUId)) handler.sendEmptyMessage(0);
    }

    @Override
    protected void handleMessage(HandlerActivity activity, Message msg) {
        switch (msg.what) {
            case 0:
                if (attemptCount < 10) new Thread(this::prepare).start();
                break;
        }
    }

    @Override
    protected void getBundle(@NotNull Intent intent) {
        mNewsUUId = intent.getStringExtra("newsId");
        helper = new CacheDbHelper(mContext);
    }

    @Override
    protected void initViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> {
            if (BuildUtility.isRequired(Build.VERSION_CODES.LOLLIPOP)) finishAfterTransition();
            else finish();
        });

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void invalidateUI() {
        if (mItem == null) System.out.println("count: " + attemptCount);
        else System.out.println(mItem.title);
    }

    @Override
    protected void bindListeners() {

    }

    private void prepare() {
        attemptCount++;

        Map<String, String> cacheMap = helper.load("news_current");
        if (cacheMap != null && !cacheMap.isEmpty()) {
            if (mNewsUUId.equals(cacheMap.get(DbConst.TABLE_CACHE_FIELDS[3][0]))) {
                String cacheJson = cacheMap.get(DbConst.TABLE_CACHE_FIELDS[2][0]);
                if (!TextUtils.isEmpty(cacheJson)) {
                    mItem = new Gson().fromJson(cacheJson, News.class);
                    invalidateUI();
                } else handler.sendEmptyMessageDelayed(0, 500);
            } else handler.sendEmptyMessageDelayed(0, 500);
        } else handler.sendEmptyMessageDelayed(0, 500);
    }
}
