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
import com.teamsolo.swear.foundation.bean.Teachmat;

import java.util.ArrayList;
import java.util.List;

/**
 * description Teaching material adapter
 * author Melo Chan
 * date 2016/12/29
 * version 0.0.0.1
 */
@SuppressWarnings("WeakerAccess, unused")
public class TeachmatAdapter extends RecyclerView.Adapter<TeachmatAdapter.ViewHolder> {

    private static int aimHeight = -1;

    private Context mContext;

    private List<Teachmat> mList = new ArrayList<>();

    private LayoutInflater mInflater;

    private OnItemClickListener listener;

    public TeachmatAdapter(Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(mInflater.inflate(R.layout.item_teachmat, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Teachmat item = getItem(holder.getAdapterPosition());

        if (item != null) {
            try {
                holder.coverImage.setImageURI(Uri.parse(item.coverImagePath));
            } catch (Exception e) {
                holder.coverImage.setImageURI(Uri.parse("http://error"));
            }
            holder.titleText.setText(!TextUtils.isEmpty(item.teachingMaterialsName) ? item.teachingMaterialsName : mContext.getString(R.string.unknown));
            holder.typeText.setText("(" + (!TextUtils.isEmpty(item.teachingMaterialsTypeName) ? item.teachingMaterialsTypeName : mContext.getString(R.string.unknown)) + ")");

            holder.itemView.setOnClickListener(v -> {
                if (listener != null) listener.onClick(v, item);
            });
        } else {
            holder.titleText.setText(R.string.unknown);
            holder.typeText.setText(R.string.unknown);
            holder.coverImage.setImageURI(Uri.parse("http://error"));
        }

        if (aimHeight < 0)
            holder.coverImage.post(() -> {
                int width = holder.coverImage.getMeasuredWidth();
                ViewGroup.LayoutParams params = holder.coverImage.getLayoutParams();
                aimHeight = (int) (width * 22.0 / 15);
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

    public Teachmat getItem(int position) {
        if (position < mList.size()) return mList.get(position);
        return null;
    }

    public void setList(List<Teachmat> teachmats) {
        if (!mList.isEmpty()) mList.clear();
        mList.addAll(teachmats);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        SimpleDraweeView coverImage;

        TextView titleText, typeText;

        public ViewHolder(View itemView) {
            super(itemView);

            coverImage = (SimpleDraweeView) itemView.findViewById(R.id.cover);
            titleText = (TextView) itemView.findViewById(R.id.title);
            typeText = (TextView) itemView.findViewById(R.id.type);
        }
    }

    public interface OnItemClickListener {
        void onClick(View view, Teachmat teachmat);
    }
}
