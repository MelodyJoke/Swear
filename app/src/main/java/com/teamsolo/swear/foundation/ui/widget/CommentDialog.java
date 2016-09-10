package com.teamsolo.swear.foundation.ui.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.teamsolo.base.template.fragment.BaseDialog;
import com.teamsolo.swear.R;
import com.teamsolo.swear.foundation.constant.SpConst;

import org.jetbrains.annotations.NotNull;

/**
 * description: common comment dialog
 * author: Melody
 * date: 2016/9/9
 * version: 0.0.0.1
 */
@SuppressWarnings("unused")
public class CommentDialog extends BaseDialog {

    private EditText mInputEdit;

    private TextView mCountText;

    private View mCancelButton, mConfirmButton;

    private int maxLength = 500;

    private OnButtonClickListener mCancelListener, mConfirmListener;

    public CommentDialog() {

    }

    public static CommentDialog newInstance(int maxLength) {
        CommentDialog dialog = new CommentDialog();
        Bundle args = new Bundle();
        args.putInt("maxLength", maxLength);
        dialog.setArguments(args);
        return dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        setContentView(R.layout.view_comment);

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
        maxLength = bundle.getInt("maxLength");
    }

    @Override
    protected void initViews() {
        mInputEdit = (EditText) findViewById(R.id.input);

        mCountText = (TextView) findViewById(R.id.count);
        mCountText.setText(String.format(
                getContext().getString(R.string.news_comments_length), 0, maxLength));

        mCancelButton = findViewById(R.id.cancel);
        mConfirmButton = findViewById(R.id.confirm);
    }

    @Override
    protected void bindListeners() {
        mCancelButton.setOnClickListener(v -> {
            if (mCancelListener != null) mCancelListener.onClick(v, mInputEdit);
        });

        mConfirmButton.setOnClickListener(v -> {
            if (mConfirmListener != null) mConfirmListener.onClick(v, mInputEdit);
        });

        mDialog.setOnShowListener(dialog -> {
            String contentCache = PreferenceManager.getDefaultSharedPreferences(getContext())
                    .getString(SpConst.NEWS_COMMENT_CACHE, "");
            mInputEdit.requestFocus();
            if (!TextUtils.isEmpty(contentCache)) {
                mInputEdit.setText(contentCache);
                mInputEdit.setSelection(contentCache.length());
            }

            InputMethodManager inputManager =
                    (InputMethodManager) mInputEdit.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.showSoftInput(mInputEdit, 0);
        });

        mInputEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!isAdded() || isDetached()) return;

                if (s.length() > maxLength) {
                    mInputEdit.setText(s.subSequence(0, maxLength));
                    mInputEdit.setSelection(maxLength);
                    mCountText.setText(String.format(
                            getContext().getString(R.string.news_comments_length), maxLength, maxLength));
                } else
                    mCountText.setText(String.format(
                            getContext().getString(R.string.news_comments_length), s.length(), maxLength));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public void setOnCancelButtonClickListener(OnButtonClickListener listener) {
        mCancelListener = listener;
    }

    public void setOnConfirmButtonClickListener(OnButtonClickListener listener) {
        mConfirmListener = listener;
    }

    public void clearInput() {
        mInputEdit.getText().clear();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);

        PreferenceManager.getDefaultSharedPreferences(getContext()).edit()
                .putString(SpConst.NEWS_COMMENT_CACHE, mInputEdit.getText().toString()).apply();
    }

    public interface OnButtonClickListener {
        void onClick(View v, EditText editText);
    }
}
