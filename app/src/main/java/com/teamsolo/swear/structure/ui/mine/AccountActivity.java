package com.teamsolo.swear.structure.ui.mine;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;

import com.teamsolo.base.template.activity.HandlerActivity;
import com.teamsolo.base.util.BuildUtility;
import com.teamsolo.swear.R;
import com.teamsolo.swear.foundation.bean.Relationship;

import org.jetbrains.annotations.NotNull;

/**
 * description: add or edit account page
 * author: Melody
 * date: 2016/9/21
 * version: 0.0.0.1
 */
public class AccountActivity extends HandlerActivity {

    private boolean isEdit;

    private Relationship relationship;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        getBundle(getIntent());
        initViews();
        bindListeners();
    }

    @Override
    protected void getBundle(@NotNull Intent intent) {
        isEdit = intent.getBooleanExtra("isEdit", false);
        if (isEdit) relationship = intent.getParcelableExtra("relationShip");
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
            actionBar.setTitle(isEdit ? R.string.accounts_edit : R.string.accounts_add);
        }
    }

    @Override
    protected void bindListeners() {

    }

    @Override
    protected void handleMessage(HandlerActivity activity, Message msg) {

    }
}
