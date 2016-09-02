package com.teamsolo.swear.structure.ui.mine;

import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.teamsolo.base.template.fragment.HandlerFragment;
import com.teamsolo.swear.R;
import com.teamsolo.swear.foundation.bean.Order;
import com.teamsolo.swear.foundation.bean.resp.OrdersResp;
import com.teamsolo.swear.foundation.constant.CmdConst;
import com.teamsolo.swear.foundation.ui.Appendable;
import com.teamsolo.swear.foundation.ui.Refreshable;
import com.teamsolo.swear.foundation.ui.ScrollAble;
import com.teamsolo.swear.foundation.util.RetrofitConfig;
import com.teamsolo.swear.structure.request.BaseHttpUrlRequests;
import com.teamsolo.swear.structure.ui.mine.adapter.OrderAdapter;
import com.teamsolo.swear.structure.util.LoadingUtil;
import com.teamsolo.swear.structure.util.UserHelper;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Subscriber;

/**
 * description: orders pager
 * author: Melody
 * date: 2016/8/31
 * version: 0.0.0.1
 */
public class OrdersFragment extends HandlerFragment implements Refreshable, Appendable, ScrollAble {

    private RecyclerView mListView;

    private OrderAdapter mAdapter;

    private List<Order> mList = new ArrayList<>();

    private int type;

    private int page = 1;

    private static final int pageSize = 10;

    private boolean append;

    private Subscriber<OrdersResp> subscriber;

    private LoadingUtil loadingUtil;

    public static OrdersFragment newInstance(int type) {
        OrdersFragment fragment = new OrdersFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("type", type);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setContentView(inflater, R.layout.fragment_orders, container);
        initViews();
        bindListeners();
        onInteraction(Uri.parse("refresh?start=true"));
        new Thread(this::request).start();

        return mLayoutView;
    }

    @Override
    protected void getBundle(@NotNull Bundle bundle) {
        type = bundle.getInt("type", 0);
    }

    @Override
    protected void initViews() {
        mListView = (RecyclerView) findViewById(R.id.listView);

        mListView.setHasFixedSize(true);
        mListView.setItemAnimator(new DefaultItemAnimator());
        mListView.setLayoutManager(new LinearLayoutManager(mContext));

        mAdapter = new OrderAdapter(mContext, mList);
        mListView.setAdapter(mAdapter);

        loadingUtil = new LoadingUtil(findViewById(R.id.loading), mListView)
                .init(R.mipmap.orders_empty, R.string.loading, R.string.orders_empty);
        loadingUtil.showLoading();
    }

    @Override
    protected void bindListeners() {
        mAdapter.setOnItemClickListener((v, item) -> {
            // TODO: show detail
            toast("item");
        });

        mAdapter.setOnCancelListener((v, item) -> {
            // TODO: cancel order
            toast("cancel");
        });

        mAdapter.setOnRefundListener((v, item) -> {
            // TODO: refund
            toast("refund");
        });

        mAdapter.setOnPayListener((v, item) -> {
            // TODO: pay
            toast("pay");
        });
    }

    private void request() {
        Map<String, String> paras = new HashMap<>();
        paras.put("CMD", CmdConst.CMD_GET_ORDERS);
        paras.put("userId", String.valueOf(UserHelper.getUserId(mContext)));
        paras.put("orderStatus", String.valueOf(type));
        paras.put("page", String.valueOf(page));
        paras.put("pageSize", String.valueOf(pageSize));
        subscriber = BaseHttpUrlRequests.getInstance().getOrders(paras, new Subscriber<OrdersResp>() {
            @Override
            public void onCompleted() {
                onInteraction(Uri.parse("refresh?ready=true"));
            }

            @Override
            public void onError(Throwable e) {
                toast(RetrofitConfig.handleReqError(e));
                onInteraction(Uri.parse("refresh?ready=true"));

                handler.postDelayed(() -> {
                    if (mList.size() == 0 || mList.size() == 1 && mList.get(0) == null)
                        loadingUtil.showEmpty();
                    else loadingUtil.dismiss();
                }, 500);
            }

            @Override
            public void onNext(OrdersResp ordersResp) {
                if (!RetrofitConfig.handleResp(ordersResp, mContext)) toast(ordersResp.message);
                else {
                    List<Order> temp = ordersResp.orderList;

                    if (!append) mList.clear();

                    int size = mList.size();
                    if (size > 0 && mList.get(size - 1) == null) mList.remove(size - 1);

                    mList.addAll(temp);

                    if (temp.size() >= pageSize) mList.add(null);
                    if (temp.size() == 0) {
                        page--;
                        if (page < 1) page = 1;
                    }

                    mAdapter.notifyDataSetChanged();

                    handler.postDelayed(() -> {
                        if (mList.size() == 0 || mList.size() == 1 && mList.get(0) == null)
                            loadingUtil.showEmpty();
                        else loadingUtil.dismiss();
                    }, 500);
                }
            }
        });

        handler.postDelayed(() -> onInteraction(Uri.parse("refresh?ready=true")), 2000);
    }

    @Override
    public void refresh(Uri uri) {
        append = false;
        page = 1;
        new Thread(this::request).start();
    }

    @Override
    public void append(Uri uri) {
        append = true;
        page++;
        handler.postDelayed(() -> new Thread(this::request).start(), 500);
    }

    @Override
    public void scroll(Uri uri) {
        if (uri.getBooleanQueryParameter("top", false))
            if (mList.size() > 0) mListView.scrollToPosition(0);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (subscriber != null && !subscriber.isUnsubscribed()) subscriber.unsubscribe();
    }

    @Override
    protected void handleMessage(HandlerFragment fragment, Message msg) {

    }
}
