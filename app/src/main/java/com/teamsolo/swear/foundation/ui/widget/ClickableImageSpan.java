package com.teamsolo.swear.foundation.ui.widget;

import android.graphics.drawable.Drawable;
import android.text.style.ImageSpan;
import android.view.View;

/**
 * description: clickable image span
 * author: Melody
 * date: 2016/9/6
 * version: 0.0.0.1
 *
 * @see ClickableMovementMethod to support this
 */
@SuppressWarnings("WeakerAccess")
public abstract class ClickableImageSpan extends ImageSpan {

    public ClickableImageSpan(Drawable drawable) {
        super(drawable);
    }

    public abstract void onClick(View view);
}
