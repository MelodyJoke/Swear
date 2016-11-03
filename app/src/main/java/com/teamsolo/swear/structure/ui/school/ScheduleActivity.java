package com.teamsolo.swear.structure.ui.school;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;

import com.teamsolo.base.template.activity.BaseActivity;
import com.teamsolo.base.util.BuildUtility;
import com.teamsolo.swear.R;
import com.teamsolo.swear.foundation.bean.Child;
import com.teamsolo.swear.foundation.bean.WebLink;
import com.teamsolo.swear.foundation.bean.resp.UrlResp;
import com.teamsolo.swear.foundation.constant.CmdConst;
import com.teamsolo.swear.foundation.util.RetrofitConfig;
import com.teamsolo.swear.structure.request.BaseHttpUrlRequests;
import com.teamsolo.swear.structure.ui.common.WebLinkActivity;
import com.teamsolo.swear.structure.util.LoadingUtil;
import com.teamsolo.swear.structure.util.UserHelper;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Subscriber;

/**
 * description schedule page
 * author Melo Chan
 * date 2016/11/3
 * version 0.0.0.1
 */
public class ScheduleActivity extends BaseActivity {

    private static final int PERMISSION_REQUEST_CODE = 196;

    private View mLoadingView;

    private AlertDialog serviceDialog;

    private LoadingUtil mLoadingUtil;

    private int status;

    private Subscriber<UrlResp> subscriber;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

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
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
        if (toolbar != null)
            toolbar.setNavigationOnClickListener(v -> {
                if (BuildUtility.isRequired(Build.VERSION_CODES.LOLLIPOP)) finishAfterTransition();
                else finish();
            });

        mLoadingView = findViewById(R.id.loading);
        mLoadingUtil = new LoadingUtil(mLoadingView);
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
    }

    private void judge() {
        if (status == 1)
            mLoadingUtil.init(R.mipmap.schools_empty, R.string.loading, R.string.school_schedule_without_school).showEmpty();
        else if (status == 2)
            mLoadingUtil.init(R.mipmap.classes_empty, R.string.loading, R.string.school_schedule_without_child).showEmpty();
        else requestUrl();
    }

    private void requestUrl() {
        Map<String, String> paras = new HashMap<>();
        paras.put("CMD", CmdConst.CMD_SCHEDULE);
        subscriber = BaseHttpUrlRequests.getInstance().getUrl(paras, new Subscriber<UrlResp>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                toast((RetrofitConfig.handleReqError(e)));
            }

            @Override
            public void onNext(UrlResp urlResp) {
                if (!RetrofitConfig.handleResp(urlResp, mContext))
                    toast(urlResp.message);
                else {
                    if (!TextUtils.isEmpty(urlResp.url)) {
                        WebLink webLink = new WebLink();
                        webLink.title = getString(R.string.school_schedule);
                        webLink.forwardUrl = urlResp.url;

                        String sessionId = RetrofitConfig.getSessionId();
                        if (!TextUtils.isEmpty(sessionId))
                            webLink.forwardUrl += "&JSESSIONID=" + sessionId;

                        Intent intent = new Intent(mContext, WebLinkActivity.class);
                        intent.putExtra("link", webLink);
                        startActivity(intent);

                        finish();
                    }
                }
            }
        });
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

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (subscriber != null && !subscriber.isUnsubscribed()) subscriber.unsubscribe();
    }
}
