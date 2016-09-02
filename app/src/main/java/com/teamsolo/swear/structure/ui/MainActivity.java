package com.teamsolo.swear.structure.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.facebook.drawee.view.SimpleDraweeView;
import com.teamsolo.base.template.activity.HandlerActivity;
import com.teamsolo.base.template.fragment.BaseFragment;
import com.teamsolo.swear.R;
import com.teamsolo.swear.foundation.bean.User;
import com.teamsolo.swear.foundation.bean.WebLink;
import com.teamsolo.swear.foundation.constant.NetConst;
import com.teamsolo.swear.foundation.ui.Appendable;
import com.teamsolo.swear.foundation.ui.ScrollAble;
import com.teamsolo.swear.structure.ui.about.AboutActivity;
import com.teamsolo.swear.structure.ui.mine.OrdersActivity;
import com.teamsolo.swear.structure.ui.mine.OrdersFragment;
import com.teamsolo.swear.structure.util.UserHelper;

import org.jetbrains.annotations.NotNull;

/**
 * description: main page
 * author: Melody
 * date: 2016/8/28
 * version: 0.0.0.1
 */

public class MainActivity extends HandlerActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        BaseFragment.OnFragmentInteractionListener,
        Appendable {

    private static final int FRAG_SCHOOL = 0, FRAG_TRAINING = 1, FRAG_NEWS = 2, FRAG_NLG = 3;

    private FloatingActionButton mFab;

    private DrawerLayout mDrawer;

    private NavigationView mNavigationView;

    private TabLayout mTabLayout;

    private BottomNavigationBar mBottomNavigationBar;

    private SimpleDraweeView mPortraitImage, mChildPortraitImage;

    private TextView mUsernameText, mChildText, mSchoolText;

    private User mUser;

    private boolean isWaitingForSecondBackPress;

    private FragmentManager fragmentManager;

    private SparseArray<Fragment> fragments = new SparseArray<>();

    private Fragment currentFragment;

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
        fragmentManager = getSupportFragmentManager();
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

        mTabLayout = (TabLayout) findViewById(R.id.tab);
        mTabLayout.setVisibility(View.GONE);

        mBottomNavigationBar = (BottomNavigationBar) findViewById(R.id.bnb);
        mBottomNavigationBar.setFab(mFab);
        mBottomNavigationBar
                .addItem(new BottomNavigationItem(R.drawable.ic_business_white_24dp, getString(R.string.school_nav)))
                .addItem(new BottomNavigationItem(R.drawable.ic_school_white_24dp, getString(R.string.training_nav)))
                .addItem(new BottomNavigationItem(R.drawable.ic_receipt_white_24dp, getString(R.string.news_nav)))
                .addItem(new BottomNavigationItem(R.drawable.ic_local_library_white_24dp, getString(R.string.nlg_nav)))
                .initialise();

        fragments.append(FRAG_SCHOOL, OrdersFragment.newInstance(FRAG_SCHOOL));
        /*fragments.append(FRAG_TRAINING, OrdersFragment.newInstance(FRAG_TRAINING));
        fragments.append(FRAG_NEWS, OrdersFragment.newInstance(FRAG_NEWS));
        fragments.append(FRAG_NLG, OrdersFragment.newInstance(FRAG_NLG));*/

        fragmentManager.beginTransaction()
                .add(R.id.content, fragments.get(FRAG_SCHOOL), String.valueOf(FRAG_SCHOOL))
                /*.add(R.id.content, fragments.get(FRAG_TRAINING), String.valueOf(FRAG_TRAINING))
                .add(R.id.content, fragments.get(FRAG_NEWS), String.valueOf(FRAG_NEWS))
                .add(R.id.content, fragments.get(FRAG_NLG), String.valueOf(FRAG_NLG))*/
                .show(fragments.get(FRAG_SCHOOL))
                .commit();
        currentFragment = fragments.get(FRAG_SCHOOL);
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

        mBottomNavigationBar.setTabSelectedListener(new BottomNavigationBar.OnTabSelectedListener() {
            @Override
            public void onTabSelected(int position) {
                ActionBar actionBar = getSupportActionBar();

                switch (position) {
                    case 0:
                        if (actionBar != null) actionBar.setTitle(R.string.app_name);

                        Fragment fragmentSchool = fragments.get(FRAG_SCHOOL);
                        fragmentManager.beginTransaction()
                                .show(fragmentSchool)
                                .hide(currentFragment)
                                .commit();
                        currentFragment = fragmentSchool;
                        break;

                    case 1:
                        if (actionBar != null) actionBar.setTitle(R.string.training_nav);
                        if (fragments.get(FRAG_TRAINING) == null) {
                            Fragment fragmentTraining = OrdersFragment.newInstance(FRAG_TRAINING);
                            fragments.append(FRAG_TRAINING, fragmentTraining);
                            fragmentManager.beginTransaction()
                                    .add(R.id.content, fragments.get(FRAG_TRAINING), String.valueOf(FRAG_TRAINING))
                                    .show(fragmentTraining)
                                    .hide(currentFragment)
                                    .commit();
                            currentFragment = fragmentTraining;
                        } else {
                            Fragment fragmentTraining = fragments.get(FRAG_TRAINING);
                            fragmentManager.beginTransaction()
                                    .show(fragmentTraining)
                                    .hide(currentFragment)
                                    .commit();
                            currentFragment = fragmentTraining;
                        }
                        break;

                    case 2:
                        if (actionBar != null) actionBar.setTitle(R.string.news_nav);
                        if (fragments.get(FRAG_NEWS) == null) {
                            Fragment fragmentNews = OrdersFragment.newInstance(FRAG_NEWS);
                            fragments.append(FRAG_NEWS, fragmentNews);
                            fragmentManager.beginTransaction()
                                    .add(R.id.content, fragments.get(FRAG_NEWS), String.valueOf(FRAG_NEWS))
                                    .show(fragmentNews)
                                    .hide(currentFragment)
                                    .commit();
                            currentFragment = fragmentNews;
                        } else {
                            Fragment fragmentNews = fragments.get(FRAG_NEWS);
                            fragmentManager.beginTransaction()
                                    .show(fragmentNews)
                                    .hide(currentFragment)
                                    .commit();
                            currentFragment = fragmentNews;
                        }
                        break;

                    case 3:
                        if (actionBar != null) actionBar.setTitle(R.string.nlg_nav);
                        if (fragments.get(FRAG_NLG) == null) {
                            Fragment fragmentNlg = OrdersFragment.newInstance(FRAG_NLG);
                            fragments.append(FRAG_NLG, fragmentNlg);
                            fragmentManager.beginTransaction()
                                    .add(R.id.content, fragments.get(FRAG_NLG), String.valueOf(FRAG_NLG))
                                    .show(fragmentNlg)
                                    .hide(currentFragment)
                                    .commit();
                            currentFragment = fragmentNlg;
                        } else {
                            Fragment fragmentNlg = fragments.get(FRAG_NLG);
                            fragmentManager.beginTransaction()
                                    .show(fragmentNlg)
                                    .hide(currentFragment)
                                    .commit();
                            currentFragment = fragmentNlg;
                        }
                        break;
                }

                if (position < fragments.size()) {
                    fragmentManager.beginTransaction()
                            .hide(currentFragment)
                            .show(fragments.get(position))
                            .commit();

                    currentFragment = fragments.get(position);
                }
            }

            @Override
            public void onTabUnselected(int position) {

            }

            @Override
            public void onTabReselected(int position) {
                if (currentFragment instanceof ScrollAble)
                    ((ScrollAble) currentFragment).scroll(Uri.parse("scroll?top=true"));
            }
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
                startActivity(new Intent(mContext, SettingsActivity.class));
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
                startActivity(new Intent(mContext, OrdersActivity.class));
                break;

            case R.id.nav_action_setting:
                startActivity(new Intent(mContext, SettingsActivity.class));
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
            toast(R.string.double_click_exit);

            handler.postDelayed(() -> isWaitingForSecondBackPress = false, 1000);
        } else super.onBackPressed();
    }

    @SuppressWarnings("deprecation")
    private void invalidateUIAboutUser() {
        mUser = UserHelper.getUser(mContext);

        handler.post(() -> {
            if (mUser != null) {
                if (!TextUtils.isEmpty(mUser.parentsName)) mUsernameText.setText(mUser.parentsName);
                else mUsernameText.setText(R.string.app_name);

                if (!TextUtils.isEmpty(mUser.parentPath)) {
                    try {
                        mPortraitImage.setImageURI(Uri.parse(mUser.parentPath));
                    } catch (Exception e) {
                        mPortraitImage.setImageResource(R.mipmap.portrait_default);
                    }
                } else mPortraitImage.setImageResource(R.mipmap.portrait_default);
            } else {
                mUsernameText.setText(R.string.app_name);
                mPortraitImage.setImageResource(R.mipmap.portrait_default);
            }
        });
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void append(Uri uri) {
        if (currentFragment instanceof Appendable) ((Appendable) currentFragment).append(null);
    }
}
