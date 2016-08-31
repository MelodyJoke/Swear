package com.teamsolo.swear.foundation.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import java.util.Arrays;
import java.util.List;

/**
 * description: fragment pager adapter
 * author: Melody
 * date: 2016/8/31
 * version: 0.0.0.1
 */
@SuppressWarnings("WeakerAccess, unused")
public class CommonPagerAdapter extends FragmentPagerAdapter {

    protected List<String> indicators;

    protected List<Fragment> fragments;

    protected Fragment currentFragment;

    protected boolean recycle = true;

    public CommonPagerAdapter(FragmentManager fm, List<String> indicators, List<Fragment> fragments) {
        super(fm);
        this.indicators = indicators;
        this.fragments = fragments;
    }

    public CommonPagerAdapter(FragmentManager fm, String[] indicators, List<Fragment> fragments) {
        super(fm);
        this.indicators = Arrays.asList(indicators);
        this.fragments = fragments;
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (indicators.size() > position) return indicators.get(position);
        return null;
    }

    @Override
    public Fragment getItem(int position) {
        if (fragments.size() > position) return fragments.get(position);
        return null;
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        if (object != null) currentFragment = (Fragment) object;
        super.setPrimaryItem(container, position, object);
    }

    public Fragment getCurrentFragment() {
        return currentFragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        if (recycle) super.destroyItem(container, position, object);
    }

    public CommonPagerAdapter setRecycle(boolean recycle) {
        this.recycle = recycle;
        return this;
    }
}
