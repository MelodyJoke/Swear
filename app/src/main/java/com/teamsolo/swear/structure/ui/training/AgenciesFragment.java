package com.teamsolo.swear.structure.ui.training;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.teamsolo.base.template.fragment.BaseFragment;
import com.teamsolo.swear.R;
import com.teamsolo.swear.foundation.bean.Agency;
import com.teamsolo.swear.foundation.bean.Classify;
import com.teamsolo.swear.foundation.bean.resp.AgenciesResp;
import com.teamsolo.swear.foundation.constant.CmdConst;
import com.teamsolo.swear.foundation.ui.Refreshable;
import com.teamsolo.swear.foundation.util.RetrofitConfig;
import com.teamsolo.swear.structure.request.BaseHttpUrlRequests;
import com.teamsolo.swear.structure.ui.training.adapter.AgencyAdapter;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Subscriber;

/**
 * description: agency list fragment
 * author: Melody
 * date: 2016/9/12
 * version: 0.0.0.1
 */
public class AgenciesFragment extends BaseFragment implements Refreshable {

    private AgencyAdapter mAdapter;

    private List<Agency> mList = new ArrayList<>();

    private Classify mClassify;

    private Subscriber<AgenciesResp> subscriber;

    private boolean isRefreshing;

    public static AgenciesFragment newInstance(Classify classify) {
        AgenciesFragment fragment = new AgenciesFragment();
        Bundle args = new Bundle();
        args.putParcelable("classify", classify);
        fragment.setArguments(args);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setContentView(inflater, R.layout.fragment_recycler_view_simple, container);
        initViews();
        bindListeners();
        new Thread(this::request).start();

        return mLayoutView;
    }

    @Override
    protected void getBundle(@NotNull Bundle bundle) {
        mClassify = bundle.getParcelable("classify");
    }

    @Override
    protected void initViews() {
        RecyclerView mGridView = (RecyclerView) findViewById(R.id.gridView);
        mGridView.setHasFixedSize(true);
        mGridView.setNestedScrollingEnabled(false);
        GridLayoutManager manager = new GridLayoutManager(mContext, 2);
        manager.setAutoMeasureEnabled(true);
        mGridView.setLayoutManager(manager);
        mGridView.setItemAnimator(new DefaultItemAnimator());
        mGridView.setFocusable(false);
        mGridView.setTag(this);

        mList.add(null);
        mAdapter = new AgencyAdapter(mContext, mList);
        mGridView.setAdapter(mAdapter);
    }

    @Override
    protected void bindListeners() {
        mAdapter.setOnItemClickListener((view, agency) -> {
            Intent intent = new Intent(mContext, AgencyActivity.class);
            intent.putExtra("agency", agency);
            startActivity(intent);
        });
    }

    private void request() {
        if (isRefreshing) return;

        isRefreshing = true;

        Map<String, String> paras = new HashMap<>();
        paras.put("CMD", CmdConst.CMD_GET_AGENCIES);
        paras.put("serviceType", "5");
        paras.put("rows", "10");
        if (mClassify != null && mClassify.classificationType != 2 && mClassify.classificationId > 0)
            paras.put("classificationId", String.valueOf(mClassify.classificationId));

        subscriber = BaseHttpUrlRequests.getInstance().getAgencies(paras, new Subscriber<AgenciesResp>() {
            @Override
            public void onCompleted() {
                isRefreshing = false;
            }

            @Override
            public void onError(Throwable e) {
                toast(RetrofitConfig.handleReqError(e));
                isRefreshing = false;
            }

            @Override
            public void onNext(AgenciesResp agenciesResp) {
                if (!RetrofitConfig.handleResp(agenciesResp, mContext))
                    toast(agenciesResp.message);
                else {
                    List<Agency> temp = agenciesResp.schoolList;

                    if (temp != null && !temp.isEmpty()) {
                        mList.clear();
                        mList.addAll(temp);
                        mAdapter.notifyDataSetChanged();
                    }
                }
            }
        });
    }

    @Override
    public void refresh(Uri uri) {
        new Thread(this::request).start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (subscriber != null && !subscriber.isUnsubscribed())
            subscriber.unsubscribe();
    }
}
