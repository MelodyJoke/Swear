package com.teamsolo.swear.structure.ui.mine.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.teamsolo.swear.R;
import com.teamsolo.swear.foundation.bean.Order;

import java.util.List;

/**
 * description: order adapter
 * author: Melody
 * date: 2016/8/31
 * version: 0.0.0.1
 */
@SuppressWarnings("WeakerAccess")
public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {

    private Context mContext;

    private List<Order> mList;

    public OrderAdapter(List<Order> orders) {
        mList = orders;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_order, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        int mPosition = holder.getAdapterPosition();
        final Order item = getItem(mPosition);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

    public Order getItem(int position) {
        if (mList.size() > position) return mList.get(position);
        return null;
    }
}
