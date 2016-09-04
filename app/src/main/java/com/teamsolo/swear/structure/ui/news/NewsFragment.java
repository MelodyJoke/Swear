package com.teamsolo.swear.structure.ui.news;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.teamsolo.base.template.fragment.HandlerFragment;
import com.teamsolo.swear.R;
import com.teamsolo.swear.foundation.bean.News;
import com.teamsolo.swear.foundation.bean.NewsDaily;
import com.teamsolo.swear.foundation.bean.dummy.NewsDummy;
import com.teamsolo.swear.foundation.bean.resp.NewsResp;
import com.teamsolo.swear.foundation.constant.CmdConst;
import com.teamsolo.swear.foundation.constant.DbConst;
import com.teamsolo.swear.foundation.constant.SpConst;
import com.teamsolo.swear.foundation.ui.Appendable;
import com.teamsolo.swear.foundation.ui.Refreshable;
import com.teamsolo.swear.foundation.ui.ScrollAble;
import com.teamsolo.swear.foundation.util.RetrofitConfig;
import com.teamsolo.swear.structure.request.BaseHttpUrlRequests;
import com.teamsolo.swear.structure.ui.news.adapter.NewsAdapter;
import com.teamsolo.swear.structure.util.LoadingUtil;
import com.teamsolo.swear.structure.util.UserHelper;
import com.teamsolo.swear.structure.util.db.CacheDbHelper;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import rx.Subscriber;

/**
 * description: news fragment
 * author: Melody
 * date: 2016/9/3
 * version: 0.0.0.1
 */
public class NewsFragment extends HandlerFragment implements Refreshable, Appendable, ScrollAble {

    private RecyclerView mListView;

    private NewsAdapter mAdapter;

    private List<NewsDaily> mList = new ArrayList<>();

    private List<NewsDummy> mDummyList = new ArrayList<>();

    private String date;

    private static final int pageSize = 4;

    private boolean append;

    private Subscriber<NewsResp> subscriber;

    private SimpleDateFormat format;

    private LoadingUtil loadingUtil;

    private CacheDbHelper helper;

    public static NewsFragment newInstance() {
        NewsFragment fragment = new NewsFragment();
        fragment.setArguments(new Bundle());

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setContentView(inflater, R.layout.fragment_recycler_view, container);
        initViews();
        bindListeners();
        onInteraction(Uri.parse("refresh?start=true"));
        date = format.format(new Date());
        new Thread(this::request).start();

        return mLayoutView;
    }

    @Override
    protected void getBundle(@NotNull Bundle bundle) {
        format = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        helper = new CacheDbHelper(mContext);
    }

    @Override
    protected void initViews() {
        mListView = (RecyclerView) findViewById(R.id.listView);

        mListView.setHasFixedSize(true);
        mListView.setItemAnimator(new DefaultItemAnimator());
        mListView.setLayoutManager(new LinearLayoutManager(mContext));

        mAdapter = new NewsAdapter(mContext, mDummyList);
        mListView.setAdapter(mAdapter);

        loadingUtil = new LoadingUtil(findViewById(R.id.loading), mListView)
                .init(R.mipmap.news_empty, R.string.loading, R.string.news_empty);
        loadingUtil.showLoading();
    }

    @Override
    @SuppressWarnings("Convert2streamapi")
    protected void bindListeners() {
        mAdapter.setOnItemClickListener((v, dummy) -> {
            Intent intent = new Intent(mContext, NewsDetailActivity.class);
            intent.putExtra("newsId", dummy.newsUUId);
            startActivity(intent);

            new Thread(() -> {
                for (NewsDaily newsDaily :
                        mList) {
                    if (newsDaily == null || newsDaily.newsList == null || newsDaily.newsList.isEmpty())
                        continue;
                    for (News news :
                            newsDaily.newsList) {
                        if (news == null || TextUtils.isEmpty(news.newsUuid)) continue;
                        if (dummy.newsUUId.equals(news.newsUuid))
                            helper.save("news_current", new Gson().toJson(news), news.newsUuid);
                    }
                }
            }).start();
        });
    }

    @SuppressWarnings("Convert2streamapi")
    private void request() {
        final long studentId = UserHelper.getStudentId(mContext);
        String today = format.format(new Date());
        if (today.equals(date)) {
            String last = PreferenceManager.getDefaultSharedPreferences(mContext).getString(SpConst.LAST_GET_NEWS + studentId, "");
            if (last.equals(today)) {
                mList.clear();

                List<Map<String, String>> cacheMaps = new ArrayList<>();
                cacheMaps.add(helper.load("news_cache_0_" + studentId));
                cacheMaps.add(helper.load("news_cache_1_" + studentId));
                cacheMaps.add(helper.load("news_cache_2_" + studentId));
                cacheMaps.add(helper.load("news_cache_3_" + studentId));

                Gson gson = new GsonBuilder().create();

                for (Map<String, String> cacheMap :
                        cacheMaps) {
                    if (cacheMap != null && !cacheMap.isEmpty()) {
                        String cacheJson = cacheMap.get(DbConst.TABLE_CACHE_FIELDS[2][0]);
                        if (!TextUtils.isEmpty(cacheJson))
                            mList.add(gson.fromJson(cacheJson, NewsDaily.class));
                    }
                }

                if (mList.size() >= 4) mList.add(null);

                invalidateListView();

                handler.post(() -> onInteraction(Uri.parse("refresh?ready=true")));

                handler.postDelayed(() -> {
                    if (mDummyList.size() == 0 || mDummyList.size() == 1 && mDummyList.get(0) == null)
                        loadingUtil.showEmpty();
                    else loadingUtil.dismiss();
                }, 500);
                return;
            }
        }

        Map<String, String> paras = new HashMap<>();
        paras.put("CMD", CmdConst.CMD_GET_NEWS);
        paras.put("date", date);
        paras.put("gradeType", "0");
        int gradeId = UserHelper.getRealAttentionGrade(mContext);
        if (gradeId > 0) paras.put("gradeId", String.valueOf(gradeId));
        paras.put("serviceType", "2");

        subscriber = BaseHttpUrlRequests.getInstance().getNews(paras, new Subscriber<NewsResp>() {
            @Override
            public void onCompleted() {
                onInteraction(Uri.parse("refresh?ready=true"));

                handler.postDelayed(() -> {
                    if (mDummyList.size() == 0 || mDummyList.size() == 1 && mDummyList.get(0) == null)
                        loadingUtil.showEmpty();
                    else loadingUtil.dismiss();
                }, 500);
            }

            @Override
            public void onError(Throwable e) {
                toast(RetrofitConfig.handleReqError(e));
                onInteraction(Uri.parse("refresh?ready=true"));

                handler.postDelayed(() -> {
                    if (mDummyList.size() == 0 || mDummyList.size() == 1 && mDummyList.get(0) == null)
                        loadingUtil.showEmpty();
                    else loadingUtil.dismiss();
                }, 500);
            }

            @Override
            public void onNext(NewsResp newsResp) {
                if (!RetrofitConfig.handleResp(newsResp, mContext)) toast(newsResp.message);
                else {
                    List<NewsDaily> temp = newsResp.newsDateList;

                    if (!append) mList.clear();

                    int size = mList.size();
                    if (size > 0 && mList.get(size - 1) == null) mList.remove(size - 1);

                    mList.addAll(temp);

                    if (temp.size() >= pageSize) mList.add(null);

                    invalidateListView();

                    String today = format.format(new Date());
                    if (today.equals(date)) {
                        String last = PreferenceManager.getDefaultSharedPreferences(mContext).getString(SpConst.LAST_GET_NEWS, "");
                        if (!last.equals(today)) {
                            int count = temp.size();
                            Gson gson = new GsonBuilder().create();

                            for (int i = 0; i < 4; i++) {
                                if (i < count)
                                    helper.save("news_cache_" + i + "_" + studentId, gson.toJson(temp.get(i)), "");
                                else helper.save("news_cache_" + i + "_" + studentId, "", "");
                            }

                            PreferenceManager.getDefaultSharedPreferences(mContext).edit()
                                    .putString(SpConst.LAST_GET_NEWS + studentId, today).apply();
                        }
                    }
                }
            }
        });
    }

    private void invalidateListView() {
        mDummyList.clear();
        for (NewsDaily newsDaily :
                mList) {
            if (newsDaily == null) mDummyList.add(null);
            else mDummyList.addAll(newsDaily.extractDummies());
        }

        handler.post(() -> mAdapter.notifyDataSetChanged());
    }

    @Override
    public void refresh(Uri uri) {
        append = false;
        date = format.format(new Date());
        new Thread(this::request).start();
    }

    @Override
    public void append(Uri uri) {
        append = true;
        int size = mList.size();
        if (size > 0) {
            if (mList.get(size - 1) != null) date = mList.get(mList.size() - 1).date;
            else if (size > 1) date = mList.get(mList.size() - 2).date;
            else date = format.format(new Date());

            if (!format.format(new Date()).equals(date))
                handler.postDelayed(() -> new Thread(this::request).start(), 500);
        }
    }

    @Override
    public void scroll(Uri uri) {
        if (uri.getBooleanQueryParameter("top", false))
            if (mList.size() > 0) mListView.scrollToPosition(0);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (subscriber != null && !subscriber.isUnsubscribed()) subscriber.unsubscribe();
    }

    @Override
    protected void handleMessage(HandlerFragment fragment, Message msg) {

    }
}
