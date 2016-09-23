package com.teamsolo.swear.structure.ui.mine.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;

import com.teamsolo.swear.R;
import com.teamsolo.swear.foundation.bean.Grade;

import java.util.List;

/**
 * description: grade adapter
 * author: Melody
 * date: 2016/9/24
 * version: 0.0.0.1
 */
@SuppressWarnings("WeakerAccess, unused, FieldCanBeLocal")
public class GradeAdapter extends RecyclerView.Adapter<GradeAdapter.ViewHolder> {

    private Context mContext;

    private List<Grade> mList;

    private LayoutInflater mInflater;

    private OnItemClickListener listener;

    public GradeAdapter(Context context, List<Grade> grades) {
        mContext = context;
        mList = grades;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(mInflater.inflate(R.layout.item_grade, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Grade item = getItem(position);

        if (item != null) {
            if (!TextUtils.isEmpty(item.gradeName)) holder.nameText.setText(item.gradeName);
            else holder.nameText.setText(R.string.unknown);

            holder.nameText.setChecked(item.isChecked);

            holder.itemView.setOnClickListener(v -> {
                if (listener != null) listener.onClick(v, item);
            });
        } else {
            holder.nameText.setText(R.string.unknown);
            holder.nameText.setChecked(false);
            holder.itemView.setOnClickListener(null);
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public Grade getItem(int position) {
        if (position < mList.size()) return mList.get(position);
        else return null;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        CheckedTextView nameText;

        ViewHolder(View itemView) {
            super(itemView);

            nameText = (CheckedTextView) itemView.findViewById(R.id.name);
        }
    }

    public interface OnItemClickListener {
        void onClick(View view, Grade grade);
    }
}
