package com.teamsolo.swear.structure.ui.mine.adapter;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.teamsolo.base.util.BuildUtility;
import com.teamsolo.swear.R;
import com.teamsolo.swear.foundation.bean.Child;

import java.util.List;

/**
 * description: child adapter
 * author: Melody
 * date: 2016/9/2
 * version: 0.0.0.1
 */
@SuppressWarnings("WeakerAccess, unused")
public class ChildAdapter extends RecyclerView.Adapter<ChildAdapter.ViewHolder> {

    private Context mContext;

    private List<Child> mList;

    private LayoutInflater mInflater;

    private OnItemClickListener mItemListener;

    public ChildAdapter(Context context, List<Child> children) {
        mContext = context;
        mList = children;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(mInflater.inflate(R.layout.item_child_choose, parent, false));
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onBindViewHolder(ViewHolder holder, int position) {
        final int mPosition = holder.getAdapterPosition();
        final Child item = getItem(mPosition);

        if (item != null) {
            StringBuilder builder = new StringBuilder();
            if (item.isChecked) builder.append("<font color='#E91E63'>");

            if (!TextUtils.isEmpty(item.studentName)) builder.append(item.studentName);
            else builder.append(mContext.getString(R.string.unknown));

            if (!TextUtils.isEmpty(item.appellation))
                builder.append("(").append(item.appellation).append(")");

            if (item.isChecked) builder.append("<font/>");

            if (BuildUtility.isRequired(Build.VERSION_CODES.N))
                holder.nameText.setText(Html.fromHtml(builder.toString(), Html.FROM_HTML_MODE_COMPACT));
            else
                holder.nameText.setText(Html.fromHtml(builder.toString()));

            try {
                holder.portraitImage.setImageURI(Uri.parse(item.portraitPath));
            } catch (Exception e) {
                holder.portraitImage.setImageURI(Uri.parse("http://error"));
            }

            holder.itemView.setOnClickListener(v -> {
                if (mItemListener != null) mItemListener.onClick(v, item);
            });
        } else {
            holder.nameText.setText(R.string.unknown);
            holder.portraitImage.setImageURI(Uri.parse("http://error"));
            holder.itemView.setOnClickListener(null);
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public Child getItem(int position) {
        if (position < mList.size()) return mList.get(position);
        return null;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mItemListener = listener;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView nameText;

        private SimpleDraweeView portraitImage;

        ViewHolder(View itemView) {
            super(itemView);

            nameText = (TextView) itemView.findViewById(R.id.name);
            portraitImage = (SimpleDraweeView) itemView.findViewById(R.id.portrait);
        }
    }

    public interface OnItemClickListener {
        void onClick(View v, Child item);
    }
}
