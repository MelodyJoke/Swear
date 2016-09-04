package com.teamsolo.swear.structure.ui.news;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.teamsolo.base.template.activity.HandlerActivity;
import com.teamsolo.base.util.BuildUtility;
import com.teamsolo.base.util.DisplayUtility;
import com.teamsolo.swear.R;
import com.teamsolo.swear.foundation.bean.News;
import com.teamsolo.swear.foundation.constant.DbConst;
import com.teamsolo.swear.structure.util.db.CacheDbHelper;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

/**
 * description: news detail page
 * author: Melody
 * date: 2016/9/4
 * version: 0.0.0.1
 */
public class NewsDetailActivity extends HandlerActivity {

    private NestedScrollView mContainerView;

    private TextView mTitleText, mAuthorText, mDateText;

    private LinearLayout mTagContainer;

    private TextView mContentText;

    private View mReplyLayout;

    private View mInputLayout;

    private TextView mCountText;

    private CheckedTextView mCommentButton, mKeepButton, mPraiseButtom;

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
        mContainerView = (NestedScrollView) findViewById(R.id.container);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setOnClickListener(v -> mContainerView.scrollTo(0, 0));
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

        mTitleText = (TextView) findViewById(R.id.title);
        mAuthorText = (TextView) findViewById(R.id.author);
        mDateText = (TextView) findViewById(R.id.date);
        mTagContainer = (LinearLayout) findViewById(R.id.tags);
        mContentText = (TextView) findViewById(R.id.content);
        mReplyLayout = findViewById(R.id.reply_layout);

        RecyclerView mListView = (RecyclerView) findViewById(R.id.listView);
        mListView.setHasFixedSize(true);
        mListView.setLayoutManager(new LinearLayoutManager(mContext));

        mInputLayout = findViewById(R.id.input);

        mCountText = (TextView) findViewById(R.id.count);
        mCommentButton = (CheckedTextView) findViewById(R.id.comment);
        mKeepButton = (CheckedTextView) findViewById(R.id.keep);
        mPraiseButtom = (CheckedTextView) findViewById(R.id.praise);
    }

    @SuppressWarnings("deprecation")
    private void invalidateUI() {
        if (mItem != null) {
            if (!TextUtils.isEmpty(mItem.title)) mTitleText.setText(mItem.title);

            mAuthorText.setText(String.format(getString(R.string.news_author), mItem.author));

            if (mItem.publishTimeStamp > 0) {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
                String date = format.format(new Date(mItem.publishTimeStamp));
                if (format.format(new Date()).equals(date)) mDateText.setText(R.string.today);
                else mDateText.setText(date);
            } else mDateText.setText(R.string.unknown);

            if (BuildUtility.isRequired(Build.VERSION_CODES.N))
                mContentText.setText(Html.fromHtml(mItem.content, Html.FROM_HTML_MODE_COMPACT));
            else mContentText.setText(Html.fromHtml(mItem.content));

            mCountText.setText(mItem.commentNumber > 9999 ? "9999+" : String.valueOf(mItem.commentNumber));
            mCountText.setVisibility(mItem.commentNumber > 0 ? View.VISIBLE : View.GONE);

            mKeepButton.setChecked(mItem.isFavorite == 1);

            if (!TextUtils.isEmpty(mItem.searchIndexs)) {
                String[] indexes = mItem.searchIndexs.split(",");

                if (indexes.length == 0) mTagContainer.setVisibility(View.GONE);
                else {
                    mTagContainer.removeAllViews();
                    mTagContainer.setVisibility(View.VISIBLE);

                    DisplayMetrics metrics = DisplayUtility.getDisplayMetrics();
                    if (metrics != null) {
                        int totalLength = 0;

                        for (String tag :
                                indexes) {
                            TextView tagView = new TextView(mContext);

                            tagView.setText(tag);
                            tagView.setTextColor(Color.parseColor("#FFFFFF"));
                            tagView.setSingleLine(true);
                            tagView.setEllipsize(TextUtils.TruncateAt.END);
                            tagView.setBackgroundResource(tag.length() < 4
                                    ? R.drawable.shape_bg_tag_primary
                                    : R.drawable.shape_bg_tag_accent);
                            tagView.setGravity(Gravity.CENTER);
                            tagView.setAlpha(0.7f);
                            tagView.setPadding(18, 2, 18, 3);

                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.WRAP_CONTENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT);
                            params.setMargins(0, 0, DisplayUtility.getPxFromDp(5), 0);

                            tagView.measure(DisplayUtility.widthMeasureSpec, DisplayUtility.heightMeasureSpec);
                            totalLength += tagView.getMeasuredWidth();


                            if (totalLength + DisplayUtility.getPxFromDp(20) + 50 >= metrics.widthPixels)
                                break;

                            mTagContainer.addView(tagView, params);
                        }
                    }
                }
            } else mTagContainer.setVisibility(View.GONE);
        }
    }

    private void invalidateReply() {
        if (mItem != null) {
            if (mItem.newsCommentList == null || mItem.newsCommentList.isEmpty())
                mReplyLayout.setVisibility(View.GONE);
            else {
                mReplyLayout.setVisibility(View.VISIBLE);
                // TODO:
            }
        }
    }

    @Override
    protected void bindListeners() {
        mInputLayout.setOnClickListener(v -> {
            // TODO:
        });

        mCommentButton.setOnClickListener(v -> {
            // TODO:
        });

        mKeepButton.setOnClickListener(v -> {
            // TODO:
        });

        mPraiseButtom.setOnClickListener(v -> {
            if (mPraiseButtom.isChecked()) return;
            // TODO:
        });
    }

    private void prepare() {
        attemptCount++;

        Map<String, String> cacheMap = helper.load("news_current");
        if (cacheMap != null && !cacheMap.isEmpty()) {
            if (mNewsUUId.equals(cacheMap.get(DbConst.TABLE_CACHE_FIELDS[3][0]))) {
                String cacheJson = cacheMap.get(DbConst.TABLE_CACHE_FIELDS[2][0]);
                if (!TextUtils.isEmpty(cacheJson)) {
                    mItem = new Gson().fromJson(cacheJson, News.class);
                    handler.post(() -> {
                        invalidateUI();
                        invalidateReply();
                    });
                } else handler.sendEmptyMessageDelayed(0, 500);
            } else handler.sendEmptyMessageDelayed(0, 500);
        } else handler.sendEmptyMessageDelayed(0, 500);
    }
}
