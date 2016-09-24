package com.teamsolo.swear.foundation.ui;

import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.view.View;

/**
 * description: fab interact able
 * author: Melody
 * date: 2016/9/24
 * version: 0.0.0.1
 */
@SuppressWarnings("unused")
public interface FabInteractAble {

    void interact(FloatingActionButton fab, Uri uri, View... others);
}
