package com.teamsolo.swear.structure.ui.training.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.teamsolo.swear.R;
import com.teamsolo.swear.foundation.bean.Agency;
import com.teamsolo.swear.foundation.ui.Appendable;

import java.util.List;

/**
 * description: agency adapter
 * author: Melody
 * date: 2016/9/12
 * version: 0.0.0.1
 */
@SuppressWarnings("WeakerAccess")
public class AgencyAdapter extends RecyclerView.Adapter<AgencyAdapter.ViewHolder> {

    private static int aimHeight = -1;

    private Context mContext;

    private List<Agency> mList;

    private LayoutInflater mInflater;

    private OnItemClickListener onItemClickListener;

    public AgencyAdapter(Context context, List<Agency> agencies) {
        mContext = context;
        mList = agencies;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getItemViewType(int position) {
        Agency item = getItem(position);
        if (item == null) return -1;
        return super.getItemViewType(position);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == -1)
            return new ViewHolder(mInflater.inflate(R.layout.item_loading, parent, false), viewType);
        return new ViewHolder(mInflater.inflate(R.layout.item_training_agency, parent, false), viewType);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onBindViewHolder(ViewHolder holder, int position) {
        int type = getItemViewType(position);

        if (type == -1) {
            if (mContext instanceof Appendable) ((Appendable) mContext).append(null);
            return;
        }

        final Agency item = getItem(holder.getAdapterPosition());

        if (item != null) {
            try {
                holder.coverImage.setImageURI(Uri.parse(item.schoolImagePath));
            } catch (Exception e) {
                holder.coverImage.setImageURI(Uri.parse("http://error"));
            }

            holder.titleText.setText(!TextUtils.isEmpty(item.schoolName) ? item.schoolName : mContext.getString(R.string.unknown));
            Drawable drawable = mContext.getResources().getDrawable(R.drawable.ic_verified_user_accent_18dp);
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            holder.titleText.setCompoundDrawables(null, null, item.isCooperate == 1 ? drawable : null, null);
            holder.addressText.setText(!TextUtils.isEmpty(item.address) ? item.address : mContext.getString(R.string.unknown));

            holder.itemView.setOnClickListener(v -> {
                if (onItemClickListener != null) onItemClickListener.onClick(v, item);
            });
        } else {
            holder.coverImage.setImageURI(Uri.parse("http://error"));
            holder.titleText.setText(R.string.unknown);
            holder.titleText.setCompoundDrawables(null, null, null, null);
            holder.addressText.setText(R.string.unknown);
            holder.itemView.setOnClickListener(null);
        }

        if (aimHeight < 0)
            holder.coverImage.post(() -> {
                int width = holder.coverImage.getMeasuredWidth();
                aimHeight = (int) (width * 360.0 / 492);
                ViewGroup.LayoutParams params = holder.coverImage.getLayoutParams();
                if (params.height != aimHeight) {
                    params.height = aimHeight;
                    holder.coverImage.setLayoutParams(params);
                }
            });
        else {
            ViewGroup.LayoutParams params = holder.coverImage.getLayoutParams();
            if (params.height != aimHeight) {
                params.height = aimHeight;
                holder.coverImage.setLayoutParams(params);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public Agency getItem(int position) {
        if (position < mList.size()) return mList.get(position);
        return null;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        SimpleDraweeView coverImage;

        TextView titleText, addressText;

        ViewHolder(View itemView, int viewType) {
            super(itemView);

            if (viewType != -1) {
                coverImage = (SimpleDraweeView) itemView.findViewById(R.id.cover);
                titleText = (TextView) itemView.findViewById(R.id.title);
                addressText = (TextView) itemView.findViewById(R.id.address);
            }
        }
    }

    public interface OnItemClickListener {
        void onClick(View view, Agency agency);
    }
}
