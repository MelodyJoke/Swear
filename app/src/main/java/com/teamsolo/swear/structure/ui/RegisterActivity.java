package com.teamsolo.swear.structure.ui;

import android.content.Intent;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Base64;

import com.teamsolo.swear.R;
import com.teamsolo.swear.foundation.bean.WebLink;
import com.teamsolo.swear.foundation.constant.NetConst;
import com.teamsolo.swear.foundation.constant.SpConst;
import com.teamsolo.swear.structure.ui.common.WebLinkActivity;

import org.jetbrains.annotations.NotNull;

/**
 * description: register page
 * author: Melody
 * date: 2016/8/28
 * version: 0.0.0.1
 */
public class RegisterActivity extends WebLinkActivity {

    @Override
    protected void getBundle(@NotNull Intent intent) {
        mWebLink = new WebLink();
        mWebLink.title = getString(R.string.register_title);
        mWebLink.forwardUrl = NetConst.HTTP + NetConst.getBaseHttpUrl() + NetConst.PATH_PRE + NetConst.REGISTER;
    }

    @Override
    protected void loadUrl(String url) {
        if (!TextUtils.isEmpty(url) && url.startsWith("http://wenxue/app/login")) {
            String phone = Uri.parse(url).getQueryParameter("phone");
            String password = Uri.parse(url).getQueryParameter("password");

            PreferenceManager.getDefaultSharedPreferences(mContext).edit()
                    .putString(SpConst.LAST_PHONE, phone)
                    .putString(SpConst.LAST_PASSWORD, Base64.encodeToString(password.getBytes(), Base64.DEFAULT))
                    .apply();

            Intent intent = new Intent(mContext, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);

            finish();
        }

        super.loadUrl(url);
    }
}
