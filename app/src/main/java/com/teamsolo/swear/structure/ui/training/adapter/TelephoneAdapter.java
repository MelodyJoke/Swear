package com.teamsolo.swear.structure.ui.training.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.teamsolo.base.util.DisplayUtility;
import com.teamsolo.swear.R;

import java.util.List;

/**
 * description: agency telephone adapter
 * author: Melody
 * date: 2016/9/15
 * version: 0.0.0.1
 */
@SuppressWarnings("WeakerAccess, unused")
public class TelephoneAdapter extends RecyclerView.Adapter<TelephoneAdapter.ViewHolder> {

    private static final int paddingTop = DisplayUtility.getPxFromDp(2);

    private Context mContext;

    private List<String> mList;

    private OnItemClickListener listener;

    public TelephoneAdapter(Context context, List<String> telephones) {
        mContext = context;
        mList = telephones;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        TextView textView = new TextView(mContext);
        textView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return new ViewHolder(textView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final String item = getItem(holder.getAdapterPosition());

        holder.itemView.setPadding(0, paddingTop, 0, 0);

        if (!TextUtils.isEmpty(item)) holder.telephoneText.setText(item);
        else holder.telephoneText.setText(R.string.unknown);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onClick(v, item);
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public String getItem(int position) {
        if (position < mList.size()) return mList.get(position);
        else return null;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView telephoneText;

        ViewHolder(View itemView) {
            super(itemView);

            if (itemView instanceof TextView) telephoneText = (TextView) itemView;
        }
    }

    public interface OnItemClickListener {
        void onClick(View view, String telephone);
    }
}
