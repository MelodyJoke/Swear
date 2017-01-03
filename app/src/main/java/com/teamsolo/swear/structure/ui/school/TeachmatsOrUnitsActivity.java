package com.teamsolo.swear.structure.ui.school;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.preference.PreferenceManager;
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
import android.view.View;

import com.teamsolo.base.bean.CommonResponse;
import com.teamsolo.base.template.activity.HandlerActivity;
import com.teamsolo.base.util.BuildUtility;
import com.teamsolo.swear.R;
import com.teamsolo.swear.foundation.bean.Teachmats;
import com.teamsolo.swear.foundation.bean.Unit;
import com.teamsolo.swear.foundation.bean.resp.LastFollowResp;
import com.teamsolo.swear.foundation.bean.resp.TeachmatsResp;
import com.teamsolo.swear.foundation.bean.resp.UnitsResp;
import com.teamsolo.swear.foundation.constant.CmdConst;
import com.teamsolo.swear.foundation.constant.SpConst;
import com.teamsolo.swear.foundation.util.RetrofitConfig;
import com.teamsolo.swear.structure.request.FollowHttpUrlRequests;
import com.teamsolo.swear.structure.ui.school.adapter.TeachmatsAdapter;
import com.teamsolo.swear.structure.ui.school.adapter.UnitAdapter;
import com.teamsolo.swear.structure.util.LoadingUtil;
import com.teamsolo.swear.structure.util.UserHelper;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Subscriber;

/**
 * description Teaching materials or units page
 * author Melo Chan
 * date 2016/12/26
 * version 0.0.0.1
 */
@SuppressWarnings("WeakerAccess, unused")
public class TeachmatsOrUnitsActivity extends HandlerActivity {

    private RecyclerView mListView;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    private FloatingActionButton mFab;

    private ActionBar mToolbar;

    private TeachmatsAdapter mTeachmatsAdapter;

    private UnitAdapter mUnitAdapter;

    private List<Teachmats> mTeachmats = new ArrayList<>();

    private List<Unit> mUnits = new ArrayList<>();

    private String teachmatId;

    private boolean toTeachmats, fromTeachmats;

    private LoadingUtil loadingUtil;

    private Subscriber<LastFollowResp> subscriberFollow;

    private Subscriber<TeachmatsResp> subscriberTeachmats;

    private Subscriber<UnitsResp> subscriberUnits;

    private Subscriber<CommonResponse> subscriberRemember;

    private boolean hasShownRemember;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_view);

        getBundle(getIntent());
        initViews();
        bindListeners();

        mSwipeRefreshLayout.setRefreshing(true);
        new Thread(this::request).start();
    }

    @Override
    protected void getBundle(@NotNull Intent intent) {
        teachmatId = intent.getStringExtra("id");
        toTeachmats = intent.getBooleanExtra("to", false);
        fromTeachmats = intent.getBooleanExtra("from", false);
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

        mToolbar = getSupportActionBar();
        if (mToolbar != null) {
            mToolbar.setDisplayHomeAsUpEnabled(true);
            mToolbar.setDisplayShowHomeEnabled(true);
        }

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe);
        mSwipeRefreshLayout.setColorSchemeColors(
                Color.parseColor("#F44336"),
                Color.parseColor("#FF5722"),
                Color.parseColor("#CDDC39"),
                Color.parseColor("#4CAF50"));

        loadingUtil = new LoadingUtil(findViewById(R.id.loading), mListView)
                .init(R.mipmap.follow_empty, R.string.loading, R.string.school_follow_empty);
        loadingUtil.showLoading();

        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mFab.setImageResource(R.drawable.ic_undo_white_24dp);
        mFab.setVisibility(View.GONE);

        mTeachmatsAdapter = new TeachmatsAdapter(mContext, mTeachmats, (view, teachmat) -> {
            Intent intent = new Intent(mContext, TeachmatsOrUnitsActivity.class);
            intent.putExtra("id", teachmat.id);
            intent.putExtra("from", true);
            startActivity(intent);
        });
        mUnitAdapter = new UnitAdapter(mContext, mUnits);
    }

    @Override
    protected void bindListeners() {
        mSwipeRefreshLayout.setOnRefreshListener(() -> handler.post(this::request));

        mFab.setOnClickListener(v -> {
            if (fromTeachmats) finish();
            else {
                Intent intent = new Intent(mContext, TeachmatsOrUnitsActivity.class);
                intent.putExtra("to", true);
                startActivity(intent);
                finish();
            }
        });
    }

    private void request() {
        if (toTeachmats) {
            requestTeachmats();
            return;
        }

        if (!TextUtils.isEmpty(teachmatId)) {
            requestUnits();
            return;
        }

        requestFollow();
    }

    private void requestFollow() {
        if (UserHelper.getUserId(mContext) > 0) {
            Map<String, String> paras = new HashMap<>();
            paras.put("CMD", CmdConst.CMD_LAST_FOLLOW);
            paras.put("serviceType", "9");
            subscriberFollow = FollowHttpUrlRequests.getInstance().getLastFollow(paras, new Subscriber<LastFollowResp>() {
                @Override
                public void onCompleted() {

                }

                @Override
                public void onError(Throwable e) {
                    toast(RetrofitConfig.handleReqError(e));

                    mSwipeRefreshLayout.setRefreshing(false);

                    if (mTeachmats.isEmpty()) loadingUtil.showEmpty();
                    else loadingUtil.dismiss();
                }

                @Override
                public void onNext(LastFollowResp lastFollowResp) {
                    if (!RetrofitConfig.handleResp(lastFollowResp, mContext))
                        toast(lastFollowResp.message);
                    else {
                        teachmatId = lastFollowResp.teachingMaterialsId;
                        toTeachmats = TextUtils.isEmpty(teachmatId);
                        TeachmatsOrUnitsActivity.this.request();
                    }
                }
            });
        } else {
            teachmatId = PreferenceManager.getDefaultSharedPreferences(mContext).getString(SpConst.LAST_FOLLOW_VISITOR, "");
            toTeachmats = TextUtils.isEmpty(teachmatId);
            TeachmatsOrUnitsActivity.this.request();
        }
    }

    private void requestTeachmats() {
        Map<String, String> paras = new HashMap<>();
        paras.put("CMD", CmdConst.CMD_TEACH_MATS);
        paras.put("serviceType", "9");
        subscriberTeachmats = FollowHttpUrlRequests.getInstance().getTeachmats(paras, new Subscriber<TeachmatsResp>() {
            @Override
            public void onCompleted() {
                mSwipeRefreshLayout.setRefreshing(false);

                if (mTeachmats.isEmpty()) loadingUtil.showEmpty();
                else loadingUtil.dismiss();
            }

            @Override
            public void onError(Throwable e) {
                toast(RetrofitConfig.handleReqError(e));

                mSwipeRefreshLayout.setRefreshing(false);

                if (mTeachmats.isEmpty()) loadingUtil.showEmpty();
                else loadingUtil.dismiss();
            }

            @Override
            public void onNext(TeachmatsResp teachmatsResp) {
                if (!RetrofitConfig.handleResp(teachmatsResp, mContext))
                    toast(teachmatsResp.message);
                else {
                    mToolbar.setTitle(R.string.school_follow_title);

                    if (!mTeachmats.isEmpty()) mTeachmats.clear();
                    if (teachmatsResp.gradeList != null)
                        mTeachmats.addAll(teachmatsResp.gradeList);

                    if (!(mListView.getAdapter() instanceof TeachmatsAdapter))
                        mListView.setAdapter(mTeachmatsAdapter);
                    mTeachmatsAdapter.notifyDataSetChanged();
                }

                mFab.setVisibility(View.GONE);
            }
        });
    }

    private void requestUnits() {
        Map<String, String> paras = new HashMap<>();
        paras.put("CMD", CmdConst.CMD_UNITS);
        paras.put("serviceType", "9");
        paras.put("teachingMaterialId", teachmatId);
        subscriberUnits = FollowHttpUrlRequests.getInstance().getUnits(paras, new Subscriber<UnitsResp>() {
            @Override
            public void onCompleted() {
                mSwipeRefreshLayout.setRefreshing(false);

                if (mUnits.isEmpty()) loadingUtil.showEmpty();
                else {
                    loadingUtil.dismiss();
                    if (fromTeachmats && !hasShownRemember) {
                        Snackbar.make(mFab, R.string.school_follow_remember, Snackbar.LENGTH_INDEFINITE)
                                .setAction(R.string.school_follow_remember_action, v -> requestRemember()).show();
                        hasShownRemember = true;
                    }
                }
            }

            @Override
            public void onError(Throwable e) {
                toast(RetrofitConfig.handleReqError(e));

                mSwipeRefreshLayout.setRefreshing(false);

                if (mUnits.isEmpty()) loadingUtil.showEmpty();
                else loadingUtil.dismiss();
            }

            @Override
            public void onNext(UnitsResp unitsResp) {
                if (!RetrofitConfig.handleResp(unitsResp, mContext))
                    toast(unitsResp.message);
                else {
                    mToolbar.setTitle(String.format("%s (%s)", unitsResp.teachingMaterialsName, unitsResp.teachingMaterialsTypeName));

                    if (unitsResp.isTransferTeachingMaterialsList == 1) {
                        if (fromTeachmats) finish();
                        else {
                            Intent intent = new Intent(mContext, TeachmatsOrUnitsActivity.class);
                            intent.putExtra("to", true);
                            startActivity(intent);
                            finish();
                            return;
                        }
                    }

                    if (!mUnits.isEmpty()) mUnits.clear();
                    if (unitsResp.courseUnitList != null)
                        mUnits.addAll(unitsResp.courseUnitList);

                    if (!(mListView.getAdapter() instanceof UnitAdapter))
                        mListView.setAdapter(mUnitAdapter);
                    mUnitAdapter.notifyDataSetChanged();
                }

                mFab.setVisibility(View.VISIBLE);
            }
        });
    }

    private void requestRemember() {
        if (TextUtils.isEmpty(teachmatId)) return;

        if (UserHelper.getUserId(mContext) > 0) {
            Map<String, String> paras = new HashMap<>();
            paras.put("CMD", CmdConst.CMD_REMEMBER_LAST);
            paras.put("serviceType", "9");
            paras.put("teachingMaterialId", teachmatId);
            subscriberRemember = FollowHttpUrlRequests.getInstance().commonReq(paras, new Subscriber<CommonResponse>() {
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
                }
            });
        } else {
            PreferenceManager.getDefaultSharedPreferences(mContext).edit()
                    .putString(SpConst.LAST_FOLLOW_VISITOR, teachmatId).apply();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (subscriberFollow != null && !subscriberFollow.isUnsubscribed())
            subscriberFollow.unsubscribe();

        if (subscriberTeachmats != null && !subscriberTeachmats.isUnsubscribed())
            subscriberTeachmats.unsubscribe();

        if (subscriberUnits != null && !subscriberUnits.isUnsubscribed())
            subscriberUnits.unsubscribe();

        if (subscriberRemember != null && !subscriberRemember.isUnsubscribed())
            subscriberRemember.unsubscribe();
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
}
