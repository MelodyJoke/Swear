package com.teamsolo.swear.structure.ui.mine;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
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
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.view.View;

import com.teamsolo.base.bean.CommonResponse;
import com.teamsolo.base.template.activity.HandlerActivity;
import com.teamsolo.base.util.BuildUtility;
import com.teamsolo.swear.R;
import com.teamsolo.swear.foundation.bean.Relationship;
import com.teamsolo.swear.foundation.bean.resp.AccountsResp;
import com.teamsolo.swear.foundation.constant.CmdConst;
import com.teamsolo.swear.foundation.util.RetrofitConfig;
import com.teamsolo.swear.structure.request.BaseHttpUrlRequests;
import com.teamsolo.swear.structure.ui.mine.adapter.AccountAdapter;
import com.teamsolo.swear.structure.util.LoadingUtil;
import com.teamsolo.swear.structure.util.UserHelper;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Subscriber;

/**
 * description: accounts manage page
 * author: Melody
 * date: 2016/9/20
 * version: 0.0.0.1
 */
public class AccountsActivity extends HandlerActivity implements SwipeRefreshLayout.OnRefreshListener {

    private static final int ACCOUNT_EDIT_REQUEST_CODE = 233;

    private static final int PERMISSION_REQUEST_CODE = 167;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    private RecyclerView mListView;

    private FloatingActionButton mFab;

    private LoadingUtil loadingUtil;

    private AlertDialog serviceDialog;

    private AccountAdapter mAdapter;

    private List<Relationship> mList = new ArrayList<>();

    private Subscriber<AccountsResp> subscriberList;

    private Subscriber<CommonResponse> subscriberRemove;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accounts);

        getBundle(getIntent());
        initViews();
        bindListeners();

        new Thread(this::requestList).start();
    }

    @Override
    protected void getBundle(@NotNull Intent intent) {

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
        toolbar.setNavigationOnClickListener(v -> {
            if (BuildUtility.isRequired(Build.VERSION_CODES.LOLLIPOP)) finishAfterTransition();
            else finish();
        });

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe);
        mSwipeRefreshLayout.setColorSchemeColors(
                Color.parseColor("#F44336"),
                Color.parseColor("#FF5722"),
                Color.parseColor("#CDDC39"),
                Color.parseColor("#4CAF50"));
        mSwipeRefreshLayout.setRefreshing(true);

        loadingUtil = new LoadingUtil(findViewById(R.id.loading), mListView)
                .init(R.mipmap.accounts_empty, R.string.loading, R.string.accounts_empty);
        loadingUtil.showLoading();

        mAdapter = new AccountAdapter(mContext, mList);
        mListView.setAdapter(mAdapter);

        mFab = (FloatingActionButton) findViewById(R.id.fab);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(
                new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.RIGHT) {
                    @Override
                    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                        return false;
                    }

                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                        final int position = viewHolder.getAdapterPosition();
                        if (position < 0 || position >= mList.size() || !(viewHolder instanceof AccountAdapter.ViewHolder) || !mAdapter.isMain()) {
                            mAdapter.notifyItemChanged(position);
                            return;
                        }

                        AccountAdapter.ViewHolder holder = (AccountAdapter.ViewHolder) viewHolder;
                        if (holder.viewType == -1 || holder.viewType == 0) {
                            mAdapter.notifyItemChanged(position);
                            return;
                        }

                        final Relationship relationship = mList.get(position);
                        if (relationship.isMain == 1) {
                            toast(R.string.accounts_remove_error);
                            mAdapter.notifyItemChanged(position);
                            return;
                        }

                        new AlertDialog.Builder(mContext)
                                .setTitle(R.string.prompt)
                                .setMessage(String.format(getString(R.string.accounts_remove), relationship.parentName))
                                .setNegativeButton(R.string.cancel, (dialog, which) -> mAdapter.notifyItemChanged(position))
                                .setPositiveButton(R.string.ok, (dialog, which) -> new Thread(() -> requestRemove(position)).start())
                                .create()
                                .show();
                    }
                });

        itemTouchHelper.attachToRecyclerView(mListView);
    }

    @Override
    protected void bindListeners() {
        mSwipeRefreshLayout.setOnRefreshListener(this);

        mFab.setOnClickListener(v ->
                startActivityForResult(new Intent(mContext, AccountActivity.class), ACCOUNT_EDIT_REQUEST_CODE));

        mAdapter.setOnItemClickListener((view, relationship) -> {
            if (relationship.type == -1 || relationship.type == 0) return;

            if (relationship.isMain == 1)
                ActivityCompat.requestPermissions(AccountsActivity.this, new String[]{
                        Manifest.permission.CALL_PHONE
                }, PERMISSION_REQUEST_CODE);
            else {
                Intent intent = new Intent(mContext, AccountActivity.class);
                intent.putExtra("relationShip", relationship);
                intent.putExtra("isEdit", true);
                startActivityForResult(intent, ACCOUNT_EDIT_REQUEST_CODE);
            }
        });

        mAdapter.setOnButtonClickListener((view, relationship) -> {
            if (relationship.isMain == 1)
                ActivityCompat.requestPermissions(AccountsActivity.this, new String[]{
                        Manifest.permission.CALL_PHONE
                }, PERMISSION_REQUEST_CODE);
            else {
                // TODO: purchase member
                toast(relationship.parentName + " purchase");
            }
        });
    }

    private void requestList() {
        long studentId = UserHelper.getStudentId(mContext);
        if (studentId <= 0) {
            mList.clear();
            loadingUtil.showEmpty();
            mSwipeRefreshLayout.setRefreshing(false);
            return;
        }

        Map<String, String> paras = new HashMap<>();
        paras.put("CMD", CmdConst.CMD_GET_ACCOUNTS);
        paras.put("studentId", String.valueOf(studentId));
        subscriberList = BaseHttpUrlRequests.getInstance().getAccounts(paras, new Subscriber<AccountsResp>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                toast(RetrofitConfig.handleReqError(e));
                mList.clear();
                loadingUtil.showEmpty();
                mSwipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onNext(AccountsResp accountsResp) {
                if (!RetrofitConfig.handleResp(accountsResp, mContext)) {
                    toast(accountsResp.message);
                    mList.clear();
                    loadingUtil.showEmpty();
                    mSwipeRefreshLayout.setRefreshing(false);
                } else {
                    mList.clear();

                    List<Relationship> temp = accountsResp.stuParentRelaList;

                    if (temp != null && !temp.isEmpty()) {
                        for (Relationship relationship :
                                temp) {
                            relationship.type = 1;

                            if (relationship.isMain == 1) mList.add(0, relationship);
                            else mList.add(relationship);
                        }
                    }

                    Relationship titleCommon = new Relationship();
                    titleCommon.type = 0;
                    titleCommon.parentName = getString(R.string.accounts_common);

                    if (mList.size() > 0 && mList.get(0).isMain == 1) {
                        Relationship titleMain = new Relationship();
                        titleMain.type = 0;
                        titleMain.parentName = getString(R.string.accounts_main);
                        mList.add(0, titleMain);

                        if (mList.size() > 2) mList.add(2, titleCommon);
                    } else mList.add(0, titleCommon);

                    mAdapter.notifyDataSetChanged();

                    loadingUtil.dismiss();
                    mSwipeRefreshLayout.setRefreshing(false);

                    handler.postDelayed(() -> {
                        if (temp != null) {
                            int count = 0;
                            for (Relationship relationship :
                                    temp)
                                if (relationship.isMain == 0) count++;

                            mFab.setVisibility(count < 5 && mAdapter.isMain() ? View.VISIBLE : View.GONE);
                        } else mFab.setVisibility(View.GONE);
                    }, 500);
                }
            }
        });
    }

    private void requestRemove(int position) {
        final Relationship relationship = mList.get(position);
        toast(String.format(getString(R.string.accounts_removing), relationship.parentName));

        Map<String, String> paras = new HashMap<>();
        paras.put("CMD", CmdConst.CMD_DELETE_ACCOUNT);
        paras.put("parentsStudentId", String.valueOf(relationship.parentsStudentId));
        paras.put("parentsId", String.valueOf(relationship.parentsId));
        paras.put("studentId", String.valueOf(relationship.studentId));

        subscriberRemove = BaseHttpUrlRequests.getInstance().commonReq(paras, new Subscriber<CommonResponse>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                toast(RetrofitConfig.handleReqError(e));
                mAdapter.notifyItemChanged(position);
            }

            @Override
            public void onNext(CommonResponse commonResponse) {
                if (!RetrofitConfig.handleResp(commonResponse, mContext)) {
                    toast(commonResponse.message);
                    mAdapter.notifyItemChanged(position);
                } else {
                    mList.remove(position);
                    mAdapter.notifyItemRemoved(position);

                    int count = 0;
                    for (Relationship relationship :
                            mList)
                        if (relationship.type == 1 && relationship.isMain == 0) count++;

                    mFab.setVisibility(count < 5 && mAdapter.isMain() ? View.VISIBLE : View.GONE);

                    int removePosition = -1;
                    if (count == 0) {
                        for (int i = 0; i < mList.size(); i++) {
                            Relationship relationship = mList.get(i);
                            if (relationship.type == 0 && TextUtils.equals(relationship.parentName, getString(R.string.accounts_common)))
                                removePosition = i;
                        }

                        if (removePosition >= 0) {
                            mList.remove(removePosition);
                            mAdapter.notifyDataSetChanged();
                        }
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ACCOUNT_EDIT_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                mSwipeRefreshLayout.setRefreshing(true);
                new Thread(this::requestList).start();
            }
        }
    }

    @Override
    public void onRefresh() {
        new Thread(this::requestList).start();
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

        if (subscriberList != null && !subscriberList.isUnsubscribed())
            subscriberList.unsubscribe();
        if (subscriberRemove != null && !subscriberRemove.isUnsubscribed())
            subscriberRemove.unsubscribe();
    }
}
