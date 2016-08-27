package com.teamsolo.swear.structure.ui.login;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.teamsolo.base.template.activity.HandlerActivity;
import com.teamsolo.base.util.BuildUtility;
import com.teamsolo.base.util.SecurityUtility;
import com.teamsolo.swear.R;
import com.teamsolo.swear.foundation.bean.WebLink;
import com.teamsolo.swear.foundation.bean.resp.LoginResp;
import com.teamsolo.swear.foundation.constant.DbConst;
import com.teamsolo.swear.foundation.constant.NetConst;
import com.teamsolo.swear.foundation.constant.SpConst;
import com.teamsolo.swear.foundation.util.RetrofitConfig;
import com.teamsolo.swear.structure.request.BaseHttpUrlRequests;
import com.teamsolo.swear.structure.ui.WebLinkActivity;
import com.teamsolo.swear.structure.util.UserHelper;
import com.teamsolo.swear.structure.util.db.UserDbHelper;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Subscriber;

/**
 * description: login page
 * author: Melody
 * date: 2016/8/25
 * version: 0.0.0.1
 */
public class LoginActivity extends HandlerActivity {

    private static final int PERMISSION_REQUEST_CODE = 477;

    private SimpleDraweeView mPortraitView;

    private AutoCompleteTextView mPhoneEdit;

    private TextInputEditText mPasswordEdit;

    private Button mLoginButton, mRegisterButton;

    private TextView mSkipButton, mServiceButton, mHelpButton;

    private ArrayAdapter<String> mPhoneEditAdapter;

    private Map<String, Map<String, String>> userMap;

    private List<String> phones = new ArrayList<>();

    private AlertDialog serviceDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getBundle(getIntent());
        initViews();
        bindListeners();

        new Thread(this::prepare).start();
    }

    @Override
    protected void handleMessage(HandlerActivity activity, Message msg) {

    }

    @Override
    protected void getBundle(@NotNull Intent intent) {

    }

    @Override
    protected void initViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mPortraitView = (SimpleDraweeView) findViewById(R.id.portrait);
        mPhoneEdit = (AutoCompleteTextView) findViewById(R.id.phone);
        mPasswordEdit = (TextInputEditText) findViewById(R.id.password);
        mLoginButton = (Button) findViewById(R.id.login);
        mRegisterButton = (Button) findViewById(R.id.register);
        mSkipButton = (TextView) findViewById(R.id.skip);
        mServiceButton = (TextView) findViewById(R.id.service);
        mHelpButton = (TextView) findViewById(R.id.help);

        mPhoneEditAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_dropdown_item_1line, phones);
        mPhoneEdit.setAdapter(mPhoneEditAdapter);
    }

    @Override
    protected void bindListeners() {
        mPhoneEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            @SuppressWarnings("deprecation")
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (start + count == 11 && checkPhone(s.toString())) {
                    Map<String, String> user = userMap.get(s.toString());
                    if (user != null) {
                        if ("1".equals(user.get(DbConst.TABLE_USER_FIELDS[4][0])))
                            mPasswordEdit.setText(user.get(DbConst.TABLE_USER_FIELDS[2][0]));
                        else mPasswordEdit.getText().clear();

                        String portrait = user.get(DbConst.TABLE_USER_FIELDS[3][0]);
                        if (!TextUtils.isEmpty(portrait))
                            mPortraitView.setImageURI(Uri.parse(portrait));
                        else mPortraitView.setImageResource(R.drawable.portrait_default);
                    } else {
                        mPasswordEdit.getText().clear();
                        mPortraitView.setImageResource(R.drawable.portrait_default);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mPasswordEdit.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_DONE) attemptLogin();

            return true;
        });

        mLoginButton.setOnClickListener(view -> attemptLogin());

        mRegisterButton.setOnClickListener(view -> {
            // TODO: jump to register page
        });

        mSkipButton.setOnClickListener(view -> {
            // TODO: jump to main page
        });

        mServiceButton.setOnClickListener(view ->
                ActivityCompat.requestPermissions(LoginActivity.this, new String[]{
                        Manifest.permission.CALL_PHONE
                }, PERMISSION_REQUEST_CODE));

        mHelpButton.setOnClickListener(view -> {
            WebLink webLink = new WebLink();
            webLink.title = getString(R.string.web_help_center);
            webLink.forwardUrl = NetConst.HTTP + NetConst.getBaseHttpUrl() + NetConst.HELP_CENTER;

            Intent intent = new Intent(mContext, WebLinkActivity.class);
            intent.putExtra("link", webLink);
            startActivity(intent);
        });
    }

    private void prepare() {
        final String phone = PreferenceManager.getDefaultSharedPreferences(mContext).getString(SpConst.LAST_PHONE, "");
        final String password = new String(Base64.decode(
                PreferenceManager.getDefaultSharedPreferences(mContext).getString(SpConst.LAST_PASSWORD, ""),
                Base64.DEFAULT));
        UserDbHelper helper = new UserDbHelper(mContext);
        userMap = helper.load();
        phones.addAll(userMap.keySet());

        handler.post(() -> {
            mPhoneEdit.setText(phone);
            mPasswordEdit.setText(password);
            mPhoneEditAdapter.notifyDataSetChanged();
        });
    }

    private void attemptLogin() {
        mLoginButton.setClickable(false);
        mRegisterButton.setClickable(false);
        mSkipButton.setClickable(false);
        mServiceButton.setClickable(false);
        mHelpButton.setClickable(false);

        mLoginButton.setText(R.string.login_signing);

        final String phone = mPhoneEdit.getText().toString().trim();
        final String password = mPasswordEdit.getText().toString().trim();

        // check phone and password
        if (!checkPhone(phone) || !checkPassword(password)) {
            mLoginButton.setClickable(true);
            mRegisterButton.setClickable(true);
            mSkipButton.setClickable(true);
            mServiceButton.setClickable(true);
            mHelpButton.setClickable(true);

            mLoginButton.setText(R.string.login_login);
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
                if (mLoginButton != null) {
                    mLoginButton.setClickable(true);
                    mRegisterButton.setClickable(true);
                    mSkipButton.setClickable(true);
                    mServiceButton.setClickable(true);
                    mHelpButton.setClickable(true);

                    mLoginButton.setText(R.string.login_login);
                }
            }

            @Override
            public void onError(Throwable e) {
                Snackbar.make(mPortraitView, RetrofitConfig.handleReqError(e), Snackbar.LENGTH_LONG).show();

                mLoginButton.setClickable(true);
                mRegisterButton.setClickable(true);
                mSkipButton.setClickable(true);
                mServiceButton.setClickable(true);
                mHelpButton.setClickable(true);

                mLoginButton.setText(R.string.login_login);
            }

            @Override
            public void onNext(LoginResp loginResp) {
                if (loginResp.code != 200) toast(loginResp.message);
                else {
                    PreferenceManager.getDefaultSharedPreferences(mContext).edit()
                            .putString(SpConst.LAST_PHONE, phone)
                            .putString(SpConst.LAST_PASSWORD, Base64.encodeToString(password.getBytes(), Base64.DEFAULT))
                            .apply();
                    UserHelper.saveUserInfo(loginResp.getUser().merge(password), mContext);
                }
            }
        });
    }

    private boolean checkPhone(String phone) {
        if (!TextUtils.isEmpty(phone) && phone.length() == 11 && (phone.startsWith("13") || phone.startsWith("14") || phone.startsWith("15") || phone.startsWith("18")))
            return true;
        else {
            Snackbar.make(mPortraitView, R.string.login_error_phone, Snackbar.LENGTH_SHORT).show();
            mPhoneEdit.requestFocus();
            return false;
        }
    }

    private boolean checkPassword(String password) {
        if (!TextUtils.isEmpty(password) && password.length() >= 6) return true;
        else {
            Snackbar.make(mPortraitView, R.string.login_error_password, Snackbar.LENGTH_SHORT).show();
            mPasswordEdit.requestFocus();
            return false;
        }
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
                    } else
                        Snackbar.make(mPortraitView, R.string.permission_deny, Snackbar.LENGTH_LONG).show();
        }
    }
}
