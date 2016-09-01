package com.teamsolo.swear.structure.util;

import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.teamsolo.swear.R;

/**
 * description: loading ui util
 * author: Melody
 * date: 2016/9/1
 * version: 0.0.0.1
 */
@SuppressWarnings("WeakerAccess, unused")
public class LoadingUtil {

    private ImageView imageView;

    private ProgressBar progressBar;

    private TextView textView;

    private View loadingView;

    private View[] mainViews;

    private int loadingHintRes, emptyHintRes;

    public LoadingUtil(View loadingView, View... mainViews) {
        this.loadingView = loadingView;
        this.imageView = (ImageView) loadingView.findViewById(R.id.loading_image);
        this.progressBar = (ProgressBar) loadingView.findViewById(R.id.loading_progress);
        this.textView = (TextView) loadingView.findViewById(R.id.loading_hint);
        this.mainViews = mainViews;
    }

    public LoadingUtil init(int imageRes, int loadingHintRes, int emptyHintRes) {
        imageView.setImageResource(imageRes);
        this.loadingHintRes = loadingHintRes;
        this.emptyHintRes = emptyHintRes;

        return this;
    }

    public void showLoading() {
        if (loadingView.getVisibility() != View.VISIBLE)
            loadingView.setVisibility(View.VISIBLE);

        imageView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        textView.setText(loadingHintRes);

        for (View mainView
                : mainViews)
            mainView.setVisibility(View.GONE);
    }

    public void showEmpty() {
        if (loadingView.getVisibility() != View.VISIBLE)
            loadingView.setVisibility(View.VISIBLE);

        imageView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        textView.setText(emptyHintRes);

        for (View mainView
                : mainViews)
            mainView.setVisibility(View.GONE);
    }

    public void dismiss() {
        if (loadingView.getVisibility() != View.GONE)
            loadingView.setVisibility(View.GONE);

        for (View mainView
                : mainViews)
            mainView.setVisibility(View.VISIBLE);
    }
}
