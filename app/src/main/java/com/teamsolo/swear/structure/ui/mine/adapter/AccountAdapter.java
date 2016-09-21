package com.teamsolo.swear.structure.ui.mine.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.teamsolo.swear.R;
import com.teamsolo.swear.foundation.bean.Relationship;
import com.teamsolo.swear.structure.util.UserHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * description: account adapter
 * author: Melody
 * date: 2016/9/20
 * version: 0.0.0.1
 */
@SuppressWarnings("WeakerAccess, unused")
public class AccountAdapter extends RecyclerView.Adapter<AccountAdapter.ViewHolder> {

    private Context mContext;

    private List<Relationship> mList;

    private LayoutInflater mInflater;

    private OnClickListener listener, buttonListener;

    private boolean isMain;

    public AccountAdapter(Context context, List<Relationship> relationships) {
        mContext = context;
        mList = relationships;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getItemViewType(int position) {
        Relationship item = getItem(position);
        if (item == null) return -1;
        return item.type;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == -1)
            return new ViewHolder(mInflater.inflate(R.layout.item_loading, parent, false), viewType);
        if (viewType == 0)
            return new ViewHolder(mInflater.inflate(R.layout.item_date, parent, false), viewType);
        return new ViewHolder(mInflater.inflate(R.layout.item_account, parent, false), viewType);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Relationship item = getItem(position);
        int viewType = getItemViewType(position);

        if (viewType == -1) return;

        if (viewType == 0) {
            if (item != null) holder.dateText.setText(item.parentName);
            else holder.dateText.setText(R.string.unknown);
        } else {
            long userId = UserHelper.getUserId(mContext);

            if (item != null) {
                if (!TextUtils.isEmpty(item.parentName))
                    holder.nameText.setText(Html.fromHtml(userId != item.parentsId
                            ? item.parentName
                            : "<font color='#3F51B5'>" + item.parentName + "</font>" + mContext.getString(R.string.accounts_me)));
                else holder.nameText.setText(R.string.unknown);

                if (!TextUtils.isEmpty(item.appellation))
                    holder.appellationText.setText(item.appellation);
                else holder.appellationText.setText(R.string.unknown);

                if (!TextUtils.isEmpty(item.parentPhone))
                    holder.phoneText.setText(item.parentPhone);
                else holder.phoneText.setText(R.string.unknown);

                if (item.isPay == 1) {
                    holder.purchaseButton.setText(R.string.accounts_purchase_2);
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
                    holder.hintText.setText(String.format(
                            mContext.getString(R.string.accounts_hint_2),
                            format.format(new Date(item.expireTime))));
                } else {
                    holder.purchaseButton.setText(R.string.accounts_purchase);
                    holder.hintText.setText(R.string.accounts_hint);
                }

                if (item.isMain == 1) {
                    holder.purchaseButton.setText(R.string.accounts_purchase_3);
                    holder.hintText.setText(R.string.accounts_hint_3);
                }

                if (item.isMain == 1)
                    this.isMain = userId == item.parentsId;

                holder.bottomLayout.setVisibility(this.isMain || item.isMain == 1 ? View.VISIBLE : View.GONE);

                holder.purchaseButton.setOnClickListener(v -> {
                    if (buttonListener != null) buttonListener.onClick(v, item);
                });
            } else {
                holder.nameText.setText(R.string.unknown);
                holder.appellationText.setText(R.string.unknown);
                holder.phoneText.setText(R.string.unknown);
                holder.purchaseButton.setText(R.string.accounts_purchase);
                holder.hintText.setText(R.string.accounts_hint);
                holder.bottomLayout.setVisibility(View.GONE);
                holder.purchaseButton.setOnClickListener(null);
            }
        }

        if (item != null)
            holder.itemView.setOnClickListener(v -> {
                if (listener != null && (isMain || item.isMain == 1)) listener.onClick(v, item);
            });
        else holder.itemView.setOnClickListener(null);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public Relationship getItem(int position) {
        if (position < mList.size()) return mList.get(position);
        return null;
    }

    public void setOnClickListener(OnClickListener listener) {
        this.listener = listener;
    }

    public void setOnButtonClickListener(OnClickListener listener) {
        this.buttonListener = listener;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView dateText;

        TextView nameText, appellationText, phoneText;

        View bottomLayout;

        TextView hintText, purchaseButton;

        ViewHolder(View itemView, int viewType) {
            super(itemView);

            if (viewType == 0) dateText = (TextView) itemView.findViewById(R.id.title);
            else if (viewType != -1) {
                nameText = (TextView) itemView.findViewById(R.id.name);
                appellationText = (TextView) itemView.findViewById(R.id.appellation);
                phoneText = (TextView) itemView.findViewById(R.id.phone);
                bottomLayout = itemView.findViewById(R.id.bottom);
                hintText = (TextView) itemView.findViewById(R.id.hint);
                purchaseButton = (TextView) itemView.findViewById(R.id.purchase);
            }
        }
    }

    public interface OnClickListener {
        void onClick(View view, Relationship relationship);
    }
}
