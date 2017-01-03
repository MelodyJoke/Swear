package com.teamsolo.swear.structure.ui.school.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.teamsolo.swear.R;
import com.teamsolo.swear.foundation.bean.Unit;

import java.util.List;

/**
 * description Unit adapter
 * author Melo Chan
 * date 2016/12/30
 * version 0.0.0.1
 */
@SuppressWarnings("WeakerAccess, unused")
public class UnitAdapter extends RecyclerView.Adapter<UnitAdapter.ViewHolder> {

    private static int aimHeight = -1;

    private Context mContext;

    private List<Unit> mList;

    private LayoutInflater mInflater;

    public UnitAdapter(Context context, List<Unit> units) {
        mContext = context;
        mList = units;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(mInflater.inflate(R.layout.item_unit, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Unit item = getItem(holder.getAdapterPosition());

        if (item != null) {
            try {
                holder.coverImage.setImageURI(Uri.parse(item.coverImagePath));
            } catch (Exception e) {
                holder.coverImage.setImageURI(Uri.parse("http://error"));
            }

            holder.titleText.setText(TextUtils.isEmpty(item.unitName) ? mContext.getString(R.string.unknown) : item.unitName);
            holder.unitText.setText(TextUtils.isEmpty(item.unitNumber) ? mContext.getString(R.string.unknown) : "Unit " + item.unitNumber);

            String pageStr;
            if (!TextUtils.isEmpty(item.startPage) && !TextUtils.isEmpty(item.endPage)) {
                if (!TextUtils.equals(item.startPage, item.endPage))
                    pageStr = "P" + item.startPage + "-" + item.endPage;
                else pageStr = "P" + item.startPage;
            } else if (TextUtils.isEmpty(item.startPage) && TextUtils.isEmpty(item.endPage))
                pageStr = mContext.getString(R.string.unknown);
            else pageStr = "P" + item.startPage + item.endPage;

            holder.pageText.setText(pageStr);
        } else {
            holder.coverImage.setImageURI(Uri.parse("http://error"));
            holder.titleText.setText(R.string.unknown);
            holder.unitText.setText(R.string.unknown);
            holder.pageText.setText(R.string.unknown);
        }

        if (aimHeight < 0)
            holder.coverImage.post(() -> {
                int width = holder.coverImage.getMeasuredWidth();
                ViewGroup.LayoutParams params = holder.coverImage.getLayoutParams();
                aimHeight = (int) (width * 246.0 / 357);
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

    public Unit getItem(int position) {
        if (position < mList.size()) return mList.get(position);
        return null;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        SimpleDraweeView coverImage;

        TextView titleText, unitText, pageText;

        public ViewHolder(View itemView) {
            super(itemView);

            coverImage = (SimpleDraweeView) itemView.findViewById(R.id.cover);
            titleText = (TextView) itemView.findViewById(R.id.title);
            unitText = (TextView) itemView.findViewById(R.id.unit);
            pageText = (TextView) itemView.findViewById(R.id.page);
        }
    }
}
