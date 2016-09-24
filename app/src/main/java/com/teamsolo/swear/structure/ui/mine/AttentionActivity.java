package com.teamsolo.swear.structure.ui.mine;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.teamsolo.base.bean.CommonResponse;
import com.teamsolo.base.template.activity.HandlerActivity;
import com.teamsolo.base.util.BuildUtility;
import com.teamsolo.swear.R;
import com.teamsolo.swear.foundation.bean.Grade;
import com.teamsolo.swear.foundation.bean.GradeType;
import com.teamsolo.swear.foundation.bean.resp.AttentionGradeResp;
import com.teamsolo.swear.foundation.constant.CmdConst;
import com.teamsolo.swear.foundation.constant.DbConst;
import com.teamsolo.swear.foundation.util.RetrofitConfig;
import com.teamsolo.swear.structure.request.KnowledgeHttpUrlRequests;
import com.teamsolo.swear.structure.ui.mine.adapter.GradeTypeAdapter;
import com.teamsolo.swear.structure.util.UserHelper;
import com.teamsolo.swear.structure.util.db.CacheDbHelper;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Subscriber;

/**
 * description: attention grade page
 * author: Melody
 * date: 2016/9/22
 * version: 0.0.0.1
 */
public class AttentionActivity extends HandlerActivity implements SwipeRefreshLayout.OnRefreshListener {

    private FloatingActionButton mFab;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    private RecyclerView mListView;

    private GradeTypeAdapter mAdapter;

    private List<GradeType> mList = new ArrayList<>();

    private Subscriber<AttentionGradeResp> subscriberAttentionGrade;

    private Subscriber<CommonResponse> subscriberSave;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attention);

        getBundle(getIntent());
        initViews();
        bindListeners();
        new Thread(() -> requestList(true)).start();
    }

    @Override
    protected void getBundle(@NotNull Intent intent) {

    }

    @Override
    protected void initViews() {
        mListView = (RecyclerView) findViewById(R.id.listView);
        mListView.setHasFixedSize(true);
        mListView.setItemAnimator(new DefaultItemAnimator());
        LinearLayoutManager manager = new LinearLayoutManager(mContext);
        manager.setAutoMeasureEnabled(true);
        mListView.setLayoutManager(manager);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setOnClickListener(v -> mListView.smoothScrollToPosition(0));
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> {
            if (BuildUtility.isRequired(Build.VERSION_CODES.LOLLIPOP)) finishAfterTransition();
            else finish();
        });

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        mFab = (FloatingActionButton) findViewById(R.id.fab);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe);
        mSwipeRefreshLayout.setColorSchemeColors(
                Color.parseColor("#F44336"),
                Color.parseColor("#FF5722"),
                Color.parseColor("#CDDC39"),
                Color.parseColor("#4CAF50"));
        mSwipeRefreshLayout.setRefreshing(true);

        mAdapter = new GradeTypeAdapter(mContext, mList);
        mListView.setAdapter(mAdapter);
    }

    @Override
    protected void bindListeners() {
        mFab.setOnClickListener(v -> requestSave());

        mSwipeRefreshLayout.setOnRefreshListener(this);

        mAdapter.setOnItemClickListener((view, grade) -> {
            for (GradeType gradeType :
                    mList)
                for (Grade item :
                        gradeType.grades)
                    item.isChecked = item.gradeId == grade.gradeId;

            mAdapter.notifyDataSetChanged();
        });
    }

    @Override
    public void onRefresh() {
        new Thread(() -> requestList(false)).start();
    }

    private void requestList(boolean fromCache) {
        if (fromCache) {
            Map<String, String> cacheMap = new CacheDbHelper(mContext).load(DbConst.DB_GRADE_TYPES);
            if (cacheMap != null && !cacheMap.isEmpty()) {
                String cacheJson = cacheMap.get(DbConst.TABLE_CACHE_FIELDS[2][0]);
                if (!TextUtils.isEmpty(cacheJson)) {
                    List<GradeType> temp = new Gson().fromJson(cacheJson, new TypeToken<List<GradeType>>() {
                    }.getType());
                    transGradeTypes(temp);
                    mAdapter.notifyDataSetChanged();
                    mSwipeRefreshLayout.setRefreshing(false);
                } else requestList(false);
            } else requestList(false);
        } else {
            Map<String, String> paras = new HashMap<>();
            paras.put("CMD", CmdConst.CMD_ATTENTION);

            subscriberAttentionGrade = KnowledgeHttpUrlRequests.getInstance().getAttentionGrade(paras, new Subscriber<AttentionGradeResp>() {
                @Override
                public void onCompleted() {
                    mSwipeRefreshLayout.setRefreshing(false);
                }

                @Override
                public void onError(Throwable e) {
                    toast(RetrofitConfig.handleReqError(e));
                    mSwipeRefreshLayout.setRefreshing(false);
                }

                @Override
                public void onNext(AttentionGradeResp attentionGradeResp) {
                    if (!RetrofitConfig.handleResp(attentionGradeResp, mContext))
                        toast(attentionGradeResp.message);
                    else {
                        UserHelper.setAttentionGrade(attentionGradeResp.attentionGradeId, mContext);

                        List<GradeType> temp = attentionGradeResp.gradeTypes;
                        if (temp != null && !temp.isEmpty()) {
                            new CacheDbHelper(mContext).save(DbConst.DB_GRADE_TYPES, new Gson().toJson(temp), "");
                            transGradeTypes(temp);
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                }
            });
        }
    }

    private void transGradeTypes(List<GradeType> temp) {
        mList.clear();
        mList.addAll(temp);

        int gradeId = UserHelper.getRealAttentionGrade(mContext);
        for (GradeType gradeType :
                mList)
            for (Grade item :
                    gradeType.grades)
                item.isChecked = item.gradeId == gradeId;
    }

    private void requestSave() {
        mFab.setClickable(false);

        int gradeId = -1;
        out:
        for (GradeType gradeType :
                mList)
            for (Grade grade :
                    gradeType.grades)
                if (grade.isChecked) {
                    gradeId = grade.gradeId;
                    break out;
                }

        final int copy = gradeId;

        if (copy <= 0) {
            mFab.setClickable(true);
            toast(R.string.attention_empty_error);
            return;
        }

        int lastOne = UserHelper.getRealAttentionGrade(mContext);
        if (copy == lastOne) {
            mFab.setClickable(true);
            toast(R.string.attention_diff_error);
            return;
        }

        Map<String, String> paras = new HashMap<>();
        paras.put("CMD", CmdConst.CMD_ATTENTION_GRADE);
        paras.put("gradeId", String.valueOf(copy));

        subscriberSave = KnowledgeHttpUrlRequests.getInstance().commonReq(paras, new Subscriber<CommonResponse>() {
            @Override
            public void onCompleted() {
                mFab.setClickable(true);
            }

            @Override
            public void onError(Throwable e) {
                toast(RetrofitConfig.handleReqError(e));
                mFab.setClickable(true);
            }

            @Override
            public void onNext(CommonResponse commonResponse) {
                if (!RetrofitConfig.handleResp(commonResponse, mContext))
                    toast(commonResponse.message);
                else {
                    UserHelper.setAttentionGrade(copy, mContext);
                    finish();
                }
            }
        });
    }

    @Override
    protected void handleMessage(HandlerActivity activity, Message msg) {

    }

    @Override
    public void toast(int msgRes) {
        Snackbar.make(mFab, msgRes, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void toast(String message) {
        Snackbar.make(mFab, message, Snackbar.LENGTH_LONG).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (subscriberAttentionGrade != null && !subscriberAttentionGrade.isUnsubscribed())
            subscriberAttentionGrade.unsubscribe();

        if (subscriberSave != null && !subscriberSave.isUnsubscribed())
            subscriberSave.unsubscribe();
    }
}
