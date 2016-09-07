package com.teamsolo.swear.structure.ui.news;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.teamsolo.base.template.activity.BaseActivity;
import com.teamsolo.base.util.BuildUtility;
import com.teamsolo.swear.R;
import com.teamsolo.swear.foundation.bean.Comment;
import com.teamsolo.swear.foundation.ui.Appendable;
import com.teamsolo.swear.structure.ui.news.adapter.CommentAdapter;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * description: news comments list page
 * author: Melody
 * date: 2016/9/7
 * version: 0.0.0.1
 */
public class CommentsActivity extends BaseActivity implements
        SwipeRefreshLayout.OnRefreshListener,
        Appendable {

    private SwipeRefreshLayout mSwipeRefreshLayout;

    private View mInputLayout;

    private CommentAdapter mAdapter;

    private List<Comment> mList = new ArrayList<>();

    private String newsUUId;

    private long count;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        getBundle(getIntent());
        initViews();
        bindListeners();
    }

    @Override
    protected void getBundle(@NotNull Intent intent) {
        count = intent.getLongExtra("count", 0);
        newsUUId = intent.getStringExtra("id");
        ArrayList<Comment> comments = intent.getParcelableArrayListExtra("list");
        if (comments != null && !comments.isEmpty()) mList.addAll(comments);
        if (mList.size() >= 6 && count > 6) mList.add(null);
    }

    @Override
    protected void initViews() {
        RecyclerView mListView = (RecyclerView) findViewById(R.id.listView);
        mListView.setHasFixedSize(true);
        mListView.setLayoutManager(new LinearLayoutManager(mContext));
        mListView.setItemAnimator(new DefaultItemAnimator());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setOnClickListener(v -> mListView.scrollToPosition(0));
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

        mAdapter = new CommentAdapter(mContext, mList);
        mListView.setAdapter(mAdapter);

        mInputLayout = findViewById(R.id.input);

        TextView mCountText = (TextView) findViewById(R.id.count);
        mCountText.setText(String.format(getString(R.string.news_comments_count), String.valueOf(count)));
    }

    @Override
    protected void bindListeners() {
        mInputLayout.setOnClickListener(v -> {
            // TODO:
        });

        mSwipeRefreshLayout.setOnRefreshListener(this);
    }

    @Override
    public void onRefresh() {
        toast("refresh");
    }

    @Override
    public void append(Uri uri) {
        toast("append");
    }
}
