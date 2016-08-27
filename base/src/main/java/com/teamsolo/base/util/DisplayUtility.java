package com.teamsolo.base.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;

import com.teamsolo.base.template.application.BaseApplication;

/**
 * description: display util
 * author: Melody
 * date: 2016/6/26
 * version: 0.0.0.2
 */
@SuppressWarnings("WeakerAccess, unused")
public final class DisplayUtility {

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
}
