package com.teamsolo.swear.structure.ui.mine;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;

import com.teamsolo.base.template.activity.BaseActivity;
import com.teamsolo.base.template.fragment.BaseFragment;
import com.teamsolo.base.util.BuildUtility;
import com.teamsolo.swear.R;
import com.teamsolo.swear.foundation.ui.Appendable;
import com.teamsolo.swear.foundation.ui.Refreshable;
import com.teamsolo.swear.foundation.ui.adapter.CommonPagerAdapter;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * description: orders list page
 * author: Melody
 * date: 2016/8/31
 * version: 0.0.0.1
 */
public class OrdersActivity extends BaseActivity implements
        BaseFragment.OnFragmentInteractionListener,
        SwipeRefreshLayout.OnRefreshListener,
        Appendable {

    private TabLayout mTabLayout;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    private ViewPager mContainer;

    private CommonPagerAdapter mPagerAdapter;

    private int prePagePosition;

    private String[] mIndicators;

    private List<Fragment> mFragments = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);

        getBundle(getIntent());
        initViews();
        bindListeners();
    }

    @Override
    protected void getBundle(@NotNull Intent intent) {
        prePagePosition = intent.getIntExtra("position", 0);
        mIndicators = getResources().getStringArray(R.array.orders_indicators);
    }

    @Override
    protected void initViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
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

        mTabLayout = (TabLayout) findViewById(R.id.tab);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe);
        mContainer = (ViewPager) findViewById(R.id.container);

        mSwipeRefreshLayout.setColorSchemeColors(
                Color.parseColor("#F44336"),
                Color.parseColor("#FF5722"),
                Color.parseColor("#CDDC39"),
                Color.parseColor("#4CAF50"));

        initPagers();
    }

    private void initPagers() {
        mFragments.add(OrdersFragment.newInstance(0));
        mFragments.add(OrdersFragment.newInstance(1));
        mFragments.add(OrdersFragment.newInstance(2));
        mFragments.add(OrdersFragment.newInstance(3));

        mPagerAdapter = new CommonPagerAdapter(getSupportFragmentManager(), mIndicators, mFragments).setRecycle(false);
        mContainer.setAdapter(mPagerAdapter);
        mTabLayout.setupWithViewPager(mContainer);
        mContainer.setCurrentItem(prePagePosition, true);
    }

    @Override
    protected void bindListeners() {
        mSwipeRefreshLayout.setOnRefreshListener(this);

        mContainer.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (mSwipeRefreshLayout.isRefreshing()) mSwipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                Fragment currentFragment = mPagerAdapter.getCurrentFragment();
                if (currentFragment instanceof Refreshable)
                    ((Refreshable) currentFragment).refresh(Uri.parse("refresh?top=true"));
            }
        });
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        if ("refresh".equals(uri.getPath())) {
            if (uri.getBooleanQueryParameter("ready", false)) {
                if (mSwipeRefreshLayout.isRefreshing()) mSwipeRefreshLayout.setRefreshing(false);
            } else if (uri.getBooleanQueryParameter("start", false))
                if (!mSwipeRefreshLayout.isRefreshing()) mSwipeRefreshLayout.setRefreshing(true);
        }
    }

    @Override
    public void onRefresh() {
        Fragment currentFragment = mPagerAdapter.getCurrentFragment();
        if (currentFragment instanceof Refreshable) ((Refreshable) currentFragment).refresh(null);
    }

    @Override
    public void append(Uri uri) {
        Fragment currentFragment = mPagerAdapter.getCurrentFragment();
        if (currentFragment instanceof Appendable) ((Appendable) currentFragment).append(null);
    }
}
