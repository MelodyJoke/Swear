package com.teamsolo.swear.structure.ui.school;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.teamsolo.base.template.fragment.HandlerFragment;
import com.teamsolo.swear.R;
import com.teamsolo.swear.foundation.bean.Activity;
import com.teamsolo.swear.foundation.bean.Category;
import com.teamsolo.swear.foundation.bean.WebLink;
import com.teamsolo.swear.foundation.bean.resp.ActivitiesResp;
import com.teamsolo.swear.foundation.constant.BroadcastConst;
import com.teamsolo.swear.foundation.constant.CmdConst;
import com.teamsolo.swear.foundation.ui.Refreshable;
import com.teamsolo.swear.foundation.ui.ScrollAble;
import com.teamsolo.swear.foundation.ui.widget.SlideShowPlayHandler;
import com.teamsolo.swear.foundation.ui.widget.SlideShowView;
import com.teamsolo.swear.foundation.util.RetrofitConfig;
import com.teamsolo.swear.structure.request.BaseHttpUrlRequests;
import com.teamsolo.swear.structure.ui.about.AboutActivity;
import com.teamsolo.swear.structure.ui.common.WebLinkActivity;
import com.teamsolo.swear.structure.ui.mine.AccountsActivity;
import com.teamsolo.swear.structure.ui.school.adapter.CategoryAdapter;
import com.teamsolo.swear.structure.util.UserHelper;

import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Subscriber;

/**
 * description: school page
 * author: Melody
 * date: 2016/9/24
 * version: 0.0.0.1
 */
public class SchoolFragment extends HandlerFragment implements Refreshable, ScrollAble, SlideShowView.SlideShowParent {

    private NestedScrollView mContentView;

    private SlideShowView mSlideShow;

    private CategoryAdapter mCategoryAdapter;

    private SlideShowPlayHandler mSlideShowHandler;

    private List<Category> categories = new ArrayList<>();

    private boolean isRequesting;

    private List<SlideShowView.SlideShowDummy> dummies = new ArrayList<>();

    private Subscriber<ActivitiesResp> subscriberActivity;

    private BroadcastReceiver attentionChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            onInteraction(Uri.parse("refresh?start=true"));
            // TODO:
        }
    };

    public static SchoolFragment newInstance() {
        SchoolFragment fragment = new SchoolFragment();
        fragment.setArguments(new Bundle());

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setContentView(inflater, R.layout.fragment_school, container);
        initViews();
        bindListeners();
        onInteraction(Uri.parse("refresh?start=true"));
        handler.sendEmptyMessage(0);
        mContext.registerReceiver(attentionChangeReceiver, new IntentFilter(BroadcastConst.BC_ATTENTION_GRADE_CHANGE));

        return mLayoutView;
    }

    @Override
    protected void getBundle(@NotNull Bundle bundle) {

    }

    @Override
    protected void initViews() {
        mContentView = (NestedScrollView) findViewById(R.id.content);

        mSlideShow = (SlideShowView) findViewById(R.id.slide);
        dummies.add(new Activity());
        mSlideShow.setDummies(dummies);
        mSlideShowHandler = new SlideShowPlayHandler(new WeakReference<>(this), 4000);
        mSlideShow.setSlideShowHandler(mSlideShowHandler);
        mSlideShow.startFlipping();

        RecyclerView mGridView = (RecyclerView) findViewById(R.id.gridView);
        mGridView.setHasFixedSize(true);
        GridLayoutManager manager = new GridLayoutManager(mContext, 4);
        manager.setAutoMeasureEnabled(true);
        mGridView.setLayoutManager(manager);
        mGridView.setItemAnimator(new DefaultItemAnimator());
        generateCategories();
        mCategoryAdapter = new CategoryAdapter(mContext, categories);
        mGridView.setAdapter(mCategoryAdapter);
        mGridView.setNestedScrollingEnabled(false);
        mGridView.setFocusable(false);

        mSlideShow.post(() -> {
            int width = mSlideShow.getMeasuredWidth();
            int aimHeight = (int) (width * 399.0 / 1080);
            ViewGroup.LayoutParams params = mSlideShow.getLayoutParams();
            if (params.height != aimHeight) {
                params.height = aimHeight;
                mSlideShow.setLayoutParams(params);
            }
        });
    }

    private void generateCategories() {
        if (!categories.isEmpty()) categories.clear();

        categories.add(Category.generateCategory(0, getString(R.string.school_helper),
                R.mipmap.school_category_helper, new Intent(mContext, AboutActivity.class)));

        categories.add(Category.generateCategory(1, getString(R.string.school_notice),
                R.mipmap.school_category_notice, new Intent(mContext, AboutActivity.class)));

        categories.add(Category.generateCategory(2, getString(R.string.school_work),
                R.mipmap.school_category__work, new Intent(mContext, AboutActivity.class)));

        categories.add(Category.generateCategory(3, getString(R.string.school_follow),
                R.mipmap.school_category_follow, new Intent(mContext, TeachmatsOrUnitsActivity.class)));

        categories.add(Category.generateCategory(4, getString(R.string.school_schedule),
                R.mipmap.school_category_schedule, new Intent(mContext, ScheduleActivity.class)));

        categories.add(Category.generateCategory(5, getString(R.string.school_score),
                R.mipmap.school_category_score, new Intent(mContext, ScoresActivity.class)));

        categories.add(Category.generateCategory(6, getString(R.string.school_register),
                R.mipmap.school_category_register, new Intent(mContext, AboutActivity.class)));

        if (UserHelper.getUserId(mContext) > 0)
            categories.add(Category.generateCategory(7, getString(R.string.accounts_title),
                    R.mipmap.school_category_accounts, new Intent(mContext, AccountsActivity.class)));
    }

    @Override
    protected void bindListeners() {
        mSlideShow.setOnItemClickListener((view, position, dummy) -> {
            if (dummy instanceof Activity) {
                Activity activity = (Activity) dummy;

                if (TextUtils.isEmpty(activity.activityUrl) || activity.isOutUrl == 0) return;

                WebLink webLink = new WebLink();
                webLink.title = TextUtils.isEmpty(activity.title) ? getString(R.string.app_name) : activity.title;
                webLink.forwardUrl = activity.activityUrl;

                Intent intent = new Intent(mContext, WebLinkActivity.class);
                intent.putExtra("link", webLink);
                intent.putExtra("canShare", true);
                startActivity(intent);
            }
        });

        mCategoryAdapter.setOnItemClickListener((view, category) -> {
            if (category != null && category.intent != null) startActivity(category.intent);
        });
    }

    private void requestCarousels(int position) {
        int gradeId = UserHelper.getRealAttentionGrade(mContext);

        Map<String, String> paras = new HashMap<>();
        paras.put("CMD", CmdConst.CMD_GET_ACTIVITIES);
        if (gradeId > 0) paras.put("gradeId", String.valueOf(gradeId));
        paras.put("position", String.valueOf(position));
        paras.put("serviceType", "6");

        subscriberActivity = BaseHttpUrlRequests.getInstance().getActivities(paras, new Subscriber<ActivitiesResp>() {
            @Override
            public void onCompleted() {
                handler.sendEmptyMessageDelayed(1, 500);
            }

            @Override
            public void onError(Throwable e) {
                toast(RetrofitConfig.handleReqError(e));
                handler.sendEmptyMessage(1);
            }

            @Override
            public void onNext(ActivitiesResp activitiesResp) {
                if (!RetrofitConfig.handleResp(activitiesResp, mContext))
                    toast(activitiesResp.message);
                else {
                    dummies.clear();
                    List<Activity> temp = activitiesResp.activityEntities;
                    if (temp != null && !temp.isEmpty()) {
                        dummies.addAll(temp);
                        mSlideShow.setDummies(dummies);
                    }
                }
            }
        });
    }

    @Override
    public void refresh(Uri uri) {
        handler.sendEmptyMessage(0);
    }

    @Override
    public void scroll(Uri uri) {
        mContentView.smoothScrollTo(0, 0);
    }

    @Override
    public SlideShowPlayHandler getHandler() {
        return mSlideShowHandler;
    }

    @Override
    public SlideShowView getSlideShowView() {
        return mSlideShow;
    }

    @Override
    protected void handleMessage(HandlerFragment fragment, Message msg) {
        switch (msg.what) {
            case 0:
                if (isRequesting) break;

                isRequesting = true;
                new Thread(() -> requestCarousels(1)).start();
                break;

            case 1:
                handler.postDelayed(() -> {
                    onInteraction(Uri.parse("refresh?ready=true"));
                    isRequesting = false;
                }, 1500);
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (subscriberActivity != null && !subscriberActivity.isUnsubscribed())
            subscriberActivity.unsubscribe();

        mContext.unregisterReceiver(attentionChangeReceiver);
    }
}
