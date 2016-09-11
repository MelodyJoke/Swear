package com.teamsolo.swear.foundation.ui.widget;

import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;

/**
 * description: slide show auto play handler
 * author: Melody
 * date: 2016/9/11
 * version: 0.0.0.1
 */
@SuppressWarnings("WeakerAccess, unused")
public class SlideShowPlayHandler extends Handler {

    public static final int TURN_OVER = 1, SHUT_UP = 2, BREAK_SILENT = 3, PAGE_SWITCH = 4;

    public static long SLIDE_DELAY = 5000;

    private WeakReference<SlideShowView.SlideShowParent> weakReference;

    private int currentItem = 0;

    public SlideShowPlayHandler(WeakReference<SlideShowView.SlideShowParent> reference, long delay) {
        weakReference = reference;
        SLIDE_DELAY = delay;
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);

        SlideShowView.SlideShowParent parent = weakReference.get();
        if (parent == null) return;

        if (parent.getHandler().hasMessages(TURN_OVER))
            parent.getHandler().removeMessages(TURN_OVER);

        switch (msg.what) {
            case TURN_OVER:
                currentItem++;
                int count = parent.getSlideShowView().getViewPager().getAdapter().getCount();
                if (currentItem >= count) currentItem = 0;

                parent.getSlideShowView().getViewPager().setCurrentItem(currentItem);
                parent.getHandler().sendEmptyMessageDelayed(TURN_OVER, SLIDE_DELAY);
                break;

            case SHUT_UP:
                break;

            case BREAK_SILENT:
                parent.getHandler().sendEmptyMessageDelayed(TURN_OVER, SLIDE_DELAY);
                break;

            case PAGE_SWITCH:
                currentItem = msg.arg1;
                break;

            default:
                break;
        }
    }

    public void setCurrentItem(int position) {
        this.currentItem = position;
    }
}
