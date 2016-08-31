package com.teamsolo.swear.foundation.ui.widget;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

/**
 * description: fixed swipe refresh layout
 * author: Melody
 * date: 2016/8/31
 * version: 0.0.0.1
 *
 * @see SwipeRefreshLayout
 */
@SuppressWarnings("unused")
public class FixedSwipeRefreshLayout extends SwipeRefreshLayout {

    private float startX, startY;

    private boolean isChildDrag;

    private final int mTouchSlop;

    public FixedSwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        int action = event.getAction();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                startY = event.getY();
                startX = event.getX();
                isChildDrag = false;
                break;

            case MotionEvent.ACTION_MOVE:
                if (isChildDrag) return false;

                float endY = event.getY();
                float endX = event.getX();
                float distanceX = Math.abs(endX - startX);
                float distanceY = Math.abs(endY - startY);

                if (distanceX > mTouchSlop && distanceX > distanceY) {
                    isChildDrag = true;
                    return false;
                }
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                isChildDrag = false;
                break;
        }

        return super.onInterceptTouchEvent(event);
    }
}