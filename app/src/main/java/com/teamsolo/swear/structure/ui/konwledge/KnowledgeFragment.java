package com.teamsolo.swear.structure.ui.konwledge;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.teamsolo.base.template.fragment.HandlerFragment;
import com.teamsolo.swear.R;
import com.teamsolo.swear.foundation.bean.Activity;
import com.teamsolo.swear.foundation.bean.Carousel;
import com.teamsolo.swear.foundation.bean.Classify;
import com.teamsolo.swear.foundation.bean.WebLink;
import com.teamsolo.swear.foundation.bean.resp.CarouselsResp;
import com.teamsolo.swear.foundation.constant.CmdConst;
import com.teamsolo.swear.foundation.ui.Refreshable;
import com.teamsolo.swear.foundation.ui.ScrollAble;
import com.teamsolo.swear.foundation.ui.SearchAble;
import com.teamsolo.swear.foundation.ui.widget.SlideShowPlayHandler;
import com.teamsolo.swear.foundation.ui.widget.SlideShowView;
import com.teamsolo.swear.foundation.util.RetrofitConfig;
import com.teamsolo.swear.structure.request.KnowledgeHttpUrlRequests;
import com.teamsolo.swear.structure.ui.common.WebLinkActivity;
import com.teamsolo.swear.structure.ui.training.adapter.ClassifyAdapter;

import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Subscriber;

/**
 * description: knowledge fragment
 * author: Melody
 * date: 2016/9/12
 * version: 0.0.0.1
 */
public class KnowledgeFragment extends HandlerFragment implements
        Refreshable, SearchAble, ScrollAble,
        SlideShowView.SlideShowParent {

    private NestedScrollView mContentView;

    private SlideShowView mSlideShow;

    private SlideShowPlayHandler mSlideShowHandler;

    private ClassifyAdapter mClassifyAdapter;

    private boolean isRequesting;

    private List<Classify> classifies = new ArrayList<>();

    private List<SlideShowView.SlideShowDummy> dummies = new ArrayList<>();

    private Subscriber<CarouselsResp> subscriberCarousel;

    public static KnowledgeFragment newInstance() {
        KnowledgeFragment fragment = new KnowledgeFragment();
        fragment.setArguments(new Bundle());

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setContentView(inflater, R.layout.fragment_knowledge, container);
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

        RecyclerView mListViewAbility = (RecyclerView) findViewById(R.id.listView_ability);
        mListViewAbility.setHasFixedSize(true);
        LinearLayoutManager managerAbility = new LinearLayoutManager(mContext);
        managerAbility.setAutoMeasureEnabled(true);
        mListViewAbility.setLayoutManager(managerAbility);
        mListViewAbility.setItemAnimator(new DefaultItemAnimator());
        classifies.add(null);
        mClassifyAdapter = new ClassifyAdapter(mContext, classifies);
        mListViewAbility.setAdapter(mClassifyAdapter);
        mListViewAbility.setNestedScrollingEnabled(false);
        mListViewAbility.setFocusable(false);

        RecyclerView mListViewMental = (RecyclerView) findViewById(R.id.listView_mental);
        mListViewMental.setHasFixedSize(true);
        LinearLayoutManager managerMental = new LinearLayoutManager(mContext);
        managerMental.setAutoMeasureEnabled(true);
        mListViewMental.setLayoutManager(managerMental);
        mListViewMental.setItemAnimator(new DefaultItemAnimator());
        ClassifyAdapter adapter = new ClassifyAdapter(mContext, classifies);
        mListViewMental.setAdapter(adapter);
        mListViewMental.setNestedScrollingEnabled(false);
        mListViewMental.setFocusable(false);

        RecyclerView mGridViewCourseware = (RecyclerView) findViewById(R.id.gridView_courseware);
        mGridViewCourseware.setHasFixedSize(true);
        GridLayoutManager managerCourseware = new GridLayoutManager(mContext, 2);
        managerCourseware.setAutoMeasureEnabled(true);
        mGridViewCourseware.setLayoutManager(managerCourseware);
        mGridViewCourseware.setItemAnimator(new DefaultItemAnimator());
        ClassifyAdapter adapter2 = new ClassifyAdapter(mContext, classifies);
        mGridViewCourseware.setAdapter(adapter2);
        mGridViewCourseware.setNestedScrollingEnabled(false);
        mGridViewCourseware.setFocusable(false);

        RecyclerView mGridViewVideo = (RecyclerView) findViewById(R.id.gridView_video);
        mGridViewVideo.setHasFixedSize(true);
        GridLayoutManager managerVideo = new GridLayoutManager(mContext, 2);
        managerVideo.setAutoMeasureEnabled(true);
        mGridViewVideo.setLayoutManager(managerVideo);
        mGridViewVideo.setItemAnimator(new DefaultItemAnimator());
        ClassifyAdapter adapter3 = new ClassifyAdapter(mContext, classifies);
        mGridViewVideo.setAdapter(adapter3);
        mGridViewVideo.setNestedScrollingEnabled(false);
        mGridViewVideo.setFocusable(false);

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
            if (dummy instanceof Carousel) {
                Carousel carousel = (Carousel) dummy;

                if (carousel.isJump != 1) return;

                switch (carousel.linkType) {
                    case 1:
                        // TODO: jump to activity page
                        break;

                    case 2:
                        // TODO:
                        if (carousel.resourceType == 1) {
                            if ("1".equals(carousel.videoType)) {
                                // video
                            }
                        } else if (carousel.resourceType == 2) {
                            // course ware
                        }
                        break;

                    case 3:
                        if (!TextUtils.isEmpty(carousel.link)) {
                            WebLink webLink = new WebLink();
                            webLink.title = TextUtils.isEmpty(carousel.carouselName) ? getString(R.string.app_name) : carousel.carouselName;
                            webLink.forwardUrl = carousel.link;

                            Intent intent = new Intent(mContext, WebLinkActivity.class);
                            intent.putExtra("link", webLink);
                            intent.putExtra("canShare", true);
                            startActivity(intent);
                        }
                        break;
                }
            }
        });

        findViewById(R.id.category_ability).setOnClickListener(v -> {
            // TODO: ability
        });

        findViewById(R.id.category_mental).setOnClickListener(v -> {
            // TODO: ability
        });

        findViewById(R.id.category_lesson).setOnClickListener(v -> {
            // TODO: ability
        });

        findViewById(R.id.category_classify).setOnClickListener(v -> {
            // TODO: ability
        });
    }

    private void requestCarousels() {
        Map<String, String> paras = new HashMap<>();
        paras.put("CMD", CmdConst.CMD_GET_CAROUSELS);
        paras.put("serviceType", "4");

        subscriberCarousel = KnowledgeHttpUrlRequests.getInstance().getCarousels(paras, new Subscriber<CarouselsResp>() {
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
            public void onNext(CarouselsResp carouselsResp) {
                if (!RetrofitConfig.handleResp(carouselsResp, mContext))
                    toast(carouselsResp.message);
                else {
                    dummies.clear();
                    List<Carousel> temp = carouselsResp.carouselList;
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
    public void search(Uri uri) {

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
                new Thread(this::requestCarousels).start();
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

        if (subscriberCarousel != null && !subscriberCarousel.isUnsubscribed())
            subscriberCarousel.unsubscribe();
    }
}
