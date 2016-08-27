package com.teamsolo.swear.structure.ui.load;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.graphics.Palette;
import android.text.TextUtils;
import android.util.Base64;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.teamsolo.base.template.activity.HandlerActivity;
import com.teamsolo.base.util.BuildUtility;
import com.teamsolo.base.util.FileManager;
import com.teamsolo.base.util.SecurityUtility;
import com.teamsolo.swear.R;
import com.teamsolo.swear.foundation.bean.WebLink;
import com.teamsolo.swear.foundation.bean.resp.LoginResp;
import com.teamsolo.swear.foundation.constant.SpConst;
import com.teamsolo.swear.foundation.util.RetrofitConfig;
import com.teamsolo.swear.structure.request.BaseHttpUrlRequests;
import com.teamsolo.swear.structure.ui.login.LoginActivity;
import com.teamsolo.swear.structure.util.UserHelper;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import rx.Subscriber;

/**
 * description: load page
 * author: Melody
 * date: 2016/8/22
 * version: 0.0.0.1
 */
public class LoadActivity extends HandlerActivity {

    private static final int PERMISSION_REQUEST_CODE = 312;

    private static final String[] REQUIRED_PERMISSIONS = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.READ_SMS
    };

    private ImageView mImageView;

    private TextView mTextView;

    private boolean isOut;

    private boolean toLogin;

    private WebLink mWebLink;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);

        getBundle(getIntent());
        initViews();
        bindListeners();

        handler.sendEmptyMessage(0);
    }

    @Override
    protected void handleMessage(HandlerActivity activity, Message msg) {
        switch (msg.what) {
            case 0:
                new Thread(this::loadCoverImage).start();
                break;

            case 1:
                mTextView.setText(R.string.load_permission);
                if (BuildUtility.isRequired(Build.VERSION_CODES.M))
                    requestPermissions(REQUIRED_PERMISSIONS, PERMISSION_REQUEST_CODE);
                else
                    ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, PERMISSION_REQUEST_CODE);
                break;

            case 2:
                mTextView.setText(R.string.load_attempt);
                new Thread(this::attemptLogin).start();
                break;

            case 3:
                if (msg.obj == null) return;

                boolean success = (Boolean) msg.obj;
                mTextView.setText(success ? R.string.load_attempt_success : R.string.load_attempt_failed);
                toLogin = !success;
                jump();
                break;
        }
    }

    @Override
    protected void getBundle(@NotNull Intent intent) {

    }

    @Override
    protected void initViews() {
        mImageView = (ImageView) findViewById(R.id.image);
        mTextView = (TextView) findViewById(R.id.text);
    }

    @Override
    protected void bindListeners() {
        mImageView.setOnClickListener(v -> {
            if (!PreferenceManager.getDefaultSharedPreferences(mContext).getBoolean(SpConst.SETTING_LOAD_OUT, true))
                return;

            if (mWebLink == null || mWebLink.isJump == 0 || TextUtils.isEmpty(mWebLink.forwardUrl))
                return;

            v.setClickable(false);
            handler.postDelayed(() -> mImageView.setClickable(true), 500);

            // TODO: jump to webView
            toast(mWebLink.title + ": " + mWebLink.forwardUrl);
        });
    }

    private void loadCoverImage() {
        handler.sendEmptyMessageDelayed(1, 2000);

        final String loadCover = PreferenceManager.getDefaultSharedPreferences(mContext).getString(SpConst.LOAD_COVER, "");
        final String webLinkJson = PreferenceManager.getDefaultSharedPreferences(mContext).getString(SpConst.LOAD_LINK, "");
        if (!TextUtils.isEmpty(loadCover)) {
            if (PermissionChecker.checkCallingOrSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PermissionChecker.PERMISSION_GRANTED) {
                handler.post(() -> {
                    File coverFile = new File(FileManager.CACHE_PATH, loadCover);
                    if (coverFile.exists()) {
                        mImageView.setImageURI(Uri.fromFile(coverFile));
                        applyTheme(mImageView);
                    } else {
                        PreferenceManager.getDefaultSharedPreferences(mContext).edit()
                                .putString(SpConst.LOAD_COVER, "").apply();
                        applyTheme(mImageView);
                    }

                    if (!TextUtils.isEmpty(webLinkJson))
                        mWebLink = new Gson().fromJson(webLinkJson, WebLink.class);
                });
            } else applyTheme(mImageView);
        } else {
            PreferenceManager.getDefaultSharedPreferences(mContext).edit().putString(SpConst.LOAD_COVER, "cache.jpeg").apply();
            applyTheme(mImageView);
        }
    }

    private void applyTheme(ImageView mImageView) {
        if (!BuildUtility.isRequired(Build.VERSION_CODES.LOLLIPOP)) return;

        Drawable drawable = mImageView.getDrawable();
        if (drawable instanceof BitmapDrawable) {
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            if (bitmap != null)
                Palette.from(bitmap).generate(palette -> {
                    if (palette == null) return;

                    Palette.Swatch swatch = palette.getDominantSwatch();
                    if (swatch == null) return;

                    Window window = getWindow();
                    if (window != null)
                        if (BuildUtility.isRequired(Build.VERSION_CODES.LOLLIPOP))
                            window.setStatusBarColor(swatch.getRgb());
                });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for (int result :
                grantResults)
            if (result != PermissionChecker.PERMISSION_GRANTED) {
                mTextView.setText(R.string.load_permission_failed);
                handler.postDelayed(this::finish, 2000);
                return;
            }

        handler.sendEmptyMessage(2);
    }

    private void attemptLogin() {
        String phone = PreferenceManager.getDefaultSharedPreferences(mContext).getString(SpConst.LAST_PHONE, "");
        String password = new String(Base64.decode(
                PreferenceManager.getDefaultSharedPreferences(mContext).getString(SpConst.LAST_PASSWORD, ""),
                Base64.DEFAULT));

        if (TextUtils.isEmpty(phone) || TextUtils.isEmpty(password)) {
            Message.obtain(handler, 3, false).sendToTarget();
            return;
        }

        Map<String, String> paras = new HashMap<>();
        paras.put("phone", phone);
        paras.put("password", SecurityUtility.MD5(password));
        paras.put("clientType", "1");
        paras.put("serviceType", "1");
        paras.put("currentVersion", BuildUtility.getApkVersionName(this));
        paras.put("deviceId", SecurityUtility.getDeviceId(getApplicationContext()));
        paras.put("equipmentType", android.os.Build.MODEL);
        BaseHttpUrlRequests.getInstance().getLoginInfo(paras, new Subscriber<LoginResp>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                toast(RetrofitConfig.handleReqError(e));
                Message.obtain(handler, 3, false).sendToTarget();
            }

            @Override
            public void onNext(LoginResp loginResp) {
                if (loginResp.code != 200) toast(loginResp.message);
                else {
                    UserHelper.saveUserInfo(loginResp.getUser().merge(password), mContext);
                    Message.obtain(handler, 3, true).sendToTarget();
                }
            }
        });
    }

    private void jump() {
        if (isOut) return;

        if (toLogin) startActivity(new Intent(mContext, LoginActivity.class));
        else toast("main");

        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();

        handler.postDelayed(() -> {
            if (isOut) {
                isOut = false;
                jump();
            }
        }, 500);
    }
}
