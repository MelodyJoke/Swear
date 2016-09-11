package com.teamsolo.swear.structure.ui.training.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.teamsolo.swear.R;
import com.teamsolo.swear.foundation.bean.Classify;

import java.util.List;

/**
 * description: training classify adapter
 * author: Melody
 * date: 2016/9/11
 * version: 0.0.0.1
 */
@SuppressWarnings("WeakerAccess, unused, FieldCanBeLocal")
public class ClassifyAdapter extends RecyclerView.Adapter<ClassifyAdapter.ViewHolder> {

    private Context mContext;

    private List<Classify> mList;

    private LayoutInflater mInflater;

    private OnItemClickListener onItemClickListener;

    public ClassifyAdapter(Context context, List<Classify> classifies) {
        mContext = context;
        mList = classifies;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(mInflater.inflate(R.layout.item_training_classify, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Classify item = getItem(position);

        if (item != null) {
            try {
                holder.coverImage.setImageURI(Uri.parse(item.resourcePath));
            } catch (Exception e) {
                holder.coverImage.setImageURI(Uri.parse("http://error"));
            }

            holder.titleText.setText(item.name);

            holder.itemView.setOnClickListener(v -> {
                if (onItemClickListener != null) onItemClickListener.onClick(v, item);
            });
        } else {
            holder.coverImage.setImageURI(Uri.parse("http://error"));
            holder.titleText.setText(R.string.unknown);
            holder.itemView.setOnClickListener(null);
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public Classify getItem(int position) {
        if (position < mList.size()) return mList.get(position);
        return null;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        SimpleDraweeView coverImage;

        TextView titleText;

        ViewHolder(View itemView) {
            super(itemView);

            coverImage = (SimpleDraweeView) itemView.findViewById(R.id.cover);
            titleText = (TextView) itemView.findViewById(R.id.title);
        }
    }

    public interface OnItemClickListener {
        void onClick(View view, Classify classify);
    }
}
