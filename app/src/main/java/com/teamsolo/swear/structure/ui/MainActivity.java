package com.teamsolo.swear.structure.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.teamsolo.base.template.activity.HandlerActivity;
import com.teamsolo.swear.R;
import com.teamsolo.swear.foundation.bean.User;
import com.teamsolo.swear.foundation.bean.WebLink;
import com.teamsolo.swear.foundation.constant.NetConst;
import com.teamsolo.swear.structure.ui.about.AboutActivity;
import com.teamsolo.swear.structure.util.UserHelper;

import org.jetbrains.annotations.NotNull;

/**
 * description: main page
 * author: Melody
 * date: 2016/8/28
 * version: 0.0.0.1
 */

public class MainActivity extends HandlerActivity implements
        NavigationView.OnNavigationItemSelectedListener {

    private FloatingActionButton mFab;

    private DrawerLayout mDrawer;

    private NavigationView mNavigationView;

    private SimpleDraweeView mPortraitImage, mChildPortraitImage;

    private TextView mUsernameText, mChildText, mSchoolText;

    private User mUser;

    private boolean isWaitingForSecondBackPress;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getBundle(getIntent());
        initViews();
        bindListeners();

        new Thread(this::invalidateUIAboutUser).start();
    }

    @Override
    protected void handleMessage(HandlerActivity activity, Message msg) {

    }

    @Override
    protected void getBundle(@NotNull Intent intent) {

    }

    @Override
    protected void initViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mFab = (FloatingActionButton) findViewById(R.id.fab);

        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawer, toolbar, R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        mDrawer.addDrawerListener(toggle);
        toggle.syncState();

        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        if (mNavigationView != null) {
            View headerView = mNavigationView.getHeaderView(0);
            if (headerView != null) {
                mPortraitImage = (SimpleDraweeView) headerView.findViewById(R.id.nav_header_portrait);
                mChildPortraitImage = (SimpleDraweeView) headerView.findViewById(R.id.nav_header_portrait_child);
                mUsernameText = (TextView) headerView.findViewById(R.id.nav_header_username);
                mChildText = (TextView) headerView.findViewById(R.id.nav_header_child);
                mSchoolText = (TextView) headerView.findViewById(R.id.nav_header_school);
            }
        }
    }

    @Override
    protected void bindListeners() {
        mFab.setOnClickListener(view -> {
            // TODO: choose child
        });

        mNavigationView.setNavigationItemSelectedListener(this);

        mPortraitImage.setOnClickListener(view -> {
            // TODO:
        });

        mChildPortraitImage.setOnClickListener(view -> {
            // TODO:
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        // TODO: options action
        switch (id) {
            case R.id.action_refresh:
                return true;

            case R.id.action_feedback:
                return true;

            case R.id.action_setting:
                return true;

            case R.id.action_help:
                WebLink webLink = new WebLink();
                webLink.title = getString(R.string.web_help_center);
                webLink.forwardUrl = NetConst.HTTP + NetConst.getBaseHttpUrl() + NetConst.HELP_CENTER;

                Intent intentHelp = new Intent(mContext, WebLinkActivity.class);
                intentHelp.putExtra("link", webLink);
                startActivity(intentHelp);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        boolean closeDrawer = false;

        // TODO: nav drawer action
        switch (id) {
            case R.id.nav_attention:
                break;

            case R.id.nav_collection:
                break;

            case R.id.nav_history:
                break;

            case R.id.nav_download:
                break;

            case R.id.nav_account:
                break;

            case R.id.nav_order:
                break;

            case R.id.nav_action_setting:
                break;

            case R.id.nav_action_about_us:
                startActivity(new Intent(mContext, AboutActivity.class));
                break;

            case R.id.nav_action_logout:
                closeDrawer = true;
                handler.postDelayed(() -> {
                    Intent intentLogout = new Intent(mContext, LoginActivity.class);
                    intentLogout.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intentLogout);
                    finish();
                }, 300);
                break;
        }

        if (closeDrawer) mDrawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (mDrawer.isDrawerOpen(GravityCompat.START)) mDrawer.closeDrawer(GravityCompat.START);
        else if (!isWaitingForSecondBackPress) {
            isWaitingForSecondBackPress = true;
            Snackbar.make(mFab, R.string.double_click_exit, Snackbar.LENGTH_SHORT).show();
            handler.postDelayed(() -> isWaitingForSecondBackPress = false, 1000);
        } else super.onBackPressed();
    }

    @Override
    public void toast(String message) {
        Snackbar.make(mFab, message, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void toast(int msgRes) {
        Snackbar.make(mFab, msgRes, Snackbar.LENGTH_LONG).show();
    }

    @SuppressWarnings("deprecation")
    private void invalidateUIAboutUser() {
        mUser = UserHelper.getUser(mContext);

        handler.post(() -> {
            if (mUser != null) {
                if (!TextUtils.isEmpty(mUser.parentsName)) mUsernameText.setText(mUser.parentsName);
                else mUsernameText.setText(R.string.unknown);

                if (!TextUtils.isEmpty(mUser.parentPath)) {
                    try {
                        mPortraitImage.setImageURI(Uri.parse(mUser.parentPath));
                    } catch (Exception e) {
                        mPortraitImage.setImageResource(R.mipmap.portrait_default);
                    }
                } else mPortraitImage.setImageResource(R.mipmap.portrait_default);
            } else {
                mUsernameText.setText(R.string.unknown);
                mPortraitImage.setImageResource(R.mipmap.portrait_default);
            }
        });
    }
}
