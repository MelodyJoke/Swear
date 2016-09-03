package com.teamsolo.swear.structure.ui.news.adapter;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.teamsolo.base.util.DisplayUtility;
import com.teamsolo.swear.R;
import com.teamsolo.swear.foundation.bean.dummy.NewsDummy;
import com.teamsolo.swear.foundation.ui.Appendable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * description: news list adapter
 * author: Melody
 * date: 2016/9/3
 * version: 0.0.0.1
 */
@SuppressWarnings("WeakerAccess, unused")
public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {

    private Context mContext;

    private List<NewsDummy> mList;

    private LayoutInflater mInflater;

    private OnItemClickListener mItemListener;

    public NewsAdapter(Context context, List<NewsDummy> dummies) {
        mContext = context;
        mList = dummies;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getItemViewType(int position) {
        NewsDummy item = getItem(position);
        if (item == null) return -1;
        return item.type;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case -1:
                return new ViewHolder(mInflater.inflate(R.layout.item_loading, parent, false), viewType);

            case 0:
                return new ViewHolder(mInflater.inflate(R.layout.item_date, parent, false), viewType);

            case 1:
                return new ViewHolder(mInflater.inflate(R.layout.item_news_plain, parent, false), viewType);

            case 2:
                return new ViewHolder(mInflater.inflate(R.layout.item_news_single, parent, false), viewType);

            case 3:
                return new ViewHolder(mInflater.inflate(R.layout.item_news_giant, parent, false), viewType);

            case 4:
                return new ViewHolder(mInflater.inflate(R.layout.item_news_triple, parent, false), viewType);

            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final int mPosition = holder.getAdapterPosition();
        final int viewType = getItemViewType(position);
        final NewsDummy item = getItem(mPosition);

        if (viewType == -1) {
            if (mContext instanceof Appendable) ((Appendable) mContext).append(null);
            return;
        }

        if (item == null) return;

        if (viewType > -1) {
            if (!TextUtils.isEmpty(item.title)) {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
                if (!format.format(new Date()).equals(item.title))
                    holder.titleText.setText(item.title);
                else holder.titleText.setText(R.string.today);
            } else holder.titleText.setText(R.string.unknown);
        }

        if (viewType > 0) {
            holder.authorText.setText(String.format(mContext.getString(R.string.news_author), item.author));
            holder.browseText.setText(item.browserCount <= 99999 ? String.valueOf(item.browserCount) : "99999+");
            holder.commentText.setText(item.commentCount <= 99999 ? String.valueOf(item.commentCount) : "99999+");

            if (item.indexes == null || item.indexes.length == 0)
                holder.tagsContainer.setVisibility(View.GONE);
            else {
                holder.tagsContainer.removeAllViews();
                holder.tagsContainer.setVisibility(View.VISIBLE);

                DisplayMetrics metrics = DisplayUtility.getDisplayMetrics();
                if (metrics != null) {
                    int totalLength = 0;

                    for (String tag :
                            item.indexes) {
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

                        holder.tagsContainer.addView(tagView, params);
                    }
                }
            }
        }

        if (viewType > 1)
            try {
                if (item.pictures != null && item.pictures.length > 0)
                    holder.coverImage.setImageURI(Uri.parse(item.pictures[0]));
                else holder.coverImage.setImageURI(Uri.parse("http://error"));
            } catch (Exception e) {
                holder.coverImage.setImageURI(Uri.parse("http://error"));
            }

        if (viewType > 3) {
            if (item.pictures == null || item.pictures.length == 0)
                holder.coverLayout.setVisibility(View.GONE);
            else {
                int length = item.pictures.length;
                if (length > 0)
                    try {
                        holder.coverImage.setImageURI(Uri.parse(item.pictures[0]));
                    } catch (Exception e) {
                        holder.coverImage.setImageURI(Uri.parse("http://error"));
                    }

                if (length > 1)
                    try {
                        holder.coverImage2.setImageURI(Uri.parse(item.pictures[1]));
                    } catch (Exception e) {
                        holder.coverImage2.setImageURI(Uri.parse("http://error"));
                    }

                if (length > 2)
                    try {
                        holder.coverImage3.setImageURI(Uri.parse(item.pictures[2]));
                    } catch (Exception e) {
                        holder.coverImage3.setImageURI(Uri.parse("http://error"));
                    }
            }
        }

        if (viewType != 0) {
            holder.itemView.setOnClickListener(v -> {
                if (mItemListener != null) mItemListener.onClick(v, item);
            });
        } else holder.itemView.setOnClickListener(null);

        if (viewType == 2)
            holder.coverImage.post(() -> {
                int width = holder.coverImage.getMeasuredWidth();
                ViewGroup.LayoutParams params = holder.coverImage.getLayoutParams();
                int height = params.height;
                int aimHeight = width * 246 / 357;
                if (height != aimHeight) {
                    params.height = aimHeight;
                    holder.coverImage.setLayoutParams(params);
                }
            });

        if (viewType == 3)
            holder.coverLayout.post(() -> {
                int width = holder.coverLayout.getMeasuredWidth();
                ViewGroup.LayoutParams params = holder.coverLayout.getLayoutParams();
                int height = params.height;
                int aimHeight = width * 420 / 1008;
                if (height != aimHeight) {
                    params.height = aimHeight;
                    holder.coverLayout.setLayoutParams(params);
                }
            });

        if (viewType == 4)
            holder.coverLayout.post(() -> {
                int width = holder.coverLayout.getMeasuredWidth();
                ViewGroup.LayoutParams params = holder.coverLayout.getLayoutParams();
                int height = params.height;
                int aimHeight = width * 246 / 1008;
                if (height != aimHeight) {
                    params.height = aimHeight;
                    holder.coverLayout.setLayoutParams(params);
                }
            });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public NewsDummy getItem(int position) {
        if (position < mList.size()) return mList.get(position);
        else return null;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mItemListener = listener;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView titleText;

        LinearLayout tagsContainer;

        SimpleDraweeView coverImage, coverImage2, coverImage3;

        View coverLayout;

        TextView authorText, browseText, commentText;

        ViewHolder(View itemView, int viewType) {
            super(itemView);

            if (viewType > -1) titleText = (TextView) itemView.findViewById(R.id.title);
            if (viewType > 0) {
                tagsContainer = (LinearLayout) itemView.findViewById(R.id.tags);
                authorText = (TextView) itemView.findViewById(R.id.author);
                browseText = (TextView) itemView.findViewById(R.id.browse);
                commentText = (TextView) itemView.findViewById(R.id.comment);
            }
            if (viewType > 1) coverImage = (SimpleDraweeView) itemView.findViewById(R.id.cover);
            if (viewType > 2) coverLayout = itemView.findViewById(R.id.cover_layout);
            if (viewType > 3) {
                coverImage2 = (SimpleDraweeView) itemView.findViewById(R.id.cover2);
                coverImage3 = (SimpleDraweeView) itemView.findViewById(R.id.cover3);
            }
        }
    }

    public interface OnItemClickListener {
        void onClick(View v, NewsDummy dummy);
    }
}
