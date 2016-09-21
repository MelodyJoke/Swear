package com.teamsolo.swear.structure.ui.mine;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.EditorInfo;

import com.teamsolo.base.bean.CommonResponse;
import com.teamsolo.base.template.activity.HandlerActivity;
import com.teamsolo.base.util.BuildUtility;
import com.teamsolo.swear.R;
import com.teamsolo.swear.foundation.bean.Relationship;
import com.teamsolo.swear.foundation.constant.CmdConst;
import com.teamsolo.swear.foundation.util.RetrofitConfig;
import com.teamsolo.swear.structure.request.BaseHttpUrlRequests;
import com.teamsolo.swear.structure.ui.mine.view.AppellationPickDialog;
import com.teamsolo.swear.structure.util.UserHelper;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import rx.Subscriber;

/**
 * description: add or edit account page
 * author: Melody
 * date: 2016/9/21
 * version: 0.0.0.1
 */
public class AccountActivity extends HandlerActivity {

    private FloatingActionButton mFab;

    private TextInputEditText mNameEdit, mAppellationEdit, mPhoneEdit;

    private View mCommonsButton;

    private AppellationPickDialog mDialog;

    private boolean isEdit;

    private Relationship relationship;

    private Subscriber<CommonResponse> subscriber;

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

        mFab = (FloatingActionButton) findViewById(R.id.fab);

        mNameEdit = (TextInputEditText) findViewById(R.id.name);
        mAppellationEdit = (TextInputEditText) findViewById(R.id.appellation);
        mPhoneEdit = (TextInputEditText) findViewById(R.id.phone);
        mCommonsButton = findViewById(R.id.commons);

        if (isEdit && relationship != null) {
            mNameEdit.setText(relationship.parentName);
            mAppellationEdit.setText(relationship.appellation);
            mPhoneEdit.setText(relationship.parentPhone);
        }

        mDialog = AppellationPickDialog.newInstance();
    }

    @Override
    protected void bindListeners() {
        mFab.setOnClickListener(v -> attemptSave());

        mPhoneEdit.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_DONE) attemptSave();

            return true;
        });

        mCommonsButton.setOnClickListener(v -> {
            mDialog.show(getSupportFragmentManager(), "");
            mDialog.setAppellation(mAppellationEdit.getText().toString().trim());
        });

        mDialog.setOnButtonClickListener((view, appellation) -> {
            mAppellationEdit.setText(appellation);
            mDialog.dismiss();
        });
    }

    private void attemptSave() {
        String name = mNameEdit.getText().toString().trim();
        String appellation = mAppellationEdit.getText().toString().trim();
        String phone = mPhoneEdit.getText().toString().trim();

        if (checkName(name) && checkAppellation(appellation) && checkPhone(phone) && checkDiff(name, appellation, phone)) {
            requestSave(name, appellation, phone);
        }
    }

    private void requestSave(String name, String appellation, String phone) {
        mFab.setClickable(false);
        toast(isEdit ? R.string.accounts_saving_2 : R.string.accounts_saving);

        Map<String, String> paras = new HashMap<>();
        if (isEdit) {
            paras.put("CMD", CmdConst.CMD_UPDATE_ACCOUNT);
            paras.put("parentsStudentId", String.valueOf(relationship.parentsStudentId));
            paras.put("studentId", String.valueOf(relationship.studentId));
            paras.put("parentsId", String.valueOf(relationship.parentsId));
            paras.put("parentsName", name);
            paras.put("parentPhone", phone);
            paras.put("appellation", appellation);
            paras.put("contactsType", "0");
            paras.put("isMain", String.valueOf(relationship.isMain));
        } else {
            paras.put("CMD", CmdConst.CMD_ADD_ACCOUNT);
            paras.put("studentId", String.valueOf(UserHelper.getStudentId(mContext)));
            paras.put("parentsName", name);
            paras.put("parentPhone", phone);
            paras.put("appellation", appellation);
            paras.put("contactsType", "0");
            paras.put("isMain", "0");
            paras.put("openSMS", "2");
        }

        subscriber = BaseHttpUrlRequests.getInstance().commonReq(paras, new Subscriber<CommonResponse>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                toast(RetrofitConfig.handleReqError(e));
                mFab.setClickable(true);
            }

            @Override
            public void onNext(CommonResponse commonResponse) {
                if (!RetrofitConfig.handleResp(commonResponse, mContext)) {
                    toast(commonResponse.message);
                    mFab.setClickable(true);
                } else {
                    setResult(RESULT_OK);
                    mFab.setClickable(true);
                    finish();
                }
            }
        });
    }

    private boolean checkName(String name) {
        if (TextUtils.isEmpty(name)) {
            toast(R.string.accounts_error_name);
            mNameEdit.requestFocus();
            return false;
        }

        return true;
    }

    private boolean checkAppellation(String appellation) {
        if (TextUtils.isEmpty(appellation)) {
            toast(R.string.accounts_error_appellation);
            mAppellationEdit.requestFocus();
            return false;
        }

        return true;
    }

    private boolean checkPhone(String phone) {
        if (!TextUtils.isEmpty(phone) && phone.length() == 11 && (phone.startsWith("13") || phone.startsWith("14") || phone.startsWith("15") || phone.startsWith("18")))
            return true;

        toast(R.string.accounts_error_phone);
        mPhoneEdit.requestFocus();
        return false;
    }

    private boolean checkDiff(String name, String appellation, String phone) {
        if (!isEdit
                || relationship == null
                || !TextUtils.equals(name, relationship.parentName)
                || !TextUtils.equals(appellation, relationship.appellation)
                || !TextUtils.equals(phone, relationship.parentPhone))
            return true;

        toast(R.string.accounts_error_diff);
        mNameEdit.requestFocus();
        return false;
    }

    @Override
    protected void handleMessage(HandlerActivity activity, Message msg) {

    }

    @Override
    public void toast(int msgRes) {
        Snackbar.make(mFab, msgRes, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void toast(String message) {
        Snackbar.make(mFab, message, Snackbar.LENGTH_LONG).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (subscriber != null && !subscriber.isUnsubscribed()) subscriber.unsubscribe();
    }
}
