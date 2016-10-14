package com.teamsolo.swear.structure.ui.mine;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;

import com.teamsolo.base.template.activity.BaseActivity;
import com.teamsolo.base.template.fragment.BaseFragment;
import com.teamsolo.base.util.BuildUtility;
import com.teamsolo.swear.R;
import com.teamsolo.swear.foundation.ui.Appendable;

import org.jetbrains.annotations.NotNull;

/**
 * description: collections list page
 * author: Melody
 * date: 2016/10/2
 * version: 0.0.0.1
 */
public class CollectionsActivity extends BaseActivity implements BaseFragment.OnFragmentInteractionListener,
        SwipeRefreshLayout.OnRefreshListener,
        Appendable {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_pagers);

        getBundle(getIntent());
        initViews();
        bindListeners();
    }

    @Override
    protected void getBundle(@NotNull Intent intent) {

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
    }

    @Override
    protected void bindListeners() {

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onRefresh() {

    }

    @Override
    public void append(Uri uri) {

    }
}
