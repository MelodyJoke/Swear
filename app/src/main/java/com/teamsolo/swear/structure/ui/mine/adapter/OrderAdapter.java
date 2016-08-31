package com.teamsolo.swear.structure.ui.mine.adapter;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.teamsolo.base.template.activity.BaseActivity;
import com.teamsolo.base.util.BuildUtility;
import com.teamsolo.swear.R;
import com.teamsolo.swear.foundation.bean.Order;
import com.teamsolo.swear.foundation.ui.Appendable;
import com.teamsolo.swear.structure.Application;

import java.util.List;

import static android.text.Html.FROM_HTML_MODE_LEGACY;

/**
 * description: order adapter
 * author: Melody
 * date: 2016/8/31
 * version: 0.0.0.1
 */
@SuppressWarnings("WeakerAccess")
public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {

    protected Context mContext;

    protected List<Order> mList;

    protected LayoutInflater mInflater;

    public OrderAdapter(Context context, List<Order> orders) {
        mContext = context;
        mList = orders;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getItemViewType(int position) {
        if (getItem(position) == null) return 1;
        return super.getItemViewType(position);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 1)
            return new ViewHolder(mInflater.inflate(R.layout.item_loading, parent, false), viewType);
        return new ViewHolder(mInflater.inflate(R.layout.item_order, parent, false), viewType);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onBindViewHolder(ViewHolder holder, int position) {
        final int mPosition = holder.getAdapterPosition();
        final int viewType = getItemViewType(position);

        if (viewType == 1) {
            if (mContext instanceof Appendable) ((Appendable) mContext).append(null);
            return;
        }

        final Order item = getItem(mPosition);
        if (item != null) {
            holder.noText.setText(String.format(mContext.getString(R.string.orders_no), item.orderNo));
            holder.statusText.setText(transStatus(item.orderStatus));

            try {
                holder.coverImage.setImageURI(Uri.parse(item.logoResource));
            } catch (Exception e) {
                holder.coverImage.setImageURI(Uri.parse("http://error"));
            }

            holder.titleText.setText(item.serviceName);

            if (item.orderType == 1)
                holder.countText.setText(
                        String.format(mContext.getString(R.string.orders_count), item.unitCount));
            else if (item.orderType == 5)
                holder.countText.setText(
                        String.format(mContext.getString(R.string.orders_count_2), item.unitCount));
            else holder.countText.setVisibility(View.GONE);

            if (item.orderType == 5) {
                if (item.payType == 1)
                    holder.priceText.setText(String.format(mContext.getString(R.string.orders_price_2), String.valueOf(item.originalPrice)));
                else if (item.payType == 2) {
                    if (BuildUtility.isRequired(Build.VERSION_CODES.N))
                        holder.priceText.setText(Html.fromHtml(mContext.getString(R.string.orders_price_3), FROM_HTML_MODE_LEGACY));
                    else
                        holder.priceText.setText(Html.fromHtml(mContext.getString(R.string.orders_price_3)));
                }
            } else
                holder.priceText.setText(String.format(mContext.getString(R.string.orders_price), String.valueOf(item.originalPrice)));

            holder.prePriceText.setVisibility(item.isShowPrePay == 1 ? View.VISIBLE : View.INVISIBLE);
            holder.prePriceText.setText(String.format(mContext.getString(R.string.orders_re_price), String.valueOf(item.servicePrice)));
            holder.cancelButton.setVisibility(item.isShowCancel == 1 ? View.VISIBLE : View.GONE);
            holder.refundButton.setVisibility(item.isShowRefund == 1 ? View.VISIBLE : View.GONE);
            holder.payButton.setVisibility(item.isShowPay == 1 ? View.VISIBLE : View.GONE);

            holder.bottomLayout.setVisibility((item.isShowPrePay == 1 || item.isShowCancel == 1 || item.isShowRefund == 1 || item.isShowPay == 1)
                    ? View.VISIBLE : View.GONE);

            // TODO: handle click event here
            holder.cancelButton.setOnClickListener(v -> {
                ((BaseActivity) mContext).toast("cancel");
            });

            holder.refundButton.setOnClickListener(v -> {
                ((BaseActivity) mContext).toast("refund");
            });

            holder.payButton.setOnClickListener(v -> {
                ((BaseActivity) mContext).toast("pay");
            });
        } else {
            holder.noText.setText(R.string.unknown);
            holder.statusText.setText(R.string.unknown);
            holder.titleText.setText(R.string.unknown);
            holder.coverImage.setImageURI(Uri.parse("http://error"));
            holder.countText.setText(R.string.unknown);
            holder.priceText.setText(R.string.unknown);
            holder.prePriceText.setText(R.string.unknown);
            holder.cancelButton.setText(R.string.unknown);
            holder.refundButton.setText(R.string.unknown);
            holder.payButton.setText(R.string.unknown);
            holder.bottomLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public Order getItem(int position) {
        if (mList.size() > position) return mList.get(position);
        return null;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView noText, statusText;

        SimpleDraweeView coverImage;

        TextView titleText, countText, priceText;

        View bottomLayout;

        TextView prePriceText, cancelButton, refundButton, payButton;

        public ViewHolder(View itemView, int viewType) {
            super(itemView);

            if (viewType != 1) {
                noText = (TextView) itemView.findViewById(R.id.no);
                statusText = (TextView) itemView.findViewById(R.id.status);

                coverImage = (SimpleDraweeView) itemView.findViewById(R.id.cover);

                titleText = (TextView) itemView.findViewById(R.id.title);
                countText = (TextView) itemView.findViewById(R.id.count);
                priceText = (TextView) itemView.findViewById(R.id.price);

                bottomLayout = itemView.findViewById(R.id.bottom);
                prePriceText = (TextView) itemView.findViewById(R.id.pre_price);
                cancelButton = (TextView) itemView.findViewById(R.id.cancel);
                refundButton = (TextView) itemView.findViewById(R.id.refund);
                payButton = (TextView) itemView.findViewById(R.id.pay);
            }
        }
    }

    @Nullable
    public static String transStatus(int statusCode) {
        Context context = Application.getInstanceContext();
        if (context == null) return null;

        String[] statusStrings = context.getResources().getStringArray(R.array.orders_status_array);
        if (statusCode < statusStrings.length) return statusStrings[statusCode];

        return context.getString(R.string.unknown);
    }
}
