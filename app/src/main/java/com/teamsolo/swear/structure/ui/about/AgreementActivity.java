package com.teamsolo.swear.structure.ui.about;

import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import com.teamsolo.swear.R;
import com.teamsolo.swear.foundation.bean.WebLink;
import com.teamsolo.swear.foundation.constant.NetConst;
import com.teamsolo.swear.structure.ui.common.WebLinkActivity;

import org.jetbrains.annotations.NotNull;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * description: agreement page
 * author: Melody
 * date: 2016/8/28
 * version: 0.0.0.1
 */
public class AgreementActivity extends WebLinkActivity {

    @Override
    protected void getBundle(@NotNull Intent intent) {
        mWebLink = new WebLink();
        mWebLink.title = getString(R.string.web_agreement);
        mWebLink.forwardUrl = NetConst.HTTP + NetConst.getBaseHttpUrlForApply() + NetConst.AGREEMENT;
    }

    @Override
    protected void loadUrl(String url) {
        if (!TextUtils.isEmpty(url) && url.startsWith("http://app/loadfile/aboutUs")) {
            String fileName = Uri.parse(url).getQueryParameter("fileName");
            String downloadUrl = Uri.parse(url).getQueryParameter("url");

            if (!TextUtils.isEmpty(fileName) && !TextUtils.isEmpty(downloadUrl)) {
                try {
                    String realFileName = URLDecoder.decode(fileName, "UTF-8");

                    // TODO: download file
                    toast(realFileName + ": " + downloadUrl);
                } catch (UnsupportedEncodingException ignore) {
                }
            }

            return;
        }

        super.loadUrl(url);
    }
}
