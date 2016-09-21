package com.teamsolo.swear.structure.ui.mine.view;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckedTextView;

import com.teamsolo.base.template.fragment.BaseDialog;
import com.teamsolo.swear.R;

import org.jetbrains.annotations.NotNull;

/**
 * description: appellation dialog
 * author: Melody
 * date: 2016/9/21
 * version: 0.0.0.1
 */
@SuppressWarnings("unused")
public class AppellationPickDialog extends BaseDialog implements View.OnClickListener {

    private CheckedTextView[] buttons;

    private String appellation;

    private OnButtonClickListener listener;

    public AppellationPickDialog() {

    }

    public static AppellationPickDialog newInstance() {
        AppellationPickDialog dialog = new AppellationPickDialog();
        Bundle args = new Bundle();
        dialog.setArguments(args);
        return dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        setContentView(R.layout.view_appellation_pick);

        Window window = mDialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams params = window.getAttributes();
            params.gravity = Gravity.BOTTOM;
            params.width = WindowManager.LayoutParams.MATCH_PARENT;
            window.setAttributes(params);
        }

        initViews();
        bindListeners();

        return mDialog;
    }

    @Override
    protected void getBundle(@NotNull Bundle bundle) {

    }

    @Override
    protected void initViews() {
        buttons = new CheckedTextView[6];
        buttons[0] = (CheckedTextView) findViewById(R.id.father);
        buttons[1] = (CheckedTextView) findViewById(R.id.mother);
        buttons[2] = (CheckedTextView) findViewById(R.id.g_father);
        buttons[3] = (CheckedTextView) findViewById(R.id.g_mother);
        buttons[4] = (CheckedTextView) findViewById(R.id.mg_father);
        buttons[5] = (CheckedTextView) findViewById(R.id.mg_mother);

        invalidate();
    }

    @Override
    protected void bindListeners() {
        for (CheckedTextView button :
                buttons)
            button.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        appellation = ((CheckedTextView) v).getText().toString();
        invalidate();

        if (listener != null) listener.onClick(v, appellation);
    }

    private void invalidate() {
        for (CheckedTextView button :
                buttons)
            button.setChecked(TextUtils.equals(appellation, button.getText().toString()));
    }

    public void setAppellation(String appellation) {
        this.appellation = appellation;
        if (buttons != null) invalidate();
    }

    public void setOnButtonClickListener(OnButtonClickListener listener) {
        this.listener = listener;
    }

    public interface OnButtonClickListener {
        void onClick(View view, String appellation);
    }
}
