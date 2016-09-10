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
import android.widget.AdapterViewFlipper;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.teamsolo.base.template.fragment.HandlerFragment;
import com.teamsolo.swear.R;
import com.teamsolo.swear.foundation.ui.Refreshable;
import com.teamsolo.swear.foundation.ui.ScrollAble;
import com.teamsolo.swear.foundation.ui.SearchAble;

import org.jetbrains.annotations.NotNull;

/**
 * description: training fragment
 * author: Melody
 * date: 2016/9/10
 * version: 0.0.0.1
 */
public class TrainingFragment extends HandlerFragment implements Refreshable, SearchAble, ScrollAble {

    private NestedScrollView mContentView;

    private AdapterViewFlipper mSlideShow;

    private RecyclerView mGridView;

    private TabLayout mTabLayout;

    private ViewPager mContainer;

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

        mSlideShow = (AdapterViewFlipper) findViewById(R.id.slide);
        mSlideShow.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return 2;
            }

            @Override
            public Object getItem(int position) {
                return null;
            }

            @Override
            public long getItemId(int position) {
                return 0;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                ImageView imageView = new ImageView(mContext);
                imageView.setImageResource(position == 0 ? R.mipmap.loading_holder_large : R.mipmap.loading_failed_large);
                imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                return imageView;
            }
        });

        mGridView = (RecyclerView) findViewById(R.id.gridView);
        mGridView.setHasFixedSize(true);
        mGridView.setItemAnimator(new DefaultItemAnimator());

        mTabLayout = (TabLayout) findViewById(R.id.tab);
        mContainer = (ViewPager) findViewById(R.id.container);

        mTabLayout.setupWithViewPager(mContainer);
    }

    @Override
    protected void bindListeners() {

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
    protected void handleMessage(HandlerFragment fragment, Message msg) {

    }
}
