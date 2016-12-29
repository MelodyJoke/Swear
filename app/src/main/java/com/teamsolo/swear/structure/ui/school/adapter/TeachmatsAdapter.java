package com.teamsolo.swear.structure.ui.school.adapter;

import android.content.Context;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.teamsolo.swear.R;
import com.teamsolo.swear.foundation.bean.Teachmats;

import java.util.List;

/**
 * description Teaching materials of grade adapter
 * author Melo Chan
 * date 2016/12/29
 * version 0.0.0.1
 */
@SuppressWarnings("WeakerAccess, unused")
public class TeachmatsAdapter extends RecyclerView.Adapter<TeachmatsAdapter.ViewHolder> {

    private Context mContext;

    private List<Teachmats> mList;

    private LayoutInflater mInflater;

    private TeachmatAdapter.OnItemClickListener listener;

    public TeachmatsAdapter(Context context, List<Teachmats> teachmatsList, TeachmatAdapter.OnItemClickListener listener) {
        mContext = context;
        mList = teachmatsList;
        mInflater = LayoutInflater.from(context);
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(mInflater.inflate(R.layout.item_teachmats, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Teachmats item = getItem(holder.getAdapterPosition());

        if (item != null) {
            holder.titleText.setText(item.gradeName);
            TeachmatAdapter adapter = (TeachmatAdapter) holder.gridView.getAdapter();
            if (adapter != null) {
                adapter.setList(item.teachingMaterialsList);
                adapter.notifyDataSetChanged();
            }
        } else {
            holder.titleText.setText(R.string.unknown);
            TeachmatAdapter adapter = (TeachmatAdapter) holder.gridView.getAdapter();
            if (adapter != null) {
                adapter.setList(null);
                adapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public Teachmats getItem(int position) {
        if (position < mList.size()) return mList.get(position);
        return null;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView titleText;

        RecyclerView gridView;

        public ViewHolder(View itemView) {
            super(itemView);

            titleText = (TextView) itemView.findViewById(R.id.title);
            gridView = (RecyclerView) itemView.findViewById(R.id.gridView);
            gridView.setHasFixedSize(true);
            GridLayoutManager manager = new GridLayoutManager(mContext, 2);
            manager.setAutoMeasureEnabled(true);
            gridView.setLayoutManager(manager);
            gridView.setItemAnimator(new DefaultItemAnimator());
            TeachmatAdapter adapter = new TeachmatAdapter(mContext);
            adapter.setOnItemClickListener(listener);
            gridView.setAdapter(adapter);
            gridView.setNestedScrollingEnabled(false);
            gridView.setFocusable(false);
        }
    }
}
