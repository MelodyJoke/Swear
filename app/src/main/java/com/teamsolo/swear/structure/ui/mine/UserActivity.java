package com.teamsolo.swear.structure.ui.mine;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;

import com.teamsolo.base.template.activity.HandlerActivity;
import com.teamsolo.base.util.BuildUtility;
import com.teamsolo.base.util.DisplayUtility;
import com.teamsolo.base.util.FileManager;
import com.teamsolo.swear.R;
import com.yalantis.ucrop.UCrop;

import org.jetbrains.annotations.NotNull;

import java.io.File;

/**
 * description: user info page
 * author: Melody
 * date: 2016/9/29
 * version: 0.0.0.1
 */
public class UserActivity extends HandlerActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

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

        toolbar.setOnClickListener(v -> {
            DisplayMetrics metrics = DisplayUtility.getDisplayMetrics();
            if (metrics == null) return;

            UCrop.Options options = new UCrop.Options();
            options.setToolbarColor(getResources().getColor(R.color.colorPrimary));
            options.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
            options.setActiveWidgetColor(getResources().getColor(R.color.colorAccent));
            UCrop.of(Uri.fromFile(new File(FileManager.CACHE_PATH, "cache.jpg")), Uri.fromFile(new File(FileManager.CACHE_PATH, "temp.jpg")))
                    .withOptions(options)
                    .withAspectRatio(1, 1)
                    .withMaxResultSize(metrics.widthPixels, metrics.heightPixels)
                    .start(this);
        });
    }

    @Override
    protected void bindListeners() {

    }

    @Override
    protected void handleMessage(HandlerActivity activity, Message msg) {

    }
}
