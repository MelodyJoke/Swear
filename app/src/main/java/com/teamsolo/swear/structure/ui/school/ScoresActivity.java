package com.teamsolo.swear.structure.ui.school;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.TextView;

import com.teamsolo.base.template.activity.BaseActivity;
import com.teamsolo.base.util.BuildUtility;
import com.teamsolo.swear.R;
import com.teamsolo.swear.foundation.bean.Child;
import com.teamsolo.swear.structure.util.LoadingUtil;
import com.teamsolo.swear.structure.util.UserHelper;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * description Scores history page
 * author Melo Chan
 * date 2017/1/3
 * version 0.0.0.1
 */

public class ScoresActivity extends BaseActivity {

    private static final int PERMISSION_REQUEST_CODE = 244;

    private FloatingActionButton mFab;

    private CheckedTextView mPrevButton, mNextButton;

    private TextView mTitleText;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    private RecyclerView mListView;

    private View mLoadingView;

    private AlertDialog serviceDialog;

    private LoadingUtil mLoadingUtil;

    private int status;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scores);

        getBundle(getIntent());
        initViews();
        bindListeners();

        judge();
    }

    @Override
    protected void getBundle(@NotNull Intent intent) {
        if (UserHelper.getUserId(mContext) <= 0 || !UserHelper.isWXUser(mContext))
            status = 1;
        else {
            List<Child> children = UserHelper.getChildren(mContext);
            if (children == null || children.isEmpty()) status = 2;
            else status = 0;
        }
    }

    @Override
    protected void initViews() {
        mListView = (RecyclerView) findViewById(R.id.listView);
        mListView.setHasFixedSize(true);
        mListView.setItemAnimator(new DefaultItemAnimator());
        mListView.setLayoutManager(new LinearLayoutManager(mContext));

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setOnClickListener(v -> mListView.smoothScrollToPosition(0));
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> {
            if (BuildUtility.isRequired(Build.VERSION_CODES.LOLLIPOP)) finishAfterTransition();
            else finish();
        });

        mFab = (FloatingActionButton) findViewById(R.id.fab);
        List<Child> children = UserHelper.getChildren(mContext);
        mFab.setVisibility(children == null || children.isEmpty() ? View.GONE : View.VISIBLE);

        mPrevButton = (CheckedTextView) findViewById(R.id.prev);
        mNextButton = (CheckedTextView) findViewById(R.id.next);
        mPrevButton.setEnabled(false);
        mNextButton.setEnabled(false);

        mTitleText = (TextView) findViewById(R.id.title);
        mTitleText.setText(R.string.loading);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe);
        mSwipeRefreshLayout.setColorSchemeColors(
                Color.parseColor("#F44336"),
                Color.parseColor("#FF5722"),
                Color.parseColor("#CDDC39"),
                Color.parseColor("#4CAF50"));

        mLoadingView = findViewById(R.id.loading);
        mLoadingUtil = new LoadingUtil(mLoadingView, mListView, findViewById(R.id.title_layout));
        mLoadingUtil.init(R.mipmap.schools_empty, R.string.loading, R.string.school_schedule_without_school).showLoading();
    }

    @Override
    protected void bindListeners() {
        mLoadingView.setOnClickListener(v -> {
            if (status == 1) {
                if (BuildUtility.isRequired(Build.VERSION_CODES.LOLLIPOP)) finishAfterTransition();
                else finish();
            } else if (status == 2)
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.CALL_PHONE
                }, PERMISSION_REQUEST_CODE);
        });

        mFab.setOnClickListener(v -> {
            // TODO: go to analyze page
            mPrevButton.setEnabled(true);
            mNextButton.setEnabled(true);
        });

        mPrevButton.setOnClickListener(v -> {
            // TODO:
        });

        mNextButton.setOnClickListener(v -> {
            // TODO:
        });

        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            // TODO:
        });
    }

    private void judge() {
        if (status == 1) {
            mLoadingUtil.init(R.mipmap.schools_empty, R.string.loading, R.string.school_schedule_without_school).showEmpty();
            mSwipeRefreshLayout.setEnabled(false);
        } else if (status == 2) {
            mLoadingUtil.init(R.mipmap.classes_empty, R.string.loading, R.string.school_schedule_without_child).showEmpty();
            mSwipeRefreshLayout.setEnabled(false);
        } else {
            mSwipeRefreshLayout.setRefreshing(true);
            new Thread(this::request).start();
        }
    }

    private void request() {
        new Handler(getMainLooper()).postDelayed(() -> {
            mSwipeRefreshLayout.setRefreshing(false);
            mLoadingUtil.dismiss();
            mPrevButton.setChecked(true);
        }, 3000);
    }

    @SuppressWarnings("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_CODE) {
            int length = permissions.length;
            for (int i = 0; i < length; i++)
                if (permissions[i].equals(Manifest.permission.CALL_PHONE))
                    if (grantResults[i] == PermissionChecker.PERMISSION_GRANTED) {
                        if (serviceDialog == null)
                            serviceDialog = new AlertDialog.Builder(mContext)
                                    .setTitle(R.string.login_service)
                                    .setMessage(R.string.service_phone)
                                    .setPositiveButton(R.string.ok, (dialog, which) -> {
                                        Intent intent = new Intent(Intent.ACTION_CALL);
                                        intent.setData(Uri.parse("tel:" + getString(R.string.real_phone)));
                                        startActivity(intent);
                                    })
                                    .setNegativeButton(R.string.cancel, null)
                                    .create();
                        serviceDialog.show();
                    } else toast(R.string.permission_deny);
        }
    }
}
