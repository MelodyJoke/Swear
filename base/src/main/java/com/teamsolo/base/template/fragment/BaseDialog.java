package com.teamsolo.base.template.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.View;

import org.jetbrains.annotations.NotNull;

/**
 * description: base dialog fragment
 * author: Melody
 * date: 2016/9/9
 * version: 0.0.0.1
 */
@SuppressWarnings("unused")
public abstract class BaseDialog extends DialogFragment {

    protected Dialog mDialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getBundle(getArguments());
    }

    protected void setContentView(@LayoutRes int layoutRes) {
        mDialog = new Dialog(getContext());
        mDialog.setContentView(layoutRes);
    }

    protected abstract void getBundle(@NotNull Bundle bundle);

    protected abstract void initViews();

    protected abstract void bindListeners();

    public View findViewById(@IdRes int idRes) {
        return mDialog.findViewById(idRes);
    }
}
