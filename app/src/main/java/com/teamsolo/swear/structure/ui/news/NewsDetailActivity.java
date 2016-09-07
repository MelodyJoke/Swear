package com.teamsolo.swear.structure.ui.news;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.teamsolo.base.bean.CommonResponse;
import com.teamsolo.base.template.activity.HandlerActivity;
import com.teamsolo.base.util.BuildUtility;
import com.teamsolo.base.util.DisplayUtility;
import com.teamsolo.base.util.SecurityUtility;
import com.teamsolo.swear.R;
import com.teamsolo.swear.foundation.bean.Comment;
import com.teamsolo.swear.foundation.bean.News;
import com.teamsolo.swear.foundation.bean.WebLink;
import com.teamsolo.swear.foundation.bean.resp.NewsDetailResp;
import com.teamsolo.swear.foundation.constant.CmdConst;
import com.teamsolo.swear.foundation.constant.DbConst;
import com.teamsolo.swear.foundation.constant.NetConst;
import com.teamsolo.swear.foundation.ui.widget.ClickableMovementMethod;
import com.teamsolo.swear.foundation.ui.widget.HtmlSupportTextView;
import com.teamsolo.swear.foundation.util.RetrofitConfig;
import com.teamsolo.swear.structure.request.BaseHttpUrlRequests;
import com.teamsolo.swear.structure.ui.WebLinkActivity;
import com.teamsolo.swear.structure.ui.news.adapter.CommentAdapter;
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
 * description: news detail page
 * author: Melody
 * date: 2016/9/4
 * version: 0.0.0.1
 */
public class NewsDetailActivity extends HandlerActivity {

    private NestedScrollView mContainerView;

    private TextView mTitleText, mAuthorText, mDateText;

    private LinearLayout mTagContainer;

    private HtmlSupportTextView mContentText;

    private View mReplyLayout;

    private View mInputLayout;

    private TextView mCountText, mCountText2;

    private CheckedTextView mSlideButton, mCommentButton, mKeepButton, mPraiseButton;

    private CommentAdapter mAdapter;

    private List<Comment> mCommentList = new ArrayList<>();

    private String mNewsUUId;

    private News mItem;

    private CacheDbHelper helper;

    private int attemptCount;

    private Subscriber<NewsDetailResp> subscriberDetail;

    private Subscriber<CommonResponse> subscriberKeep, subscriberPraise;

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

            case 1:
                invalidateUI();
                new Thread(this::requestDetail).start();
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

        mContentText = (HtmlSupportTextView) findViewById(R.id.content);
        mContentText.setLinksClickable(true);
        mContentText.setMovementMethod(ClickableMovementMethod.getInstance());

        mReplyLayout = findViewById(R.id.reply_layout);

        RecyclerView mListView = (RecyclerView) findViewById(R.id.listView);
        mListView.setHasFixedSize(true);
        LinearLayoutManager manager = new LinearLayoutManager(mContext);
        manager.setAutoMeasureEnabled(true);
        mListView.setLayoutManager(manager);
        mListView.setItemAnimator(new DefaultItemAnimator());
        mListView.setNestedScrollingEnabled(false);

        mAdapter = new CommentAdapter(mContext, mCommentList);
        mListView.setAdapter(mAdapter);

        mInputLayout = findViewById(R.id.input);

        mCountText = (TextView) findViewById(R.id.count);
        mCountText2 = (TextView) findViewById(R.id.count2);
        mSlideButton = (CheckedTextView) findViewById(R.id.slide);
        mCommentButton = (CheckedTextView) findViewById(R.id.comment);
        mKeepButton = (CheckedTextView) findViewById(R.id.keep);
        mPraiseButton = (CheckedTextView) findViewById(R.id.praise);
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

            mContentText.setRichText(mItem.content);

            mCountText.setText(mItem.commentNumber > 9999 ? "9999+" : String.valueOf(mItem.commentNumber));
            mCountText.setVisibility(mItem.commentNumber > 0 ? View.VISIBLE : View.GONE);

            mCountText2.setText(mItem.greatNumber > 9999 ? "9999+" : String.valueOf(mItem.greatNumber));
            mCountText2.setVisibility(mItem.greatNumber > 0 ? View.VISIBLE : View.GONE);

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

                mSlideButton.setVisibility(mContentText.getPictureUrls().isEmpty() ? View.GONE : View.VISIBLE);
            } else mTagContainer.setVisibility(View.GONE);
        }
    }

    private void invalidateUIDetail() {
        if (mItem != null) {
            mKeepButton.setChecked(mItem.isFavorite == 1);
            mPraiseButton.setChecked(mItem.isLike == 1);

            if (mItem.isLike == 1) {
                long realGreatNumber = mItem.greatNumber + 1;
                mCountText2.setText(realGreatNumber > 9999 ? "9999+" : String.valueOf(realGreatNumber));
                mCountText2.setVisibility(realGreatNumber > 0 ? View.VISIBLE : View.GONE);
            }

            if (mItem.newsCommentList == null || mItem.newsCommentList.isEmpty())
                mReplyLayout.setVisibility(View.GONE);
            else {
                mCommentList.addAll(mItem.newsCommentList);
                mAdapter.notifyDataSetChanged();

                mReplyLayout.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    protected void bindListeners() {
        mInputLayout.setOnClickListener(v -> {
            // TODO:
        });

        mSlideButton.setOnClickListener(v -> {
            // TODO:
        });

        mCommentButton.setOnClickListener(v -> {
            if (mCommentList.isEmpty()) return;

            Intent intent = new Intent(mContext, CommentsActivity.class);
            intent.putExtra("count", mItem.commentNumber);
            intent.putExtra("id", mItem.newsUuid);
            intent.putParcelableArrayListExtra("list", (ArrayList<Comment>) mCommentList);
            startActivity(intent);
        });

        mKeepButton.setOnClickListener(v -> {
            v.setClickable(false);
            handler.postDelayed(() -> mKeepButton.setClickable(true), 500);
            requestToggleKeep();
        });

        mPraiseButton.setOnClickListener(v -> {
            if (mPraiseButton.isChecked()) return;
            requestPraise();
        });

        mContentText.setOnHrefClickListener((v, link) -> {
            WebLink webLink = new WebLink();
            webLink.forwardUrl = link;
            webLink.title = getString(R.string.news_detail_title);

            Intent intent = new Intent(mContext, WebLinkActivity.class);
            intent.putExtra("link", webLink);
            intent.putExtra("canShare", true);
            startActivity(intent);
        });

        mContentText.setOnAudioClickListener((v, link) -> {
            // TODO: play audio here
            toast(link);
        });

        mContentText.setOnVideoClickListener((v, link) -> {
            // TODO: play video here
            toast(link);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.web_link, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_share) {
            if (TextUtils.isEmpty(mNewsUUId)) return true;

            share(NetConst.HTTP + NetConst.getBaseHttpUrl() + NetConst.PATH_PRE + NetConst.NEWS_SHARE_URL + "newsUuid=" + mNewsUUId,
                    TextUtils.isEmpty(mItem.title) ? getString(R.string.news_detail_title) : mItem.title);
            return true;
        } else if (id == R.id.action_open_outside) {
            if (TextUtils.isEmpty(mNewsUUId)) return true;

            try {
                Intent intent = new Intent("android.intent.action.VIEW");
                intent.setData(Uri.parse(NetConst.HTTP + NetConst.getBaseHttpUrl() + NetConst.PATH_PRE + NetConst.NEWS_SHARE_URL + "newsUuid=" + mNewsUUId));
                startActivity(intent);
            } catch (Exception e) {
                toast(R.string.web_open_deny);
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void prepare() {
        attemptCount++;

        Map<String, String> cacheMap = helper.load("news_current");
        if (cacheMap != null && !cacheMap.isEmpty()) {
            if (mNewsUUId.equals(cacheMap.get(DbConst.TABLE_CACHE_FIELDS[3][0]))) {
                String cacheJson = cacheMap.get(DbConst.TABLE_CACHE_FIELDS[2][0]);
                if (!TextUtils.isEmpty(cacheJson)) {
                    mItem = new Gson().fromJson(cacheJson, News.class);
                    handler.sendEmptyMessage(1);
                } else handler.sendEmptyMessageDelayed(0, 500);
            } else handler.sendEmptyMessageDelayed(0, 500);
        } else handler.sendEmptyMessageDelayed(0, 500);
    }

    private void requestDetail() {
        if (TextUtils.isEmpty(mNewsUUId)) return;

        Map<String, String> paras = new HashMap<>();
        paras.put("CMD", CmdConst.CMD_GET_NEWS_DETAIL);
        paras.put("newsUuid", mNewsUUId);
        paras.put("serviceType", "2");
        paras.put("deviceId", SecurityUtility.getDeviceId(mContext));
        paras.put("browseType", "1");

        subscriberDetail = BaseHttpUrlRequests.getInstance().getNewsDetail(paras, new Subscriber<NewsDetailResp>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                toast(RetrofitConfig.handleReqError(e));
            }

            @Override
            public void onNext(NewsDetailResp resp) {
                if (!RetrofitConfig.handleResp(resp, mContext)) toast(resp.message);
                else {
                    mItem = mItem.merge(resp);
                    invalidateUIDetail();
                }
            }
        });
    }

    private void requestToggleKeep() {
        if (TextUtils.isEmpty(mNewsUUId)) return;

        Map<String, String> paras = new HashMap<>();
        paras.put("CMD", mKeepButton.isChecked() ? CmdConst.CMD_DROP_NEWS : CmdConst.CMD_KEEP_NEWS);
        paras.put("serviceType", "2");
        paras.put("contentId", mNewsUUId);
        paras.put("contentType", "1");
        paras.put("userType", "2");

        mKeepButton.toggle();

        subscriberKeep = BaseHttpUrlRequests.getInstance().commonReq(paras, new Subscriber<CommonResponse>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                toast(RetrofitConfig.handleReqError(e));
                mKeepButton.toggle();
            }

            @Override
            public void onNext(CommonResponse commonResponse) {
                if (!RetrofitConfig.handleResp(commonResponse, mContext)) {
                    toast(commonResponse.message);
                    mKeepButton.toggle();
                }
            }
        });
    }

    private void requestPraise() {
        if (TextUtils.isEmpty(mNewsUUId)) return;

        mPraiseButton.setChecked(true);

        Map<String, String> paras = new HashMap<>();
        paras.put("CMD", CmdConst.CMD_PRAISE_NEWS);
        paras.put("newsUuid", mNewsUUId);
        paras.put("serviceType", "2");

        subscriberPraise = BaseHttpUrlRequests.getInstance().commonReq(paras, new Subscriber<CommonResponse>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                toast(RetrofitConfig.handleReqError(e));
                mPraiseButton.setChecked(false);
            }

            @Override
            public void onNext(CommonResponse commonResponse) {
                if (!RetrofitConfig.handleResp(commonResponse, mContext)) {
                    toast(commonResponse.message);
                    mPraiseButton.setChecked(false);
                } else {
                    long realGreatNumber = mItem.greatNumber + 1;
                    mCountText2.setText(realGreatNumber > 9999 ? "9999+" : String.valueOf(realGreatNumber));
                    mCountText2.setVisibility(realGreatNumber > 0 ? View.VISIBLE : View.GONE);
                }
            }
        });
    }

    private void share(String shareUrl, String shareTitle) {
        try {
            Uri.parse(shareUrl);
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, shareTitle + ": " + shareUrl);

            startActivity(Intent.createChooser(intent, getString(R.string.share_share_to)));
        } catch (Exception e) {
            toast(R.string.web_share_deny);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (subscriberDetail != null && !subscriberDetail.isUnsubscribed())
            subscriberDetail.unsubscribe();

        if (subscriberKeep != null && !subscriberKeep.isUnsubscribed())
            subscriberKeep.unsubscribe();

        if (subscriberPraise != null && !subscriberPraise.isUnsubscribed())
            subscriberPraise.unsubscribe();
    }
}
