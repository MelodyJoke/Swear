package com.teamsolo.swear.structure.ui.training;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.teamsolo.base.template.fragment.HandlerFragment;
import com.teamsolo.swear.R;
import com.teamsolo.swear.foundation.bean.Activity;
import com.teamsolo.swear.foundation.bean.WebLink;
import com.teamsolo.swear.foundation.bean.resp.ActivitiesResp;
import com.teamsolo.swear.foundation.constant.CmdConst;
import com.teamsolo.swear.foundation.ui.Refreshable;
import com.teamsolo.swear.foundation.ui.ScrollAble;
import com.teamsolo.swear.foundation.ui.SearchAble;
import com.teamsolo.swear.foundation.ui.widget.SlideShowPlayHandler;
import com.teamsolo.swear.foundation.ui.widget.SlideShowView;
import com.teamsolo.swear.foundation.util.RetrofitConfig;
import com.teamsolo.swear.structure.request.BaseHttpUrlRequests;
import com.teamsolo.swear.structure.ui.common.WebLinkActivity;
import com.teamsolo.swear.structure.util.UserHelper;

import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Subscriber;

/**
 * description: training fragment
 * author: Melody
 * date: 2016/9/10
 * version: 0.0.0.1
 */
public class TrainingFragment extends HandlerFragment implements
        Refreshable, SearchAble, ScrollAble,
        SlideShowView.SlideShowParent {

    private NestedScrollView mContentView;

    private SlideShowView mSlideShow;

    private RecyclerView mGridView;

    private TabLayout mTabLayout;

    private ViewPager mContainer;

    private SlideShowPlayHandler mSlideShowHandler;

    private boolean isRequesting;

    private Subscriber<ActivitiesResp> subscriberActivity;

    private List<SlideShowView.SlideShowDummy> dummies = new ArrayList<>();

    public static TrainingFragment newInstance() {
        TrainingFragment fragment = new TrainingFragment();
        fragment.setArguments(new Bundle());

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setContentView(inflater, R.layout.fragment_training, container);
        initViews();
        bindListeners();
        onInteraction(Uri.parse("refresh?start=true"));
        handler.sendEmptyMessage(0);

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

        mGridView = (RecyclerView) findViewById(R.id.gridView);
        mGridView.setHasFixedSize(true);
        mGridView.setItemAnimator(new DefaultItemAnimator());

        mTabLayout = (TabLayout) findViewById(R.id.tab);
        mContainer = (ViewPager) findViewById(R.id.container);

        mTabLayout.setupWithViewPager(mContainer);
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
    }

    private void requestActivities() {
        int gradeId = UserHelper.getRealAttentionGrade(mContext);

        Map<String, String> paras = new HashMap<>();
        paras.put("CMD", CmdConst.CMD_GET_ACTIVITIES);
        if (gradeId > 0)
            paras.put("gradeId", String.valueOf(UserHelper.getRealAttentionGrade(mContext)));
        paras.put("position", "1");
        paras.put("serviceType", "6");
        paras.put("displayArea", "2");

        subscriberActivity = BaseHttpUrlRequests.getInstance().getActivities(paras, new Subscriber<ActivitiesResp>() {
            @Override
            public void onCompleted() {
                handler.sendEmptyMessage(1);
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
                        dummies.addAll(activitiesResp.activityEntities);
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
    public void search(Uri uri) {

    }

    @Override
    public void scroll(Uri uri) {
        mContentView.scrollTo(0, 0);
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
                new Thread(this::requestActivities).start();
                break;

            case 1:
                onInteraction(Uri.parse("refresh?ready=true"));
                isRequesting = false;
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (subscriberActivity != null && !subscriberActivity.isUnsubscribed())
            subscriberActivity.isUnsubscribed();
    }
}
