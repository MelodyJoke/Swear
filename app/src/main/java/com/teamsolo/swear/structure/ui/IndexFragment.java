package com.teamsolo.swear.structure.ui;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.teamsolo.base.template.fragment.BaseFragment;
import com.teamsolo.swear.R;
import com.teamsolo.swear.foundation.bean.Child;
import com.teamsolo.swear.foundation.ui.FabInteractAble;
import com.teamsolo.swear.foundation.ui.Refreshable;
import com.teamsolo.swear.foundation.ui.ScrollAble;
import com.teamsolo.swear.foundation.ui.SearchAble;
import com.teamsolo.swear.foundation.ui.adapter.CommonPagerAdapter;
import com.teamsolo.swear.structure.ui.mine.OrdersFragment;
import com.teamsolo.swear.structure.ui.news.NewsFragment;
import com.teamsolo.swear.structure.ui.school.SchoolFragment;
import com.teamsolo.swear.structure.util.UserHelper;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * description: index page
 * author: Melody
 * date: 2016/9/24
 * version: 0.0.0.1
 */
public class IndexFragment extends BaseFragment implements
        Refreshable, SearchAble, FabInteractAble {

    private TabLayout mTabLayout;

    private FloatingActionButton mFab;

    private ViewPager mContainer;

    private String[] mIndicators;

    private CommonPagerAdapter mPagerAdapter;

    private List<Fragment> mFragments = new ArrayList<>();

    private int current;

    public static IndexFragment newInstance() {
        IndexFragment fragment = new IndexFragment();
        fragment.setArguments(new Bundle());

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setContentView(inflater, R.layout.fragment_view_pager, container);
        initViews();
        bindListeners();

        return mLayoutView;
    }

    @Override
    protected void getBundle(@NotNull Bundle bundle) {
        mIndicators = getResources().getStringArray(R.array.index_indicators);
    }

    @Override
    protected void initViews() {
        mContainer = (ViewPager) findViewById(R.id.container);

        mTabLayout.setupWithViewPager(mContainer);

        initPagers();
    }

    private void initPagers() {
        mFragments.add(SchoolFragment.newInstance());
        mFragments.add(NewsFragment.newInstance());
        mFragments.add(OrdersFragment.newInstance(2));
        mFragments.add(OrdersFragment.newInstance(3));

        mPagerAdapter = new CommonPagerAdapter(getChildFragmentManager(), mIndicators, mFragments).setRecycle(false);
        mContainer.setAdapter(mPagerAdapter);
    }

    @Override
    protected void bindListeners() {
        mContainer.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                current = position;
                interact(mFab, null);
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
                if (currentFragment instanceof ScrollAble)
                    ((ScrollAble) currentFragment).scroll(Uri.parse("scroll?top=true"));
            }
        });
    }

    public IndexFragment setTabLayout(TabLayout tabLayout, FloatingActionButton fab) {
        this.mTabLayout = tabLayout;
        this.mFab = fab;

        return this;
    }

    @Override
    public void refresh(Uri uri) {
        Fragment currentFragment = mPagerAdapter.getCurrentFragment();
        if (currentFragment instanceof Refreshable)
            ((Refreshable) currentFragment).refresh(null);
    }

    @Override
    public void search(Uri uri) {
        // TODO: jump to search page
    }

    @Override
    public void interact(FloatingActionButton fab, Uri uri, View... others) {
        fab.setTag(current != 0);
        fab.setImageResource(current != 0 ? R.drawable.ic_search_white_24dp : R.drawable.ic_group_white_24dp);
        List<Child> children = UserHelper.getChildren(mContext);
        fab.setVisibility(current == 0 && (children == null || children.size() <= 1) ? GONE : VISIBLE);
    }
}
