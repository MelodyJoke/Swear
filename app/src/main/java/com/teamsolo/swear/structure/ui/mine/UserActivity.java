package com.teamsolo.swear.structure.ui.mine;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.teamsolo.base.template.activity.HandlerActivity;
import com.teamsolo.base.util.BuildUtility;
import com.teamsolo.base.util.DisplayUtility;
import com.teamsolo.base.util.FileManager;
import com.teamsolo.base.util.LogUtility;
import com.teamsolo.swear.R;
import com.teamsolo.swear.foundation.bean.Child;
import com.teamsolo.swear.foundation.bean.User;
import com.teamsolo.swear.foundation.bean.WebLink;
import com.teamsolo.swear.foundation.constant.NetConst;
import com.teamsolo.swear.structure.ui.common.WebLinkActivity;
import com.teamsolo.swear.structure.util.UserHelper;
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

    private static final int PERMISSION_REQUEST_CODE = 245;

    private static final int PICK_REQUEST_CODE = 137;

    private View mParentLayout, mChildLayout;

    private SimpleDraweeView mParentPortraitImage, mChildPortraitImage;

    private TextView mParentNameText, mChildNameText;

    private TextView mPhoneText, mAppellationText, mMemberText, mPointsText;

    private TextView mSchoolText, mClassText, mIdText;

    private boolean isChildPortrait;

    private User user;

    private Child child;

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
        user = UserHelper.getUser(mContext);
        child = UserHelper.getChild(mContext);
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

        mParentLayout = findViewById(R.id.info_parent);
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
            // TODO: edit parent name
        });

        findViewById(R.id.child_name_layout).setOnClickListener(v -> {
            // TODO: edit child name
        });

        findViewById(R.id.parent_appellation_layout).setOnClickListener(v -> {
            // TODO: edit appellation
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

    @Override
    protected void handleMessage(HandlerActivity activity, Message msg) {

    }
}
