package com.teamsolo.swear.structure.ui.konwledge.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.teamsolo.base.util.DisplayUtility;
import com.teamsolo.swear.R;
import com.teamsolo.swear.foundation.bean.KnowledgeNews;
import com.teamsolo.swear.foundation.ui.Appendable;

import java.util.List;

/**
 * description: knowledge news adapter
 * author: Melody
 * date: 2016/9/14
 * version: 0.0.0.1
 */
@SuppressWarnings("WeakerAccess, unused")
public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {

    private Context mContext;

    private List<KnowledgeNews> mList;

    private LayoutInflater mInflater;

    private OnItemClickListener onItemClickListener;

    public NewsAdapter(Context context, List<KnowledgeNews> news) {
        mContext = context;
        mList = news;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getItemViewType(int position) {
        KnowledgeNews item = getItem(position);
        if (item == null) return -1;
        else return super.getItemViewType(position);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == -1)
            return new ViewHolder(mInflater.inflate(R.layout.item_loading, parent, false), viewType);
        return new ViewHolder(mInflater.inflate(R.layout.item_nlg_news, parent, false), viewType);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final int mPosition = holder.getAdapterPosition();

        int viewType = getItemViewType(position);

        if (viewType == -1) {
            if (mContext instanceof Appendable) ((Appendable) mContext).append(null);
            return;
        }

        final KnowledgeNews item = getItem(mPosition);

        if (item != null) {
            if (!TextUtils.isEmpty(item.title)) holder.titleText.setText(item.title);
            else holder.titleText.setText(R.string.unknown);

            holder.authorText.setText(String.format(mContext.getString(R.string.news_author), item.author));
            holder.browseText.setText(item.browseNumber <= 99999 ? String.valueOf(item.browseNumber) : "99999+");
            holder.commentText.setText(item.commentNumber <= 99999 ? String.valueOf(item.commentNumber) : "99999+");

            String[] indexes;

            if (TextUtils.isEmpty(item.keyword)) indexes = null;
            else indexes = item.keyword.split(",");

            if (indexes == null || indexes.length == 0)
                holder.tagContainer.setVisibility(View.GONE);
            else {
                holder.tagContainer.removeAllViews();
                holder.tagContainer.setVisibility(View.VISIBLE);

                DisplayMetrics metrics = DisplayUtility.getDisplayMetrics();
                if (metrics != null) {
                    int totalLength = 0;

                    for (String tag :
                            indexes) {
                        TextView tagView = new TextView(mContext);

                        tagView.setText(tag);
                        tagView.setTextColor(Color.parseColor("#FFFFFF"));
                        tagView.setSingleLine(true);
                        tagView.setEllipsize(TextUtils.TruncateAt.END);
                        tagView.setBackgroundResource(tag.length() < 4
                                ? R.drawable.shape_bg_tag_primary
                                : R.drawable.shape_bg_tag_accent);
                        tagView.setGravity(Gravity.CENTER);
                        tagView.setAlpha(0.7f);
                        tagView.setPadding(18, 2, 18, 3);

                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT);
                        params.setMargins(0, 0, DisplayUtility.getPxFromDp(5), 0);

                        tagView.measure(DisplayUtility.widthMeasureSpec, DisplayUtility.heightMeasureSpec);
                        totalLength += tagView.getMeasuredWidth();


                        if (totalLength + DisplayUtility.getPxFromDp(20) + 50 >= metrics.widthPixels)
                            break;

                        holder.tagContainer.addView(tagView, params);
                    }
                }
            }

            holder.itemView.setOnClickListener(v -> {
                if (onItemClickListener != null) onItemClickListener.onClick(v, item);
            });
        } else {
            holder.titleText.setText(R.string.unknown);
            holder.authorText.setText(String.format(mContext.getString(R.string.news_author), mContext.getString(R.string.unknown)));
            holder.tagContainer.setVisibility(View.GONE);
            holder.browseText.setText("0");
            holder.commentText.setText("0");
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public KnowledgeNews getItem(int position) {
        if (position < mList.size()) return mList.get(position);
        return null;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        onItemClickListener = listener;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView titleText;

        LinearLayout tagContainer;

        TextView authorText, browseText, commentText;

        ViewHolder(View itemView, int viewType) {
            super(itemView);

            if (viewType != -1) {
                titleText = (TextView) itemView.findViewById(R.id.title);
                tagContainer = (LinearLayout) itemView.findViewById(R.id.tags);
                authorText = (TextView) itemView.findViewById(R.id.author);
                browseText = (TextView) itemView.findViewById(R.id.browse);
                commentText = (TextView) itemView.findViewById(R.id.comment);
            }
        }
    }

    public interface OnItemClickListener {
        void onClick(View view, KnowledgeNews news);
    }
}
