package com.teamsolo.swear.structure.ui.training;

import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.teamsolo.base.template.fragment.HandlerFragment;
import com.teamsolo.swear.R;
import com.teamsolo.swear.foundation.ui.Refreshable;
import com.teamsolo.swear.foundation.ui.ScrollAble;
import com.teamsolo.swear.foundation.ui.SearchAble;
import com.teamsolo.swear.foundation.ui.widget.SlideShowPlayHandler;
import com.teamsolo.swear.foundation.ui.widget.SlideShowView;

import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

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
        new Thread(this::request).start();

        return mLayoutView;
    }

    @Override
    protected void getBundle(@NotNull Bundle bundle) {

    }

    @Override
    protected void initViews() {
        mContentView = (NestedScrollView) findViewById(R.id.content);

        mSlideShow = (SlideShowView) findViewById(R.id.slide);

        List<SlideShowView.SlideShowDummy> urls = new ArrayList<>();
        urls.add(new Test("http://www.tara-china.cn/UploadFiles/Introduce_Members/2016/3/201603191209116196.jpg"));
        urls.add(new Test("http://www.tara-china.cn/UploadFiles/Introduce_Members/2016/3/201603191209137660.jpg"));
        urls.add(new Test("http://www.tara-china.cn/UploadFiles/Introduce_Members/2016/3/201603191209161126.jpg"));
        urls.add(new Test("http://www.tara-china.cn/UploadFiles/Introduce_Members/2016/3/201603191209172768.jpg"));
        urls.add(new Test("http://www.tara-china.cn/UploadFiles/Introduce_Members/2016/3/201603191209206027.jpg"));
        mSlideShow.setDummies(urls);
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

    class Test implements SlideShowView.SlideShowDummy {

        String url;

        Test(String url) {
            this.url = url;
        }

        @Override
        public String getUrl() {
            return url;
        }
    }

    @Override
    protected void bindListeners() {
        mSlideShow.setOnItemClickListener((view, position, dummy) -> {
            if (dummy instanceof Test) toast(((Test) dummy).url);
        });
    }

    private void request() {
        handler.postDelayed(() -> onInteraction(Uri.parse("refresh?ready=true")), 2000);
    }

    @Override
    public void refresh(Uri uri) {
        System.out.println("refresh");
        new Thread(this::request).start();
    }

    @Override
    public void search(Uri uri) {
        System.out.println("search");
    }

    @Override
    public void scroll(Uri uri) {
        System.out.println("scroll");
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

    }
}
