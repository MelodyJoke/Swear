package com.teamsolo.base.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.View;

import com.teamsolo.base.template.application.BaseApplication;

/**
 * description: display util
 * author: Melody
 * date: 2016/6/26
 * version: 0.0.0.2
 */
@SuppressWarnings("WeakerAccess, unused")
public final class DisplayUtility {

    public static final int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec((1 << 30) - 1, View.MeasureSpec.AT_MOST);

    public static final int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec((1 << 30) - 1, View.MeasureSpec.AT_MOST);

    private DisplayUtility() {
    }

    /**
     * get pixels from dip
     *
     * @param dip dp size
     * @return px size
     */
    public static int getPxFromDp(float dip) {
        DisplayMetrics metrics = getDisplayMetrics();

        if (metrics != null) {
            final float scale = metrics.density;
            return (int) (dip * scale + 0.5f);
        }

        return (int) dip;
    }

    /**
     * get dip from pixel
     *
     * @param pixel px size
     * @return dp size
     */
    public static int getDpFromPx(float pixel) {
        DisplayMetrics metrics = getDisplayMetrics();

        if (metrics != null) {
            final float scale = metrics.density;
            return (int) (pixel / scale + 0.5f);
        }

        return (int) pixel;
    }

    /**
     * get the display metrics of the current activity
     * then you can get properties of display such as screen width / height
     *
     * @return the metrics
     */
    @Nullable
    public static DisplayMetrics getDisplayMetrics() {
        Activity activity = BaseApplication.getInstance().getCurrentActivity();
        if (activity == null) return null;

        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);

        return metrics;
    }

    /**
     * charge if is large tablet
     *
     * @param context context
     * @return if is large tablet
     */
    public static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    /**
     * get res uri
     *
     * @param resId       res id
     * @param packageName package name
     * @return uri
     */
    public static Uri getResourceUri(int resId, String packageName) {
        return Uri.parse("android.resource://" + packageName + "/" + resId);
    }
}
