package com.teamsolo.swear.structure.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.teamsolo.swear.R;

/**
 * description: dialog with a dialog
 * author: Melody
 * date: 2016/10/1
 * version: 0.0.0.1
 */
@SuppressWarnings("WeakerAccess, unused")
public class DialogUtil {

    public static AlertDialog newInstance(Context context, String title, String preText,
                                          boolean hideAction, String actionHint,
                                          OnButtonClickListener positiveCallback,
                                          OnButtonClickListener actionCallback) {
        @SuppressLint("InflateParams")
        final View content = LayoutInflater.from(context).inflate(R.layout.view_text_edit, null);
        final EditText input = ((EditText) content.findViewById(R.id.input));
        final TextView action = (TextView) content.findViewById(R.id.action);
        input.setText(preText);
        action.setVisibility(hideAction ? View.GONE : View.VISIBLE);
        action.setText(actionHint);

        AlertDialog mDialog = new AlertDialog.Builder(context)
                .setTitle(title)
                .setView(content)
                .setNegativeButton(R.string.cancel, (dialog, which) -> {
                })
                .setPositiveButton(R.string.ok, (dialog, which) -> {
                    String contentString = input.getText().toString();

                    if (TextUtils.isEmpty(contentString)) return;

                    if (positiveCallback != null)
                        positiveCallback.onClick(dialog, contentString);
                })
                .create();

        input.setOnEditorActionListener((v1, actionId, event) -> {
            String contentString = input.getText().toString();

            if (TextUtils.isEmpty(contentString)) return true;

            if (positiveCallback != null)
                positiveCallback.onClick(mDialog, contentString);
            mDialog.dismiss();
            return true;
        });

        mDialog.setOnShowListener(dialog -> {
            InputMethodManager inputManager =
                    (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.showSoftInput(input, 0);
        });

        action.setOnClickListener(v -> {
            if (actionCallback != null) actionCallback.onClick(mDialog, "");
        });

        return mDialog;
    }

    public interface OnButtonClickListener {
        void onClick(DialogInterface dialogInterface, String content);
    }
}
