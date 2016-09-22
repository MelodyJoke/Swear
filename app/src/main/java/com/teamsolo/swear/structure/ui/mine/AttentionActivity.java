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

import org.jetbrains.annotations.NotNull;

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attention);

        getBundle(getIntent());
        initViews();
        bindListeners();
        new Thread(this::requestList).start();
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

        mFab = (FloatingActionButton) findViewById(R.id.fab);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe);
        mSwipeRefreshLayout.setColorSchemeColors(
                Color.parseColor("#F44336"),
                Color.parseColor("#FF5722"),
                Color.parseColor("#CDDC39"),
                Color.parseColor("#4CAF50"));
        mSwipeRefreshLayout.setRefreshing(true);
    }

    @Override
    protected void bindListeners() {
        mFab.setOnClickListener(v -> requestSave());

        mSwipeRefreshLayout.setOnRefreshListener(this);
    }

    @Override
    public void onRefresh() {
        new Thread(this::requestList).start();
    }

    private void requestList() {
        handler.postDelayed(() -> mSwipeRefreshLayout.setRefreshing(false), 1500);
    }

    private void requestSave() {
        mFab.setClickable(false);
        finish();
    }

    @Override
    protected void handleMessage(HandlerActivity activity, Message msg) {

    }
}
