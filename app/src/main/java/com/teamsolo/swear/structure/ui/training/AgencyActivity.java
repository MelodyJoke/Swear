package com.teamsolo.swear.structure.ui.training;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.facebook.common.executors.CallerThreadExecutor;
import com.facebook.common.references.CloseableReference;
import com.facebook.datasource.DataSource;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.datasource.BaseBitmapDataSubscriber;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.request.ImageRequest;
import com.teamsolo.base.template.activity.HandlerActivity;
import com.teamsolo.base.util.BuildUtility;
import com.teamsolo.base.util.SecurityUtility;
import com.teamsolo.swear.R;
import com.teamsolo.swear.foundation.bean.Agency;
import com.teamsolo.swear.foundation.bean.AgencyComment;
import com.teamsolo.swear.foundation.bean.Course;
import com.teamsolo.swear.foundation.bean.resp.AgencyDetailResp;
import com.teamsolo.swear.foundation.constant.CmdConst;
import com.teamsolo.swear.foundation.ui.widget.HtmlSupportTextView;
import com.teamsolo.swear.foundation.util.RetrofitConfig;
import com.teamsolo.swear.structure.request.BaseHttpUrlRequests;
import com.teamsolo.swear.structure.ui.training.adapter.TelephoneAdapter;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Subscriber;

/**
 * description: agency detail page
 * author: Melody
 * date: 2016/9/14
 * version: 0.0.0.1
 */
public class AgencyActivity extends HandlerActivity {

    private static final int PERMISSION_REQUEST_CODE = 543;

    private ImageView mCoverImage;

    private FloatingActionButton mFab;

    private TextView mTitleText;

    private View mVerifyView;

    private RatingBar mRatingBar;

    private TextView mTeacherRatingText, mQualityRatingText, mEnvironmentRatingText;

    private View mTelephoneLayout, mAddressLayout, mLessonsLayout, mIntroLayout, mEnvironmentLayout, mCommentLayout;

    private TextView mAddressText;

    private HtmlSupportTextView mIntroText;

    private CheckedTextView mExpandButton;

    private AlertDialog serviceDialog;

    private TelephoneAdapter mTelephoneAdapter;

    private Agency mAgency;

    private List<String> mTelephones = new ArrayList<>();

    private List<AgencyComment> mComments = new ArrayList<>();

    private List<Course> mCourses = new ArrayList<>();

    private Subscriber<AgencyDetailResp> subscriber;

    private String tempTelephone;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agency);

        getBundle(getIntent());
        initViews();
        bindListeners();

        handler.sendEmptyMessage(0);
    }

    @Override
    protected void handleMessage(HandlerActivity activity, Message msg) {
        switch (msg.what) {
            case 0:
                new Thread(this::requestCover).start();
                break;

            case 1:
                new Thread(this::requestDetail).start();
                break;
        }
    }

    @Override
    protected void getBundle(@NotNull Intent intent) {
        mAgency = intent.getParcelableExtra("agency");
    }

    @Override
    protected void initViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setOnClickListener(v -> ((NestedScrollView) findViewById(R.id.content)).smoothScrollTo(0, 0));
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> {
            if (BuildUtility.isRequired(Build.VERSION_CODES.LOLLIPOP)) finishAfterTransition();
            else finish();
        });

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mCoverImage = (ImageView) findViewById(R.id.app_bar_image);

        mFab = (FloatingActionButton) findViewById(R.id.fab);

        mTitleText = (TextView) findViewById(R.id.title);
        mVerifyView = findViewById(R.id.verify);
        mRatingBar = (RatingBar) findViewById(R.id.rating);
        mTeacherRatingText = (TextView) findViewById(R.id.rating_teacher);
        mQualityRatingText = (TextView) findViewById(R.id.rating_lesson);
        mEnvironmentRatingText = (TextView) findViewById(R.id.rating_environment);

        mTelephoneLayout = findViewById(R.id.telephones);
        mAddressLayout = findViewById(R.id.address);
        mLessonsLayout = findViewById(R.id.lesson);
        mIntroLayout = findViewById(R.id.intro);
        mEnvironmentLayout = findViewById(R.id.environment);
        mCommentLayout = findViewById(R.id.comment);

        RecyclerView mTelephoneListView = (RecyclerView) findViewById(R.id.telephones_listView);
        mTelephoneListView.setNestedScrollingEnabled(false);
        mTelephoneListView.setHasFixedSize(true);
        mTelephoneListView.setFocusable(false);
        mTelephoneListView.setItemAnimator(new DefaultItemAnimator());
        LinearLayoutManager telephoneManager = new LinearLayoutManager(mContext);
        telephoneManager.setAutoMeasureEnabled(true);
        mTelephoneListView.setLayoutManager(telephoneManager);
        mTelephoneAdapter = new TelephoneAdapter(mContext, mTelephones);
        mTelephoneListView.setAdapter(mTelephoneAdapter);

        mAddressText = (TextView) findViewById(R.id.address_text);
        mIntroText = (HtmlSupportTextView) findViewById(R.id.intro_text);
        mExpandButton = (CheckedTextView) findViewById(R.id.expand);

        invalidateUI();
    }

    private void invalidateUI() {
        if (mAgency != null) {
            if (!TextUtils.isEmpty(mAgency.schoolName)) mTitleText.setText(mAgency.schoolName);
            else mTitleText.setText(R.string.unknown);

            mVerifyView.setVisibility(mAgency.isCooperate == 1 ? View.VISIBLE : View.INVISIBLE);
            mRatingBar.setRating(mAgency.overallComment);

            if (!TextUtils.isEmpty(mAgency.address)) {
                mAddressText.setText(mAgency.address);
                mAddressLayout.setVisibility(View.VISIBLE);
            } else mAddressLayout.setVisibility(View.GONE);
        } else {
            mTitleText.setText(R.string.unknown);
            mVerifyView.setVisibility(View.INVISIBLE);
            mCoverImage.setVisibility(View.INVISIBLE);
            mRatingBar.setRating(0);

            mAddressLayout.setVisibility(View.GONE);
        }
    }

    private void invalidateUIDetail() {
        if (mAgency != null) {
            mTeacherRatingText.setText(String.format(
                    getString(R.string.nlg_agency_rating_teacher), String.valueOf(mAgency.teacherLevel)));
            mQualityRatingText.setText(String.format(
                    getString(R.string.nlg_agency_rating_teacher), String.valueOf(mAgency.teachQuality)));
            mEnvironmentRatingText.setText(String.format(
                    getString(R.string.nlg_agency_rating_teacher), String.valueOf(mAgency.educate)));

            if (!TextUtils.isEmpty(mAgency.customerPhone)) {
                mTelephones.addAll(Arrays.asList(mAgency.customerPhone.split(",")));
                mTelephoneAdapter.notifyDataSetChanged();
                mTelephoneLayout.setVisibility(View.VISIBLE);
            } else mTelephoneLayout.setVisibility(View.GONE);

            if (!TextUtils.isEmpty(mAgency.schoolProfile)) {
                mIntroText.setRichText(SecurityUtility.decodeString(mAgency.schoolProfile));
                mIntroLayout.setVisibility(View.VISIBLE);
            } else mIntroLayout.setVisibility(View.GONE);
        } else {
            mTeacherRatingText.setText(String.format(
                    getString(R.string.nlg_agency_rating_teacher), getString(R.string.unknown)));
            mQualityRatingText.setText(String.format(
                    getString(R.string.nlg_agency_rating_teacher), getString(R.string.unknown)));
            mEnvironmentRatingText.setText(String.format(
                    getString(R.string.nlg_agency_rating_teacher), getString(R.string.unknown)));

            mTelephoneLayout.setVisibility(View.GONE);
            mIntroLayout.setVisibility(View.GONE);
        }
    }

    @Override
    protected void bindListeners() {
        mTelephoneAdapter.setOnItemClickListener((v, telephone) -> {
            tempTelephone = telephone;

            ActivityCompat.requestPermissions(AgencyActivity.this, new String[]{
                    Manifest.permission.CALL_PHONE
            }, PERMISSION_REQUEST_CODE);
        });

        mAddressLayout.setOnClickListener(v -> {
            // TODO:
        });

        mLessonsLayout.setOnClickListener(v -> {
            // TODO:
        });

        mIntroLayout.setOnClickListener(v -> {
            boolean expand = mExpandButton.isChecked();

            if (expand) mIntroText.setMaxLines(3);
            else mIntroText.setMaxLines(Integer.MAX_VALUE);

            mExpandButton.toggle();
        });

        mCommentLayout.setOnClickListener(v -> {
            // TODO:
        });
    }

    private void requestCover() {
        if (mAgency != null) {
            try {
                final ImageRequest request = ImageRequest.fromUri(Uri.parse(mAgency.schoolImagePath));

                final BaseBitmapDataSubscriber subscriber = new BaseBitmapDataSubscriber() {
                    @Override
                    protected void onNewResultImpl(Bitmap bitmap) {
                        handler.post(() -> {
                            if (bitmap != null && !bitmap.isRecycled())
                                mCoverImage.setImageBitmap(bitmap.copy(bitmap.getConfig(), false));
                            else mCoverImage.setVisibility(View.INVISIBLE);
                        });

                        handler.sendEmptyMessage(1);
                    }

                    @Override
                    protected void onFailureImpl(DataSource<CloseableReference<CloseableImage>> dataSource) {
                        handler.post(() -> mCoverImage.setVisibility(View.INVISIBLE));
                        handler.sendEmptyMessage(1);
                    }
                };

                if (Fresco.getImagePipeline().isInBitmapMemoryCache(request.getSourceUri()))
                    Fresco.getImagePipeline()
                            .fetchImageFromBitmapCache(request, mContext)
                            .subscribe(subscriber, CallerThreadExecutor.getInstance());
                else
                    Fresco.getImagePipeline()
                            .fetchDecodedImage(request, mContext)
                            .subscribe(subscriber, CallerThreadExecutor.getInstance());
            } catch (Exception e) {
                handler.post(() -> mCoverImage.setVisibility(View.INVISIBLE));
                handler.sendEmptyMessage(1);
            }
        } else {
            handler.post(() -> mCoverImage.setVisibility(View.INVISIBLE));
            handler.sendEmptyMessage(1);
        }
    }

    private void requestDetail() {
        if (mAgency == null || TextUtils.isEmpty(mAgency.schoolId)) return;

        Map<String, String> paras = new HashMap<>();
        paras.put("CMD", CmdConst.CMD_AGENCY_DETAIL);
        paras.put("trainSchoolId", mAgency.schoolId);
        paras.put("serviceType", "5");
        subscriber = BaseHttpUrlRequests.getInstance().getAgencyDetail(paras, new Subscriber<AgencyDetailResp>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                toast(RetrofitConfig.handleReqError(e));
            }

            @Override
            public void onNext(AgencyDetailResp agencyDetailResp) {
                if (!RetrofitConfig.handleResp(agencyDetailResp, mContext))
                    toast(agencyDetailResp.message);
                else {
                    if (agencyDetailResp.school != null) {
                        if (mAgency != null) mAgency = mAgency.merge(agencyDetailResp.school);
                        else mAgency = agencyDetailResp.school;
                    }

                    mComments.addAll(agencyDetailResp.commentList);
                    mCourses.addAll(agencyDetailResp.courseList);

                    invalidateUIDetail();
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
                                    .setPositiveButton(R.string.ok, (dialog, which) -> {
                                        Intent intent = new Intent(Intent.ACTION_CALL);
                                        intent.setData(Uri.parse("tel:" + tempTelephone));
                                        startActivity(intent);
                                    })
                                    .setNegativeButton(R.string.cancel, null)
                                    .create();
                        serviceDialog.setMessage(tempTelephone);
                        serviceDialog.show();
                    } else toast(R.string.permission_deny);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Drawable drawable = mCoverImage.getDrawable();
        if (drawable instanceof BitmapDrawable) {
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            if (bitmap != null && !bitmap.isRecycled()) bitmap.recycle();
        }

        if (subscriber != null && !subscriber.isUnsubscribed()) subscriber.unsubscribe();

        mIntroText.recycle();
    }

    @Override
    public void toast(int msgRes) {
        Snackbar.make(mFab, msgRes, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void toast(String message) {
        Snackbar.make(mFab, message, Snackbar.LENGTH_LONG).show();
    }
}
