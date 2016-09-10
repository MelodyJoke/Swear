package com.teamsolo.swear.foundation.ui.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.facebook.drawee.view.SimpleDraweeView;
import com.teamsolo.base.util.DisplayUtility;
import com.teamsolo.swear.R;

import java.util.ArrayList;
import java.util.List;

/**
 * description: slide show
 * author: Melody
 * date: 2016/9/10
 * version: 0.0.0.1
 */
@SuppressWarnings("MismatchedQueryAndUpdateOfCollection, unused")
public class SlideShowView extends RelativeLayout {

    private static final int padding = DisplayUtility.getPxFromDp(8);

    private ViewPager viewPager;

    private LinearLayout indicatorContainer;

    private List<SlideShowDummy> dummies = new ArrayList<>();

    private List<SimpleDraweeView> pagers;

    private List<CheckedTextView> indicators;

    private PagerAdapter adapter;

    private int drawableRes = R.drawable.selector_slide_indicator;

    private int current;

    private OnItemClickListener onItemClickListener;

    private SlideShowPlayHandler mSlideShowHandler;

    public SlideShowView(Context context) {
        super(context);
        init();
    }

    public SlideShowView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SlideShowView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public SlideShowView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.view_slide_show, this);
        initViews();
        bindListeners();
    }

    private void initViews() {
        viewPager = (ViewPager) findViewById(R.id.pager);
        pagers = new ArrayList<>();
        adapter = new PagerAdapter() {
            @Override
            public int getCount() {
                return dummies.size();
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                String item = dummies.get(position).getUrl();
                SimpleDraweeView imageView = (SimpleDraweeView) LayoutInflater.from(getContext())
                        .inflate(R.layout.item_slide_show, container, false);
                try {
                    if (!TextUtils.isEmpty(item)) imageView.setImageURI(Uri.parse(item));
                    else imageView.setImageURI(Uri.parse("http://error"));
                } catch (Exception e) {
                    imageView.setImageURI(Uri.parse("http://error"));
                }

                imageView.setOnClickListener(v -> {
                    if (onItemClickListener != null)
                        onItemClickListener.onClick(v, current, dummies.get(current));
                });

                pagers.add(imageView);
                container.addView(imageView);
                return imageView;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                if (object instanceof SimpleDraweeView) {
                    SimpleDraweeView imageView = (SimpleDraweeView) object;
                    container.removeView(imageView);
                    pagers.remove(imageView);
                }
            }
        };
        viewPager.setAdapter(adapter);

        indicatorContainer = (LinearLayout) findViewById(R.id.indicators);
    }

    @SuppressWarnings("deprecation")
    private void invalidateIndicators(boolean init) {
        if (init) {
            indicatorContainer.removeAllViews();
            indicators = new ArrayList<>();

            for (int i = 0; i < dummies.size(); i++) {
                Drawable drawable = getContext().getResources().getDrawable(drawableRes);
                drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());

                CheckedTextView indicator = new CheckedTextView(getContext());
                indicator.setCompoundDrawables(null, null, null, drawable);
                indicator.setPadding(padding / 2, padding, padding / 2, padding);

                indicators.add(indicator);
                indicatorContainer.addView(indicator);
            }
        }

        for (int i = 0; i < indicators.size(); i++) indicators.get(i).setChecked(i == current);
    }

    private void bindListeners() {
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                current = position;
                invalidateIndicators(false);

                if (mSlideShowHandler != null)
                    Message.obtain(mSlideShowHandler, SlideShowPlayHandler.PAGE_SWITCH, position, 0).sendToTarget();
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                switch (state) {
                    case ViewPager.SCROLL_STATE_DRAGGING:
                        if (mSlideShowHandler != null)
                            mSlideShowHandler.sendEmptyMessage(SlideShowPlayHandler.SHUT_UP);
                        break;

                    case ViewPager.SCROLL_STATE_IDLE:
                        if (mSlideShowHandler != null)
                            mSlideShowHandler.sendEmptyMessageDelayed(
                                    SlideShowPlayHandler.TURN_OVER, SlideShowPlayHandler.SLIDE_DELAY);
                        break;

                    default:
                        break;
                }
            }
        });
    }

    public ViewPager getViewPager() {
        return viewPager;
    }

    public void setIndicatorDrawable(int drawableRes) {
        this.drawableRes = drawableRes;
        invalidateIndicators(true);
    }

    public void setDummies(List<SlideShowDummy> dummies) {
        this.dummies.clear();
        this.dummies.addAll(dummies);
        adapter.notifyDataSetChanged();

        current = 0;
        invalidateIndicators(true);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        onItemClickListener = listener;
    }

    public void setSlideShowHandler(SlideShowPlayHandler handler) {
        mSlideShowHandler = handler;
    }

    public void startFlipping() {
        if (mSlideShowHandler != null)
            mSlideShowHandler.sendEmptyMessageDelayed(
                    SlideShowPlayHandler.TURN_OVER, SlideShowPlayHandler.SLIDE_DELAY);
    }

    public void stopFlipping() {
        if (mSlideShowHandler != null)
            mSlideShowHandler.sendEmptyMessage(SlideShowPlayHandler.SHUT_UP);
    }

    public interface SlideShowDummy {
        String getUrl();
    }

    public interface OnItemClickListener {
        void onClick(View view, int position, SlideShowDummy dummy);
    }

    public interface SlideShowParent {
        SlideShowPlayHandler getHandler();

        SlideShowView getSlideShowView();
    }
}
