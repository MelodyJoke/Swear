package com.teamsolo.swear.structure.ui.news;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.teamsolo.base.bean.CommonResponse;
import com.teamsolo.base.template.activity.HandlerActivity;
import com.teamsolo.base.util.BuildUtility;
import com.teamsolo.swear.R;
import com.teamsolo.swear.foundation.bean.Comment;
import com.teamsolo.swear.foundation.bean.resp.CommentsResp;
import com.teamsolo.swear.foundation.constant.CmdConst;
import com.teamsolo.swear.foundation.constant.SpConst;
import com.teamsolo.swear.foundation.ui.Appendable;
import com.teamsolo.swear.foundation.ui.widget.CommentDialog;
import com.teamsolo.swear.foundation.util.RetrofitConfig;
import com.teamsolo.swear.structure.request.BaseHttpUrlRequests;
import com.teamsolo.swear.structure.ui.news.adapter.CommentAdapter;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Subscriber;

/**
 * description: news comments list page
 * author: Melody
 * date: 2016/9/7
 * version: 0.0.0.1
 */
public class CommentsActivity extends HandlerActivity implements
        SwipeRefreshLayout.OnRefreshListener,
        Appendable {

    private SwipeRefreshLayout mSwipeRefreshLayout;

    private View mInputLayout;

    private TextView mCountText;

    private CommentDialog mCommentDialog;

    private CommentAdapter mAdapter;

    private List<Comment> mList = new ArrayList<>();

    private ArrayList<Comment> comments;

    private String newsUUId;

    private long count;

    private Subscriber<CommentsResp> subscriber;

    private Subscriber<CommonResponse> subscriberComment;

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
        comments = intent.getParcelableArrayListExtra("list");
        if (comments != null) mList.addAll(comments);
        if (mList.size() >= 6) mList.add(null);
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
        mSwipeRefreshLayout.setColorSchemeColors(
                Color.parseColor("#F44336"),
                Color.parseColor("#FF5722"),
                Color.parseColor("#CDDC39"),
                Color.parseColor("#4CAF50"));

        mAdapter = new CommentAdapter(mContext, mList);
        mListView.setAdapter(mAdapter);

        mInputLayout = findViewById(R.id.input);

        mCountText = (TextView) findViewById(R.id.count);
        long realCount = count;
        if (realCount < mList.size()) {
            realCount = mList.size();
            if (mList.get(mList.size() - 1) == null) realCount--;
        }
        mCountText.setText(String.format(getString(R.string.news_comments_count),
                realCount > 99999 ? "99999+" : String.valueOf(realCount)));

        mCommentDialog = CommentDialog.newInstance(500);
    }

    @Override
    protected void bindListeners() {
        mInputLayout.setOnClickListener(v -> mCommentDialog.show(getSupportFragmentManager(), ""));

        mCommentDialog.setOnCancelButtonClickListener((v, editText) -> editText.getText().clear());

        mCommentDialog.setOnConfirmButtonClickListener((v, editText) -> {
            String reply = editText.getText().toString();

            if (TextUtils.isEmpty(reply)) {
                toast(R.string.news_comments_hint);
                return;
            }

            if (TextUtils.isEmpty(newsUUId)) return;

            Map<String, String> paras = new HashMap<>();
            paras.put("CMD", CmdConst.CMD_NEWS_COMMENT);
            paras.put("newsUuid", newsUUId);
            paras.put("replyContent", reply);
            paras.put("serviceType", "2");
            subscriberComment = BaseHttpUrlRequests.getInstance().commonReq(paras, new Subscriber<CommonResponse>() {
                @Override
                public void onCompleted() {

                }

                @Override
                public void onError(Throwable e) {
                    toast(RetrofitConfig.handleReqError(e));
                }

                @Override
                public void onNext(CommonResponse commonResponse) {
                    if (!RetrofitConfig.handleResp(commonResponse, mContext))
                        toast(commonResponse.message);
                    else {
                        toast(commonResponse.message);
                        PreferenceManager.getDefaultSharedPreferences(mContext).edit()
                                .putString(SpConst.NEWS_COMMENT_CACHE, "").apply();
                        if (mList.size() > 0 && mList.get(mList.size() - 1) != null)
                            CommentsActivity.this.request();
                    }
                }
            });

            mCommentDialog.dismiss();
        });

        mSwipeRefreshLayout.setOnRefreshListener(this);
    }

    private void request() {
        if (TextUtils.isEmpty(newsUUId) || mList.isEmpty() || mList.size() == 1 && mList.get(0) == null)
            return;

        if (mList.get(mList.size() - 1) == null) mList.remove(mList.size() - 1);

        Map<String, String> paras = new HashMap<>();
        paras.put("CMD", CmdConst.CMD_GET_COMMENTS);
        paras.put("newsUuid", newsUUId);
        paras.put("serviceType", "2");
        paras.put("newsCommentId", String.valueOf(mList.get(mList.size() - 1).newsCommentId));

        subscriber = BaseHttpUrlRequests.getInstance().getNewsComments(paras, new Subscriber<CommentsResp>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                toast(RetrofitConfig.handleReqError(e));
            }

            @Override
            public void onNext(CommentsResp commentsResp) {
                if (!RetrofitConfig.handleResp(commentsResp, mContext))
                    toast(commentsResp.message);
                else {
                    List<Comment> temp = commentsResp.newsCommentList;
                    if (temp != null) {
                        if (mList.get(mList.size() - 1) == null) mList.remove(mList.size() - 1);
                        mList.addAll(commentsResp.newsCommentList);
                        if (temp.size() >= 10) mList.add(null);
                        mAdapter.notifyDataSetChanged();

                        long realCount = count;
                        if (realCount < mList.size()) {
                            realCount = mList.size();
                            if (mList.get(mList.size() - 1) == null) realCount--;
                        }
                        mCountText.setText(String.format(getString(R.string.news_comments_count),
                                realCount > 99999 ? "99999+" : String.valueOf(realCount)));

                        comments.clear();
                        if (mList.size() < 6) comments.addAll(mList);
                        else if (mList.size() == 6 && mList.get(5) == null)
                            comments.addAll(mList.subList(0, 5));
                        else if (mList.size() > 6 || mList.size() == 6 && mList.get(5) != null)
                            comments.addAll(mList.subList(0, 6));
                    }
                }
            }
        });
    }

    @Override
    public void onRefresh() {
        mList.clear();
        mList.addAll(comments);
        if (mList.size() >= 6) mList.add(null);

        mSwipeRefreshLayout.setRefreshing(false);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void append(Uri uri) {
        handler.postDelayed(this::request, 500);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (subscriber != null && !subscriber.isUnsubscribed()) subscriber.unsubscribe();

        if (subscriberComment != null && !subscriberComment.isUnsubscribed())
            subscriberComment.unsubscribe();
    }

    @Override
    protected void handleMessage(HandlerActivity activity, Message msg) {

    }
}
