package com.teamsolo.swear.structure.ui.school.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.teamsolo.swear.R;
import com.teamsolo.swear.foundation.bean.Category;

import java.util.List;

/**
 * description: training Category adapter
 * author: Melody
 * date: 2016/9/11
 * version: 0.0.0.1
 */
@SuppressWarnings("WeakerAccess, unused, FieldCanBeLocal")
public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    private Context mContext;

    private List<Category> mList;

    private LayoutInflater mInflater;

    private OnItemClickListener onItemClickListener;

    public CategoryAdapter(Context context, List<Category> classifies) {
        mContext = context;
        mList = classifies;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getItemViewType(int position) {
        if (getItem(position) == null) return -1;
        return super.getItemViewType(position);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == -1)
            return new ViewHolder(mInflater.inflate(R.layout.item_loading, parent, false), viewType);
        return new ViewHolder(mInflater.inflate(R.layout.item_training_classify, parent, false), viewType);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (getItemViewType(position) == -1) return;

        final Category item = getItem(holder.getAdapterPosition());

        if (item != null) {
            if (item.resourceId >= 0) holder.coverImage.setImageResource(item.resourceId);

            holder.titleText.setText(item.title);

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

    public Category getItem(int position) {
        if (position < mList.size()) return mList.get(position);
        return null;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView coverImage;

        TextView titleText;

        ViewHolder(View itemView, int viewType) {
            super(itemView);

            if (viewType != -1) {
                coverImage = (ImageView) itemView.findViewById(R.id.cover);
                titleText = (TextView) itemView.findViewById(R.id.title);
            }
        }
    }

    public interface OnItemClickListener {
        void onClick(View view, Category category);
    }
}
