package com.teamsolo.swear.structure.ui.about;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.teamsolo.base.template.activity.BaseActivity;
import com.teamsolo.base.util.BuildUtility;
import com.teamsolo.swear.R;

import org.jetbrains.annotations.NotNull;

/**
 * description: about us page
 * author: Melody
 * date: 2016/8/29
 * version: 0.0.0.1
 */
public class AboutActivity extends BaseActivity {

    private static final int PERMISSION_REQUEST_CODE = 792;

    private View mServiceButton, mCallButton, mAgreementButton;

    private AlertDialog serviceDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

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

        ((TextView) findViewById(R.id.version)).setText(BuildUtility.getApkVersionName(mContext));

        mServiceButton = findViewById(R.id.service);
        mCallButton = findViewById(R.id.call);
        mAgreementButton = findViewById(R.id.agreement);
    }

    @Override
    protected void bindListeners() {
        mServiceButton.setOnClickListener(view ->
                ActivityCompat.requestPermissions(AboutActivity.this, new String[]{
                        Manifest.permission.CALL_PHONE
                }, PERMISSION_REQUEST_CODE));

        mCallButton.setOnClickListener(view ->
                ActivityCompat.requestPermissions(AboutActivity.this, new String[]{
                        Manifest.permission.CALL_PHONE
                }, PERMISSION_REQUEST_CODE));

        mAgreementButton.setOnClickListener(view -> startActivity(new Intent(mContext, AgreementActivity.class)));
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
    public void toast(String message) {
        Snackbar.make(mServiceButton, message, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void toast(int msgRes) {
        Snackbar.make(mServiceButton, msgRes, Snackbar.LENGTH_LONG).show();
    }
}
