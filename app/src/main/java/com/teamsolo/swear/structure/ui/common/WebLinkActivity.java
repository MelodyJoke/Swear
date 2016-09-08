package com.teamsolo.swear.structure.ui.common;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.teamsolo.base.template.activity.BaseActivity;
import com.teamsolo.base.util.BuildUtility;
import com.teamsolo.base.util.LogUtility;
import com.teamsolo.base.util.SecurityUtility;
import com.teamsolo.swear.R;
import com.teamsolo.swear.foundation.bean.WebLink;
import com.teamsolo.swear.structure.ui.LoginActivity;
import com.teamsolo.swear.structure.ui.about.AgreementActivity;

import org.jetbrains.annotations.NotNull;

import java.net.URLDecoder;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import static com.teamsolo.swear.foundation.util.RetrofitConfig.getSessionId;

/**
 * description: web link page
 * author: Melody
 * date: 2016/8/27
 * version: 0.0.0.1
 */
@SuppressWarnings("unused")
public class WebLinkActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {

    public static final String USER_AGENT_DEFAULT = "wenx_webview";

    protected SwipeRefreshLayout mSwipeRefreshLayout;

    protected WebView mWebView;

    protected ProgressBar mProgressBar;

    protected WebLink mWebLink;

    protected boolean canShare;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_link);

        getBundle(getIntent());
        initViews();
        bindListeners();

        if (mWebLink != null && !TextUtils.isEmpty(mWebLink.forwardUrl))
            loadUrl(mWebLink.forwardUrl);
    }

    @Override
    protected void getBundle(@NotNull Intent intent) {
        mWebLink = intent.getParcelableExtra("link");
        canShare = intent.getBooleanExtra("canShare", false);
    }

    @Override
    protected void initViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setOnClickListener(view -> {
            if (mWebView.getScrollY() != 0) mWebView.scrollTo(0, 0);
            else {
                if (!mSwipeRefreshLayout.isRefreshing()) mSwipeRefreshLayout.setRefreshing(true);
                onRefresh();
            }
        });
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(view -> {
            if (BuildUtility.isRequired(Build.VERSION_CODES.LOLLIPOP)) finishAfterTransition();
            else finish();
        });

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            if (mWebLink != null && !TextUtils.isEmpty(mWebLink.title))
                actionBar.setTitle(mWebLink.title);

            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe);
        mSwipeRefreshLayout.setColorSchemeColors(
                Color.parseColor("#F44336"),
                Color.parseColor("#FF5722"),
                Color.parseColor("#CDDC39"),
                Color.parseColor("#4CAF50"));

        mWebView = (WebView) findViewById(R.id.webView);
        initWebView();

        mProgressBar = (ProgressBar) findViewById(R.id.progress);
        mProgressBar.setProgress(0);
        mProgressBar.setSecondaryProgress(0);
    }

    @SuppressLint("SetJavaScriptEnabled")
    protected void initWebView() {
        WebSettings settings = mWebView.getSettings();
        settings.setAllowFileAccess(true);
        settings.setJavaScriptEnabled(true);
        settings.setAppCacheEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        String userAgent = settings.getUserAgentString();
        if (!userAgent.contains(USER_AGENT_DEFAULT))
            userAgent = userAgent + " " + USER_AGENT_DEFAULT;
        settings.setUserAgentString(userAgent);

        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                if (BuildUtility.isRequired(Build.VERSION_CODES.N)) {
                    loadUrl(request.getUrl().toString());
                    return true;
                }
                return super.shouldOverrideUrlLoading(view, request);
            }

            @Override
            @SuppressWarnings("deprecation")
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                loadUrl(url);
                return true;
            }
        });

        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);

                if (mSwipeRefreshLayout == null || mProgressBar == null) return;

                if (newProgress >= 100 && mSwipeRefreshLayout.isRefreshing())
                    mSwipeRefreshLayout.setRefreshing(false);

                mProgressBar.setProgress(newProgress);
                mProgressBar.setSecondaryProgress(newProgress + 5 >= 100 ? 100 : (newProgress + 5));

                if (newProgress >= 100 && mProgressBar.getVisibility() == View.VISIBLE)
                    mProgressBar.setVisibility(View.GONE);

                if (newProgress < 100 && mProgressBar.getVisibility() != View.VISIBLE)
                    mProgressBar.setVisibility(View.VISIBLE);

                WebLinkActivity.this.onProgressChanged(newProgress);
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                WebLinkActivity.this.onReceivedTitle(title);
            }
        });
    }

    @Override
    protected void bindListeners() {
        mSwipeRefreshLayout.setOnRefreshListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.web_link, menu);
        menu.findItem(R.id.action_share).setVisible(canShare);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_share) {
            share(mWebView.getUrl(), mWebView.getTitle());
            return true;
        } else if (id == R.id.action_open_outside) {
            try {
                Intent intent = new Intent("android.intent.action.VIEW");
                intent.setData(Uri.parse(mWebView.getUrl()));
                startActivity(intent);
            } catch (Exception e) {
                toast(R.string.web_open_deny);
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected void loadUrl(String url) {
        // empty url
        if (TextUtils.isEmpty(url)) {
            LogUtility.i("url", "empty url");
            return;
        }

        LogUtility.i("url", url);

        // js call
        if (url.startsWith("javascript:")) {
            mWebView.loadUrl(url);
            return;
        }

        // app protocols
        if (url.startsWith("http://app/login") || url.startsWith("http://wenxue/app/login")) {
            toast(R.string.log_out);
            Intent intent = new Intent(mContext, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            return;
        }

        if (url.startsWith("http://wenxue/app/getInfoDetail")) {
            mWebView.loadUrl("javascript:getNeedInfo('" + getSessionId() + "', '" + SecurityUtility.getDeviceId(mContext) + "')");
            return;
        }

        if (url.equals("http://wenxue/app/end")) {
            if (BuildUtility.isRequired(Build.VERSION_CODES.LOLLIPOP)) finishAfterTransition();
            else finish();
        }

        if (url.startsWith("http://wenxue/app/service/agreement")) {
            startActivity(new Intent(mContext, AgreementActivity.class));
            return;
        }

        if (url.startsWith("http://wenxue/app/share")) {
            try {
                String shareUrl = Uri.parse(url).getQueryParameter("url");
                String title = URLDecoder.decode(Uri.parse(url).getQueryParameter("title"), "UTF-8");

                share(shareUrl, title);
                return;
            } catch (Exception ignore) {
            }
        }

        try {
            Uri uri = Uri.parse(url);
            if (!TextUtils.isEmpty(uri.getQueryParameter("lotteryDrawId"))) {
                Uri.Builder builder = uri.buildUpon();
                if (TextUtils.isEmpty(uri.getQueryParameter("sessionId")) && !TextUtils.isEmpty(getSessionId()))
                    builder.appendQueryParameter("sessionId", getSessionId());

                if (TextUtils.isEmpty(uri.getQueryParameter("app")))
                    builder.appendQueryParameter("app", "1");

                String result = builder.build().toString();
                LogUtility.i("url fixed", result);
                mWebView.loadUrl(result);
                return;
            }
        } catch (Exception ignore) {
        }

        mWebView.loadUrl(url);
    }

    protected void share(String shareUrl, String shareTitle) {
        try {
            Uri.parse(fixShareUrl(shareUrl));
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, shareTitle + ": " + shareUrl);

            startActivity(Intent.createChooser(intent, getString(R.string.share_share_to)));
        } catch (Exception e) {
            toast(R.string.web_share_deny);
        }
    }

    public static String fixShareUrl(String origin) {
        try {
            String result;
            Uri uri = Uri.parse(origin);

            Map<String, String> queryParamPairs = new LinkedHashMap<>();
            Set<String> paramNames = uri.getQueryParameterNames();

            for (String paramName : paramNames) {
                if ("sessionId".equals(paramName) || "app".equals(paramName)) continue;

                String paramValue = uri.getQueryParameter(paramName);
                queryParamPairs.put(paramName, paramValue);
            }

            Uri.Builder builder = uri.buildUpon().clearQuery();

            for (Map.Entry<String, String> entry : queryParamPairs.entrySet())
                builder.appendQueryParameter(entry.getKey(), entry.getValue());

            result = builder.build().toString();

            return result;
        } catch (Exception ignore) {
        }

        return origin;
    }

    @Override
    public void onBackPressed() {
        if (mWebView != null && mWebView.canGoBack()) mWebView.goBack();
        else super.onBackPressed();
    }

    protected void onProgressChanged(int newProgress) {
        // handle progress here
    }

    private void onReceivedTitle(String title) {
        // handle title here
    }

    @Override
    public void onRefresh() {
        mWebView.reload();
    }

    @Override
    public void toast(int msgRes) {
        Snackbar.make(mProgressBar, msgRes, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void toast(String message) {
        Snackbar.make(mProgressBar, message, Snackbar.LENGTH_LONG).show();
    }
}
