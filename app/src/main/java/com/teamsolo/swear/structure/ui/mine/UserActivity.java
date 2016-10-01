package com.teamsolo.swear.structure.ui.mine;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.teamsolo.base.bean.CommonResponse;
import com.teamsolo.base.template.activity.HandlerActivity;
import com.teamsolo.base.util.BuildUtility;
import com.teamsolo.base.util.DisplayUtility;
import com.teamsolo.base.util.FileManager;
import com.teamsolo.base.util.LogUtility;
import com.teamsolo.swear.R;
import com.teamsolo.swear.foundation.bean.Child;
import com.teamsolo.swear.foundation.bean.User;
import com.teamsolo.swear.foundation.bean.WebLink;
import com.teamsolo.swear.foundation.bean.resp.MemberResp;
import com.teamsolo.swear.foundation.bean.resp.PointsResp;
import com.teamsolo.swear.foundation.constant.CmdConst;
import com.teamsolo.swear.foundation.constant.NetConst;
import com.teamsolo.swear.foundation.util.RetrofitConfig;
import com.teamsolo.swear.structure.request.BaseHttpUrlRequests;
import com.teamsolo.swear.structure.ui.common.WebLinkActivity;
import com.teamsolo.swear.structure.ui.mine.view.AppellationPickDialog;
import com.teamsolo.swear.structure.util.DialogUtil;
import com.teamsolo.swear.structure.util.UserHelper;
import com.yalantis.ucrop.UCrop;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import rx.Subscriber;

/**
 * description: user info page
 * author: Melody
 * date: 2016/9/29
 * version: 0.0.0.1
 */
@SuppressWarnings("Convert2streamapi")
public class UserActivity extends HandlerActivity {

    private static final int PERMISSION_REQUEST_CODE = 245;

    private static final int PICK_REQUEST_CODE = 137;

    private View mChildLayout;

    private SimpleDraweeView mParentPortraitImage, mChildPortraitImage;

    private TextView mParentNameText, mChildNameText;

    private TextView mPhoneText, mAppellationText, mMemberText, mPointsText;

    private TextView mSchoolText, mClassText, mIdText;

    private boolean isChildPortrait;

    private User user;

    private Child child;

    private AlertDialog mParentNameDialog, mChildNameDialog, mAppellationDialog;

    private AppellationPickDialog mAppellationPickDialog;

    private boolean hasModified;

    private Subscriber<MemberResp> subscriberMember;

    private Subscriber<PointsResp> subscriberPoints;

    private Subscriber<CommonResponse> subscriberUpdate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        getBundle(getIntent());
        initViews();
        bindListeners();

        requestMemberInfo();
        requestPointsInfo();
    }

    @Override
    protected void getBundle(@NotNull Intent intent) {
        user = UserHelper.getUser(mContext);
        child = UserHelper.getChild(mContext);
    }

    @Override
    protected void initViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> {
            if (hasModified) setResult(RESULT_OK);
            if (BuildUtility.isRequired(Build.VERSION_CODES.LOLLIPOP)) finishAfterTransition();
            else finish();
        });

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        mChildLayout = findViewById(R.id.info_child);

        mParentPortraitImage = (SimpleDraweeView) findViewById(R.id.parent_portrait);
        mChildPortraitImage = (SimpleDraweeView) findViewById(R.id.child_portrait);

        mParentNameText = (TextView) findViewById(R.id.parent_name);
        mChildNameText = (TextView) findViewById(R.id.child_name);

        mPhoneText = (TextView) findViewById(R.id.parent_phone);
        mAppellationText = (TextView) findViewById(R.id.parent_appellation);
        mMemberText = (TextView) findViewById(R.id.parent_member);
        mPointsText = (TextView) findViewById(R.id.parent_points);

        mSchoolText = (TextView) findViewById(R.id.child_school);
        mClassText = (TextView) findViewById(R.id.child_class);
        mIdText = (TextView) findViewById(R.id.child_id);

        invalidateUI();
    }

    private void invalidateUI() {
        if (user != null) {
            try {
                mParentPortraitImage.setImageURI(Uri.parse(user.parentPath));
            } catch (Exception e) {
                mParentPortraitImage.setImageURI(Uri.parse("http://error"));
            }

            if (!TextUtils.isEmpty(user.parentsName)) mParentNameText.setText(user.parentsName);
            else mParentNameText.setText(R.string.unknown);

            if (!TextUtils.isEmpty(user.phone)) mPhoneText.setText(user.phone);
            else mPhoneText.setText(R.string.unknown);
        } else {
            mParentPortraitImage.setImageURI(Uri.parse("http://error"));
            mParentNameText.setText(R.string.unknown);
            mPhoneText.setText(R.string.unknown);
        }

        mChildLayout.setVisibility(child != null ? View.VISIBLE : View.GONE);

        if (child != null) {
            try {
                mChildPortraitImage.setImageURI(Uri.parse(child.portraitPath));
            } catch (Exception e) {
                mChildPortraitImage.setImageURI(Uri.parse("http://error"));
            }

            if (!TextUtils.isEmpty(child.studentName)) mChildNameText.setText(child.studentName);
            else mChildNameText.setText(R.string.unknown);

            if (!TextUtils.isEmpty(child.schoolName)) mSchoolText.setText(child.schoolName);
            else mSchoolText.setText(R.string.unknown);

            if (!TextUtils.isEmpty(child.classinfoName)) mClassText.setText(child.classinfoName);
            else mClassText.setText(R.string.unknown);

            if (!TextUtils.isEmpty(child.schoolRollCard)) mIdText.setText(child.schoolRollCard);
            else mIdText.setText(R.string.unknown);

            if (!TextUtils.isEmpty(child.appellation)) mAppellationText.setText(child.appellation);
            else mAppellationText.setText(R.string.unknown);
        } else {
            mChildPortraitImage.setImageURI(Uri.parse("http://error"));
            mChildNameText.setText(R.string.unknown);
            mSchoolText.setText(R.string.unknown);
            mClassText.setText(R.string.unknown);
            mIdText.setText(R.string.unknown);
            mAppellationText.setText(R.string.unknown);
        }
    }

    @Override
    protected void bindListeners() {
        findViewById(R.id.parent_portrait_layout).setOnClickListener(v -> {
            isChildPortrait = false;

            if (BuildUtility.isRequired(Build.VERSION_CODES.M))
                requestPermissions(new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                }, PERMISSION_REQUEST_CODE);
            else
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                }, PERMISSION_REQUEST_CODE);
        });

        findViewById(R.id.child_portrait_layout).setOnClickListener(v -> {
            isChildPortrait = true;

            if (BuildUtility.isRequired(Build.VERSION_CODES.M))
                requestPermissions(new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                }, PERMISSION_REQUEST_CODE);
            else
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                }, PERMISSION_REQUEST_CODE);
        });

        findViewById(R.id.parent_name_layout).setOnClickListener(v -> {
            if (mParentNameDialog == null)
                mParentNameDialog = DialogUtil.newInstance(mContext, getString(R.string.user_name),
                        mParentNameText.getText().toString(), true, getString(R.string.accounts_commons),
                        (dialogInterface, content) -> updateParentName(content), null);

            mParentNameDialog.show();
        });

        findViewById(R.id.child_name_layout).setOnClickListener(v -> {
            if (mChildNameDialog == null)
                mChildNameDialog = DialogUtil.newInstance(mContext, getString(R.string.user_name),
                        mChildNameText.getText().toString(), true, getString(R.string.accounts_commons),
                        (dialogInterface, content) -> updateChildName(content), null);

            mChildNameDialog.show();
        });

        findViewById(R.id.parent_appellation_layout).setOnClickListener(v -> {
            if (mAppellationDialog == null)
                mAppellationDialog = DialogUtil.newInstance(mContext, getString(R.string.user_appellation),
                        mAppellationText.getText().toString(), false, getString(R.string.accounts_commons),
                        (dialogInterface, content) -> updateAppellation(content),
                        (dialogInterface, content) -> {
                            if (mAppellationPickDialog == null) {
                                mAppellationPickDialog = AppellationPickDialog.newInstance();
                                mAppellationPickDialog.setOnButtonClickListener((view, appellation) -> {
                                    updateAppellation(appellation);
                                    mAppellationPickDialog.dismiss();
                                });
                            }

                            mAppellationDialog.dismiss();
                            mAppellationPickDialog.show(getSupportFragmentManager(), "");
                            mAppellationPickDialog.setAppellation(mAppellationText.getText().toString());
                        });

            mAppellationDialog.show();
        });

        findViewById(R.id.parent_member_layout).setOnClickListener(v -> {
            if (UserHelper.getUserId(mContext) > 0) {
                WebLink webLinkMem = new WebLink();
                webLinkMem.title = getString(R.string.nav_member);
                webLinkMem.forwardUrl = NetConst.HTTP + NetConst.getBaseHttpUrl() + NetConst.PATH_PRE + NetConst.MEMBER_INDEX_URL;

                Intent intentMem = new Intent(mContext, WebLinkActivity.class);
                intentMem.putExtra("link", webLinkMem);
                startActivity(intentMem);
            }
        });

        findViewById(R.id.parent_points_layout).setOnClickListener(v -> {
            if (UserHelper.getUserId(mContext) > 0) {
                WebLink webLinkBP = new WebLink();
                webLinkBP.title = getString(R.string.nav_bonus_point);
                webLinkBP.forwardUrl = NetConst.HTTP + NetConst.getBaseHttpUrl() + NetConst.PATH_PRE + NetConst.BONUS_POINT_URL;

                Intent intentBP = new Intent(mContext, WebLinkActivity.class);
                intentBP.putExtra("link", webLinkBP);
                startActivity(intentBP);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_CODE) {
            for (int result :
                    grantResults)
                if (result != PermissionChecker.PERMISSION_GRANTED) return;

            Intent intent = new Intent();
            if (BuildUtility.isRequired(Build.VERSION_CODES.KITKAT))
                intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
            else intent.setAction(Intent.ACTION_GET_CONTENT);

            intent.setType("image/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);

            startActivityForResult(intent, PICK_REQUEST_CODE);
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == UCrop.REQUEST_CROP) {
            if (resultCode == RESULT_OK) {
                final Uri resultUri = UCrop.getOutput(data);
                // TODO:
                System.out.println((isChildPortrait ? "child" : "parent") + resultUri);
            } else if (resultCode == UCrop.RESULT_ERROR) {
                try {
                    throw UCrop.getError(data);
                } catch (Throwable throwable) {
                    LogUtility.e(getClass().getSimpleName(), throwable.getMessage());
                }
            }
        } else if (requestCode == PICK_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                if (data == null) return;

                DisplayMetrics metrics = DisplayUtility.getDisplayMetrics();
                if (metrics == null) return;

                Uri uri = data.getData();
                String filePath = FileManager.getUriPath(this, uri);
                if (TextUtils.isEmpty(filePath)) return;

                String tempFilePath = System.currentTimeMillis() + ".jpg";

                UCrop.Options options = new UCrop.Options();
                options.setToolbarColor(getResources().getColor(R.color.colorPrimary));
                options.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
                options.setActiveWidgetColor(getResources().getColor(R.color.colorAccent));
                UCrop.of(Uri.fromFile(new File(filePath)), Uri.fromFile(new File(FileManager.CACHE_PATH, tempFilePath)))
                        .withOptions(options)
                        .withAspectRatio(1, 1)
                        .withMaxResultSize(metrics.widthPixels, metrics.heightPixels)
                        .start(this);
            }
        }
    }

    private void requestMemberInfo() {
        Map<String, String> paras = new HashMap<>();
        paras.put("CMD", CmdConst.CMD_MEMBER_INFO);
        paras.put("serviceType", "2");

        subscriberMember = BaseHttpUrlRequests.getInstance().getMemberInfo(paras, new Subscriber<MemberResp>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                toast(RetrofitConfig.handleReqError(e));
                mMemberText.setText(R.string.user_establish);
            }

            @Override
            public void onNext(MemberResp memberResp) {
                if (!RetrofitConfig.handleResp(memberResp, mContext)) {
                    toast(memberResp.message);
                    mMemberText.setText(R.string.user_establish);
                } else {
                    if (memberResp.memberStatus == 0 || memberResp.memberStatus == 2)
                        mMemberText.setText(R.string.user_establish);
                    else
                        mMemberText.setText(String.format(getString(R.string.user_arrival),
                                new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).format(new Date(memberResp.expireTime))));
                }
            }
        });
    }

    private void requestPointsInfo() {
        Map<String, String> paras = new HashMap<>();
        paras.put("CMD", CmdConst.CMD_POINT_INFO);
        paras.put("serviceType", "2");

        subscriberPoints = BaseHttpUrlRequests.getInstance().getPointsInfo(paras, new Subscriber<PointsResp>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                toast(RetrofitConfig.handleReqError(e));
                mPointsText.setText(R.string.unknown);
            }

            @Override
            public void onNext(PointsResp pointsResp) {
                if (!RetrofitConfig.handleResp(pointsResp, mContext)) {
                    toast(pointsResp.message);
                    mPointsText.setText(R.string.unknown);
                } else {
                    if (!TextUtils.isEmpty(pointsResp.currentPoints))
                        mPointsText.setText(pointsResp.currentPoints);
                    else mPointsText.setText(R.string.unknown);
                }
            }
        });
    }

    private void updateParentName(String name) {
        String lastOne = mParentNameText.getText().toString();
        mParentNameText.setText(name);

        if (TextUtils.equals(name, user.parentsName)) return;

        Map<String, String> paras = new HashMap<>();
        paras.put("CMD", CmdConst.CMD_USER_NAME);
        paras.put("parentsName", name);

        requestUpdate(paras, mParentNameText, lastOne);
    }

    private void updateChildName(String name) {
        String lastOne = mChildNameText.getText().toString();
        mChildNameText.setText(name);

        if (TextUtils.equals(name, child.studentName)) return;

        Map<String, String> paras = new HashMap<>();
        paras.put("CMD", CmdConst.CMD_USER_NAME);
        paras.put("name", name);

        requestUpdate(paras, mChildNameText, lastOne);
    }

    private void updateAppellation(String appellation) {
        String lastOne = mAppellationText.getText().toString();
        mAppellationText.setText(appellation);

        if (TextUtils.equals(appellation, child.appellation)) return;

        Map<String, String> paras = new HashMap<>();
        paras.put("CMD", CmdConst.CMD_USER_APPELLATION);
        paras.put("appellation", appellation);

        requestUpdate(paras, mAppellationText, lastOne);
    }

    private void requestUpdate(Map<String, String> paras, final TextView textView, final String lastOne) {
        hasModified = true;

        subscriberUpdate = BaseHttpUrlRequests.getInstance().commonReq(paras, new Subscriber<CommonResponse>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                toast(RetrofitConfig.handleReqError(e));
                textView.setText(lastOne);
            }

            @Override
            public void onNext(CommonResponse commonResponse) {
                if (!RetrofitConfig.handleResp(commonResponse, mContext)) {
                    toast(commonResponse.message);
                    textView.setText(lastOne);
                } else {
                    String parentName = mParentNameText.getText().toString();
                    String childName = mChildNameText.getText().toString();
                    String appellation = mAppellationText.getText().toString();

                    user.parentsName = parentName;
                    child.studentName = childName;
                    child.appellation = appellation;

                    if (user.children != null)
                        for (Child temp :
                                user.children) {
                            if (temp.studentId == child.studentId) {
                                temp.studentName = childName;
                                temp.appellation = appellation;
                            }
                        }

                    UserHelper.saveUserInfo(user, mContext);
                    UserHelper.saveChildInfo(child, mContext);
                }
            }
        });
    }

    @Override
    protected void handleMessage(HandlerActivity activity, Message msg) {

    }

    @Override
    public void toast(int msgRes) {
        Snackbar.make(mParentPortraitImage, msgRes, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void toast(String message) {
        Snackbar.make(mParentPortraitImage, message, Snackbar.LENGTH_LONG).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (subscriberMember != null && !subscriberMember.isUnsubscribed())
            subscriberMember.unsubscribe();

        if (subscriberPoints != null && !subscriberPoints.isUnsubscribed())
            subscriberPoints.unsubscribe();

        if (subscriberUpdate != null && !subscriberUpdate.isUnsubscribed())
            subscriberUpdate.unsubscribe();
    }

    @Override
    public void onBackPressed() {
        if (hasModified) setResult(RESULT_OK);
        super.onBackPressed();
    }
}
