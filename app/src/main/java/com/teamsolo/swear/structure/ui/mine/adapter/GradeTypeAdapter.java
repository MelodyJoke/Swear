package com.teamsolo.swear.structure.ui.mine.adapter;

import android.content.Context;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.teamsolo.swear.R;
import com.teamsolo.swear.foundation.bean.Grade;
import com.teamsolo.swear.foundation.bean.GradeType;

import java.util.ArrayList;
import java.util.List;

/**
 * description: grade type adapter
 * author: Melody
 * date: 2016/9/24
 * version: 0.0.0.1
 */
@SuppressWarnings("WeakerAccess, unused")
public class GradeTypeAdapter extends RecyclerView.Adapter<GradeTypeAdapter.ViewHolder> {

    private Context mContext;

    private List<GradeType> mList;

    private LayoutInflater mInflater;

    private GradeAdapter.OnItemClickListener listener;

    public GradeTypeAdapter(Context context, List<GradeType> gradeTypes) {
        mContext = context;
        mList = gradeTypes;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(mInflater.inflate(R.layout.item_grade_type, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final GradeType item = getItem(position);

        if (item != null) {
            if (!TextUtils.isEmpty(item.gradeTpyeName))
                holder.titleText.setText(item.gradeTpyeName);
            else holder.titleText.setText(R.string.unknown);

            if (item.grades != null && !item.grades.isEmpty()) {
                holder.list.clear();
                holder.list.addAll(item.grades);
                holder.adapter.notifyDataSetChanged();
            } else {
                holder.list.clear();
                holder.adapter.notifyDataSetChanged();
            }
        } else {
            holder.titleText.setText(R.string.unknown);
            holder.list.clear();
            holder.adapter.notifyDataSetChanged();
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public GradeType getItem(int position) {
        if (position < mList.size()) return mList.get(position);
        else return null;
    }

    public void setOnItemClickListener(GradeAdapter.OnItemClickListener listener) {
        this.listener = listener;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView titleText;

        RecyclerView gridView;

        GradeAdapter adapter;

        List<Grade> list = new ArrayList<>();

        ViewHolder(View itemView) {
            super(itemView);

            titleText = (TextView) itemView.findViewById(R.id.title);
            gridView = (RecyclerView) itemView.findViewById(R.id.gridView);
            gridView.setHasFixedSize(true);
            gridView.setItemAnimator(new DefaultItemAnimator());
            gridView.setNestedScrollingEnabled(false);
            GridLayoutManager manager = new GridLayoutManager(mContext, 4);
            manager.setAutoMeasureEnabled(true);
            gridView.setLayoutManager(manager);

            adapter = new GradeAdapter(mContext, list);
            adapter.setOnItemClickListener((view, grade) -> {
                if (listener != null) listener.onClick(view, grade);
            });
            gridView.setAdapter(adapter);
        }
    }
}
