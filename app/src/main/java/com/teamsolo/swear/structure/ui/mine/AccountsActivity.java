package com.teamsolo.swear.structure.ui.mine;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.teamsolo.base.template.activity.HandlerActivity;
import com.teamsolo.base.util.BuildUtility;
import com.teamsolo.swear.R;
import com.teamsolo.swear.foundation.bean.Relationship;
import com.teamsolo.swear.structure.ui.mine.adapter.AccountAdapter;
import com.teamsolo.swear.structure.util.LoadingUtil;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * description: accounts manage page
 * author: Melody
 * date: 2016/9/20
 * version: 0.0.0.1
 */
public class AccountsActivity extends HandlerActivity implements SwipeRefreshLayout.OnRefreshListener {

    private SwipeRefreshLayout mSwipeRefreshLayout;

    private RecyclerView mListView;

    private FloatingActionButton mFab;

    private LoadingUtil loadingUtil;

    private AccountAdapter mAdapter;

    private List<Relationship> mList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accounts);

        getBundle(getIntent());
        initViews();
        bindListeners();

        new Thread(this::request).start();
    }

    @Override
    protected void getBundle(@NotNull Intent intent) {

    }

    @Override
    protected void initViews() {
        mListView = (RecyclerView) findViewById(R.id.listView);
        mListView.setHasFixedSize(true);
        mListView.setItemAnimator(new DefaultItemAnimator());
        mListView.setLayoutManager(new LinearLayoutManager(mContext));

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

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe);
        mSwipeRefreshLayout.setColorSchemeColors(
                Color.parseColor("#F44336"),
                Color.parseColor("#FF5722"),
                Color.parseColor("#CDDC39"),
                Color.parseColor("#4CAF50"));
        mSwipeRefreshLayout.setRefreshing(true);

        loadingUtil = new LoadingUtil(findViewById(R.id.loading), mListView)
                .init(R.mipmap.accounts_empty, R.string.loading, R.string.accounts_empty);
        loadingUtil.showLoading();

        mAdapter = new AccountAdapter(mContext, mList);
        mListView.setAdapter(mAdapter);

        mFab = (FloatingActionButton) findViewById(R.id.fab);
    }

    @Override
    protected void bindListeners() {
        mSwipeRefreshLayout.setOnRefreshListener(this);

        mFab.setOnClickListener(v -> {
            // TODO:
        });
    }

    private void request() {
        Relationship relationship1 = new Relationship();
        relationship1.type = 0;
        relationship1.parentName = "Main";
        mList.add(relationship1);

        Relationship relationship2 = new Relationship();
        relationship2.type = 1;
        mList.add(relationship2);

        Relationship relationship4 = new Relationship();
        relationship4.type = 0;
        relationship4.parentName = "Common";
        mList.add(relationship4);

        Relationship relationship3 = new Relationship();
        relationship3.type = 1;
        mList.add(relationship3);

        Relationship relationship5 = new Relationship();
        relationship5.type = 1;
        mList.add(relationship5);

        handler.postDelayed(() -> {
            mAdapter.notifyDataSetChanged();
            loadingUtil.dismiss();
            mSwipeRefreshLayout.setRefreshing(false);
        }, 1500);
    }

    @Override
    public void onRefresh() {
        new Thread(this::request).start();
    }

    @Override
    protected void handleMessage(HandlerActivity activity, Message msg) {

    }
}
