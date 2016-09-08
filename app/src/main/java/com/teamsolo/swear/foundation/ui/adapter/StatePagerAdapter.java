package com.teamsolo.swear.foundation.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import java.util.Arrays;
import java.util.List;

/**
 * description: state fragment page adapetr
 * author: Melody
 * date: 2016/9/8
 * version: 0.0.0.1
 */
@SuppressWarnings("WeakerAccess, unused")
public class StatePagerAdapter extends FragmentStatePagerAdapter {

    protected List<String> indicators;

    protected List<Fragment> fragments;

    protected Fragment currentFragment;

    public StatePagerAdapter(FragmentManager fm, List<String> indicators, List<Fragment> fragments) {
        super(fm);
        this.indicators = indicators;
        this.fragments = fragments;
    }

    public StatePagerAdapter(FragmentManager fm, String[] indicators, List<Fragment> fragments) {
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
}
