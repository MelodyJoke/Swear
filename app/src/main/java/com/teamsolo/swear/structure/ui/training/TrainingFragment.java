package com.teamsolo.swear.structure.ui.training;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.teamsolo.base.template.fragment.HandlerFragment;
import com.teamsolo.swear.R;
import com.teamsolo.swear.foundation.bean.Activity;
import com.teamsolo.swear.foundation.bean.Classify;
import com.teamsolo.swear.foundation.bean.WebLink;
import com.teamsolo.swear.foundation.bean.resp.ActivitiesResp;
import com.teamsolo.swear.foundation.bean.resp.ClassifiesResp;
import com.teamsolo.swear.foundation.constant.CmdConst;
import com.teamsolo.swear.foundation.ui.Refreshable;
import com.teamsolo.swear.foundation.ui.ScrollAble;
import com.teamsolo.swear.foundation.ui.SearchAble;
import com.teamsolo.swear.foundation.ui.widget.SlideShowPlayHandler;
import com.teamsolo.swear.foundation.ui.widget.SlideShowView;
import com.teamsolo.swear.foundation.util.RetrofitConfig;
import com.teamsolo.swear.structure.request.BaseHttpUrlRequests;
import com.teamsolo.swear.structure.ui.common.WebLinkActivity;
import com.teamsolo.swear.structure.ui.training.adapter.ClassifyAdapter;
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

    private TabLayout mTabLayout;

    private ViewPager mContainer;

    private SlideShowPlayHandler mSlideShowHandler;

    private ClassifyAdapter mClassifyAdapter;

    private PagerAdapter mPagerAdapter;

    private boolean isRequesting;

    private List<SlideShowView.SlideShowDummy> dummies = new ArrayList<>();

    private List<Classify> classifies = new ArrayList<>();

    private List<Classify> classifiesAno = new ArrayList<>();

    private Subscriber<ActivitiesResp> subscriberActivity;

    private Subscriber<ClassifiesResp> subscriberClassify;

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

        RecyclerView mGridView = (RecyclerView) findViewById(R.id.gridView);
        mGridView.setHasFixedSize(true);
        GridLayoutManager manager = new GridLayoutManager(mContext, 4);
        manager.setAutoMeasureEnabled(true);
        mGridView.setLayoutManager(manager);
        mGridView.setItemAnimator(new DefaultItemAnimator());
        mClassifyAdapter = new ClassifyAdapter(mContext, classifies);
        mGridView.setAdapter(mClassifyAdapter);
        mGridView.setNestedScrollingEnabled(false);
        mGridView.setFocusable(false);

        mPagerAdapter = new PagerAdapter() {
            @Override
            public int getCount() {
                return classifiesAno.size();
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return classifiesAno.get(position).name;
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                return new TextView(mContext);
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {

            }
        };
        mContainer = (ViewPager) findViewById(R.id.container);
        mContainer.setAdapter(mPagerAdapter);

        mTabLayout = (TabLayout) findViewById(R.id.tab);
        mTabLayout.setTabMode(TabLayout.MODE_FIXED);
        mTabLayout.setupWithViewPager(mContainer);

        mSlideShow.post(() -> {
            int width = mSlideShow.getMeasuredWidth();
            int aimHeight = width * 399 / 1080;
            ViewGroup.LayoutParams params = mSlideShow.getLayoutParams();
            if (params.height != aimHeight) {
                params.height = aimHeight;
                mSlideShow.setLayoutParams(params);
            }
        });
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

        mClassifyAdapter.setOnItemClickListener((view, classify) -> {
            // TODO:
            System.out.println(classify.name);
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

    private void requestClassifies(int position) {
        Map<String, String> paras = new HashMap<>();
        paras.put("CMD", CmdConst.CMD_GET_CLASSIFIES);
        paras.put("serviceType", "5");
        paras.put("type", String.valueOf(position));

        subscriberClassify = BaseHttpUrlRequests.getInstance().getClassifies(paras, new Subscriber<ClassifiesResp>() {
            @Override
            public void onCompleted() {
                handler.sendEmptyMessage(position == 1 ? 2 : 3);
            }

            @Override
            public void onError(Throwable e) {
                toast(RetrofitConfig.handleReqError(e));
                handler.sendEmptyMessage(position == 1 ? 2 : 3);
            }

            @Override
            public void onNext(ClassifiesResp classifiesResp) {
                if (!RetrofitConfig.handleResp(classifiesResp, mContext))
                    toast(classifiesResp.message);
                else {
                    if (position == 1) {
                        classifies.clear();
                        List<Classify> temp = classifiesResp.classifyList;
                        if (temp != null && !temp.isEmpty()) {
                            classifies.addAll(temp);
                            mClassifyAdapter.notifyDataSetChanged();
                        }
                    } else {
                        classifiesAno.clear();

                        Classify recClassify = new Classify();
                        recClassify.name = getString(R.string.training_rec);
                        recClassify.classificationType = 2;
                        recClassify.classificationId = -1;
                        classifiesAno.add(recClassify);

                        List<Classify> temp = classifiesResp.classifyList;
                        if (temp != null && !temp.isEmpty()) {
                            classifiesAno.addAll(temp);
                            mPagerAdapter.notifyDataSetChanged();
                        }
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
                new Thread(() -> requestClassifies(1)).start();
                break;

            case 2:
                new Thread(() -> requestClassifies(2)).start();
                break;

            case 3:
                onInteraction(Uri.parse("refresh?ready=true"));
                isRequesting = false;
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (subscriberActivity != null && !subscriberActivity.isUnsubscribed())
            subscriberActivity.unsubscribe();

        if (subscriberClassify != null && !subscriberClassify.isUnsubscribed())
            subscriberClassify.unsubscribe();
    }
}
