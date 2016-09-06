package com.teamsolo.swear.structure.ui.news.adapter;

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
import com.teamsolo.swear.foundation.bean.Comment;
import com.teamsolo.swear.foundation.ui.Appendable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * description: comment adapter
 * author: Melody
 * date: 2016/9/6
 * version: 0.0.0.1
 */
@SuppressWarnings("WeakerAccess, unused")
public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

    private Context mContext;

    private List<Comment> mList;

    private LayoutInflater mInflater;

    public CommentAdapter(Context context, List<Comment> comments) {
        mContext = context;
        mList = comments;
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
        else
            return new ViewHolder(mInflater.inflate(R.layout.item_comment, parent, false), viewType);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final int mPosition = holder.getAdapterPosition();
        final int viewType = getItemViewType(position);

        if (viewType == -1) {
            if (mContext instanceof Appendable) ((Appendable) mContext).append(null);
            return;
        }

        final Comment item = getItem(mPosition);
        if (item != null) {
            if (!TextUtils.isEmpty(item.portraitResourcePath)) {
                try {
                    holder.portraitImage.setImageURI(Uri.parse(item.portraitResourcePath));
                } catch (Exception e) {
                    holder.portraitImage.setImageURI(Uri.parse("http://error"));
                }
            } else holder.portraitImage.setImageURI(Uri.parse("http://error"));

            if (!TextUtils.isEmpty(item.replyName)) holder.nameText.setText(item.replyName);
            else holder.nameText.setText(R.string.unknown);

            if (item.createTimeStamp > 0) {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
                String date = format.format(new Date(item.createTimeStamp));

                if (!format.format(new Date()).equals(date)) {
                    if (!format.format(new Date(System.currentTimeMillis() - 60 * 60 * 24 * 1000)).equals(date))
                        holder.dateText.setText(date);
                    else holder.dateText.setText(R.string.yesterday);
                } else holder.dateText.setText(R.string.today);
            } else holder.dateText.setText(R.string.unknown);

            holder.commentText.setText(item.replyContent);
        } else {
            holder.portraitImage.setImageURI(Uri.parse("http://error"));
            holder.nameText.setText(R.string.unknown);
            holder.dateText.setText(R.string.unknown);
            holder.commentText.setText(R.string.unknown);
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public Comment getItem(int position) {
        if (position < mList.size()) return mList.get(position);
        return null;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        SimpleDraweeView portraitImage;

        TextView nameText, dateText, commentText;

        ViewHolder(View itemView, int viewType) {
            super(itemView);

            if (viewType != -1) {
                portraitImage = (SimpleDraweeView) itemView.findViewById(R.id.portrait);
                nameText = (TextView) itemView.findViewById(R.id.name);
                dateText = (TextView) itemView.findViewById(R.id.date);
                commentText = (TextView) itemView.findViewById(R.id.comment);
            }
        }
    }
}
