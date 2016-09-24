package com.teamsolo.swear.structure.ui;

import android.content.Intent;
import android.graphics.Color;
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
import android.support.v4.widget.SwipeRefreshLayout;
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
import com.teamsolo.swear.foundation.bean.Child;
import com.teamsolo.swear.foundation.bean.User;
import com.teamsolo.swear.foundation.bean.WebLink;
import com.teamsolo.swear.foundation.bean.resp.ChildChooseResp;
import com.teamsolo.swear.foundation.constant.NetConst;
import com.teamsolo.swear.foundation.ui.Appendable;
import com.teamsolo.swear.foundation.ui.Refreshable;
import com.teamsolo.swear.foundation.ui.ScrollAble;
import com.teamsolo.swear.foundation.ui.SearchAble;
import com.teamsolo.swear.foundation.util.RetrofitConfig;
import com.teamsolo.swear.structure.request.BaseHttpUrlRequests;
import com.teamsolo.swear.structure.ui.about.AboutActivity;
import com.teamsolo.swear.structure.ui.common.WebLinkActivity;
import com.teamsolo.swear.structure.ui.konwledge.KnowledgeFragment;
import com.teamsolo.swear.structure.ui.mine.AccountsActivity;
import com.teamsolo.swear.structure.ui.mine.AttentionActivity;
import com.teamsolo.swear.structure.ui.mine.ChildChooseActivity;
import com.teamsolo.swear.structure.ui.mine.OrdersActivity;
import com.teamsolo.swear.structure.ui.mine.OrdersFragment;
import com.teamsolo.swear.structure.ui.news.NewsFragment;
import com.teamsolo.swear.structure.ui.training.TrainingFragment;
import com.teamsolo.swear.structure.util.UserHelper;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Subscriber;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.teamsolo.swear.structure.util.UserHelper.getChildren;

/**
 * description: main page
 * author: Melody
 * date: 2016/8/28
 * version: 0.0.0.1
 */
public class MainActivity extends HandlerActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        BaseFragment.OnFragmentInteractionListener,
        SwipeRefreshLayout.OnRefreshListener,
        Appendable {

    private static final int CHILD_CHOOSE_REQUEST_CODE = 847;

    private static final int FRAG_INDEX = 0, FRAG_TRAINING = 1, FRAG_NEWS = 2, FRAG_NLG = 3;

    private FloatingActionButton mFab;

    private DrawerLayout mDrawer;

    private NavigationView mNavigationView;

    private TabLayout mTabLayout;

    private BottomNavigationBar mBottomNavigationBar;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    private SimpleDraweeView mPortraitImage, mChildPortraitImage;

    private TextView mUsernameText, mChildText, mSchoolText;

    private User mUser;

    private boolean isWaitingForSecondBackPress;

    private FragmentManager fragmentManager;

    private SparseArray<Fragment> fragments = new SparseArray<>();

    private Fragment currentFragment;

    private Subscriber<ChildChooseResp> childChooseRespSubscriber;

    private boolean hasInit;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getBundle(getIntent());
        initViews();
        bindListeners();

        handler.sendEmptyMessage(0);
    }

    @Override
    protected void handleMessage(HandlerActivity activity, Message msg) {
        switch (msg.what) {
            case 0:
                // choose child
                new Thread(this::startChooseChild).start();
                break;

            case 1:
                // init school fragment
                initFragment();
                break;

            case 2:
                // invalidate ui about user
                new Thread(this::invalidateUIAboutUser).start();
                break;

            case 3:
                // invalidate ui about child
                new Thread(this::invalidateUIAboutChild).start();
                break;

            case 4:
                // request and download load image
                new Thread(this::requestLoadPic).start();
                break;

            case 5:
                hasInit = true;
                break;
        }
    }

    @Override
    protected void getBundle(@NotNull Intent intent) {
        fragmentManager = getSupportFragmentManager();
    }

    @Override
    protected void initViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setOnClickListener(v -> {
            if (currentFragment instanceof ScrollAble)
                ((ScrollAble) currentFragment).scroll(Uri.parse("scroll?top=true"));
        });
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

        mBottomNavigationBar = (BottomNavigationBar) findViewById(R.id.bnb);
        mBottomNavigationBar.setFab(mFab);
        mBottomNavigationBar
                .addItem(new BottomNavigationItem(R.drawable.ic_business_white_24dp, getString(R.string.school_nav)))
                .addItem(new BottomNavigationItem(R.drawable.ic_school_white_24dp, getString(R.string.training_nav)))
                .addItem(new BottomNavigationItem(R.drawable.ic_receipt_white_24dp, getString(R.string.news_nav)))
                .addItem(new BottomNavigationItem(R.drawable.ic_local_library_white_24dp, getString(R.string.nlg_nav)))
                .initialise();

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe);
        mSwipeRefreshLayout.setColorSchemeColors(
                Color.parseColor("#F44336"),
                Color.parseColor("#FF5722"),
                Color.parseColor("#CDDC39"),
                Color.parseColor("#4CAF50"));
    }

    @Override
    protected void bindListeners() {
        mFab.setOnClickListener(view -> {
            if (!hasInit) return;

            boolean isSearch = (boolean) view.getTag();
            if (!isSearch) startChooseChild();
            else {
                if (currentFragment instanceof SearchAble)
                    ((SearchAble) currentFragment).search(null);
            }
        });

        mNavigationView.setNavigationItemSelectedListener(this);

        mPortraitImage.setOnClickListener(view -> {
            if (!hasInit) return;
            // TODO:
        });

        mChildPortraitImage.setOnClickListener(view -> {
            if (!hasInit) return;
            startChooseChild();
        });

        mBottomNavigationBar.setTabSelectedListener(new BottomNavigationBar.OnTabSelectedListener() {
            @Override
            public void onTabSelected(int position) {
                if (!hasInit) return;

                ActionBar actionBar = getSupportActionBar();

                switch (position) {
                    case 0:
                        if (actionBar != null) actionBar.setTitle(R.string.app_name);

                        if (fragments.get(FRAG_INDEX) == null) {
                            Fragment fragmentSchool = OrdersFragment.newInstance(FRAG_INDEX);
                            fragments.append(FRAG_INDEX, fragmentSchool);
                            fragmentManager.beginTransaction()
                                    .add(R.id.content, fragments.get(FRAG_INDEX), String.valueOf(FRAG_INDEX))
                                    .show(fragmentSchool)
                                    .hide(currentFragment)
                                    .commit();
                            currentFragment = fragmentSchool;
                        } else {
                            Fragment fragmentSchool = fragments.get(FRAG_INDEX);
                            fragmentManager.beginTransaction()
                                    .show(fragmentSchool)
                                    .hide(currentFragment)
                                    .commit();
                            currentFragment = fragmentSchool;
                        }

                        if (currentFragment instanceof IndexFragment)
                            ((IndexFragment) currentFragment).onPageSelected();
                        break;

                    case 1:
                        if (actionBar != null) actionBar.setTitle(R.string.training_nav);

                        mFab.setTag(true);
                        mFab.setImageResource(R.drawable.ic_search_white_24dp);
                        mFab.setVisibility(VISIBLE);

                        if (fragments.get(FRAG_TRAINING) == null) {
                            Fragment fragmentTraining = TrainingFragment.newInstance();
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

                        mFab.setTag(false);
                        mFab.setImageResource(R.drawable.ic_group_white_24dp);
                        mFab.setVisibility(GONE);

                        if (fragments.get(FRAG_NEWS) == null) {
                            Fragment fragmentNews = NewsFragment.newInstance();
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

                        mFab.setTag(true);
                        mFab.setImageResource(R.drawable.ic_search_white_24dp);
                        mFab.setVisibility(VISIBLE);

                        if (fragments.get(FRAG_NLG) == null) {
                            Fragment fragmentNlg = KnowledgeFragment.newInstance();
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

                mTabLayout.setVisibility(position == 0 ? VISIBLE : GONE);

                if (position < fragments.size()) {
                    fragmentManager.beginTransaction()
                            .hide(currentFragment)
                            .show(fragments.get(position))
                            .commit();

                    currentFragment = fragments.get(position);
                }

                if (mSwipeRefreshLayout.isRefreshing()) mSwipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onTabUnselected(int position) {

            }

            @Override
            public void onTabReselected(int position) {
                if (!hasInit) return;

                if (currentFragment instanceof ScrollAble)
                    ((ScrollAble) currentFragment).scroll(Uri.parse("scroll?top=true"));
            }
        });

        mSwipeRefreshLayout.setOnRefreshListener(this);
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
                if (!mSwipeRefreshLayout.isRefreshing()) mSwipeRefreshLayout.setRefreshing(true);
                if (currentFragment instanceof Refreshable)
                    ((Refreshable) currentFragment).refresh(null);
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
                startActivity(new Intent(mContext, AttentionActivity.class));
                break;

            case R.id.nav_collection:
                break;

            case R.id.nav_history:
                break;

            case R.id.nav_download:
                break;

            case R.id.nav_account:
                startActivity(new Intent(mContext, AccountsActivity.class));
                break;

            case R.id.nav_member:
                WebLink webLinkMem = new WebLink();
                webLinkMem.title = getString(R.string.nav_member);
                webLinkMem.forwardUrl = NetConst.HTTP + NetConst.getBaseHttpUrl() + NetConst.PATH_PRE + NetConst.MEMBER_INDEX_URL;

                Intent intentMem = new Intent(mContext, WebLinkActivity.class);
                intentMem.putExtra("link", webLinkMem);
                startActivity(intentMem);

                break;

            case R.id.nav_bonus_point:
                WebLink webLinkBP = new WebLink();
                webLinkBP.title = getString(R.string.nav_bonus_point);
                webLinkBP.forwardUrl = NetConst.HTTP + NetConst.getBaseHttpUrl() + NetConst.PATH_PRE + NetConst.BONUS_POINT_URL;

                Intent intentBP = new Intent(mContext, WebLinkActivity.class);
                intentBP.putExtra("link", webLinkBP);
                startActivity(intentBP);
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
                RetrofitConfig.clearCookies();
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

    private void startChooseChild() {
        List<Child> children = getChildren(mContext);
        if (children != null && children.size() > 1) {
            if (!hasInit) {
                long lastStudentId = UserHelper.getLastChildId(mContext);
                if (lastStudentId > 0)
                    for (Child child :
                            children) {
                        if (child.studentId == lastStudentId) {
                            chooseChild(child);
                            return;
                        }
                    }
            }

            startActivityForResult(new Intent(mContext, ChildChooseActivity.class), CHILD_CHOOSE_REQUEST_CODE);
        } else if (children != null && children.size() == 1) {
            if (!hasInit) chooseChild(children.get(0));
        } else handler.sendEmptyMessage(1);
    }

    private void chooseChild(final Child child) {
        if (child.studentId < 0) handler.sendEmptyMessage(1);
        else {
            Map<String, String> paras = new HashMap<>();
            paras.put("studentId", String.valueOf(child.studentId));

            childChooseRespSubscriber = BaseHttpUrlRequests.getInstance().getChildInfo(paras, new Subscriber<ChildChooseResp>() {
                @Override
                public void onCompleted() {
                    handler.sendEmptyMessage(1);
                }

                @Override
                public void onError(Throwable e) {
                    toast(RetrofitConfig.handleReqError(e));
                    handler.sendEmptyMessage(1);
                }

                @Override
                public void onNext(ChildChooseResp childChooseResp) {
                    if (!RetrofitConfig.handleResp(childChooseResp, mContext))
                        toast(childChooseResp.message);
                    else UserHelper.saveChildInfo(child.merge(childChooseResp), mContext);
                }
            });
        }
    }

    private void initFragment() {
        if (!hasInit) {
            fragments.append(FRAG_INDEX, IndexFragment.newInstance().setInterActViews(mTabLayout, mFab));
            mFab.setTag(false);

            fragmentManager.beginTransaction()
                    .add(R.id.content, fragments.get(FRAG_INDEX), String.valueOf(FRAG_INDEX))
                    .show(fragments.get(FRAG_INDEX))
                    .commit();
            currentFragment = fragments.get(FRAG_INDEX);
        }

        handler.sendEmptyMessage(2);
    }

    private void invalidateUIAboutUser() {
        if (hasInit) {
            handler.sendEmptyMessage(3);
            return;
        }

        if (mUser == null) mUser = UserHelper.getUser(mContext);

        handler.post(() -> {
            if (mUser != null) {
                if (!TextUtils.isEmpty(mUser.parentsName)) mUsernameText.setText(mUser.parentsName);
                else mUsernameText.setText(R.string.app_name);

                if (!TextUtils.isEmpty(mUser.parentPath)) {
                    try {
                        mPortraitImage.setImageURI(Uri.parse(mUser.parentPath));
                    } catch (Exception e) {
                        mPortraitImage.setImageURI(Uri.parse("http://error"));
                    }
                } else mPortraitImage.setImageURI(Uri.parse("http://error"));
            } else {
                mUsernameText.setText(R.string.app_name);
                mPortraitImage.setImageURI(Uri.parse("http://error"));
            }

            List<Child> children = UserHelper.getChildren(mContext);
            mFab.setVisibility(children == null || children.size() <= 1 ? GONE : VISIBLE);

            mChildPortraitImage.setVisibility(children == null || children.size() < 1 ? GONE : VISIBLE);
            mChildText.setVisibility(children == null || children.size() < 1 ? GONE : VISIBLE);

            handler.sendEmptyMessage(3);
        });
    }

    private void invalidateUIAboutChild() {
        final Child child = UserHelper.getChild(mContext);

        handler.post(() -> {
            if (child != null) {
                StringBuilder builder = new StringBuilder();

                if (!TextUtils.isEmpty(child.studentName)) builder.append(child.studentName);
                else builder.append(getString(R.string.app_name));

                if (!TextUtils.isEmpty(child.appellation))
                    builder.append("(").append(child.appellation).append(")");

                mChildText.setText(builder.toString());

                if (!TextUtils.isEmpty(child.schoolName)) mSchoolText.setText(child.schoolName);
                else mSchoolText.setText(R.string.load_company);

                try {
                    mChildPortraitImage.setImageURI(Uri.parse(child.portraitPath));
                } catch (Exception e) {
                    mChildPortraitImage.setImageURI(Uri.parse("http://error"));
                }
            } else {
                mChildText.setText(R.string.app_name);
                mSchoolText.setText(R.string.load_company);
            }
        });
        handler.sendEmptyMessage(4);
    }

    private void requestLoadPic() {
        handler.sendEmptyMessage(5);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        if ("refresh".equals(uri.getPath())) {
            if (uri.getBooleanQueryParameter("ready", false)) {
                if (mSwipeRefreshLayout.isRefreshing()) mSwipeRefreshLayout.setRefreshing(false);
            } else if (uri.getBooleanQueryParameter("start", false))
                if (!mSwipeRefreshLayout.isRefreshing()) mSwipeRefreshLayout.setRefreshing(true);
        }
    }

    @Override
    public void append(Uri uri) {
        if (currentFragment instanceof Appendable) ((Appendable) currentFragment).append(null);
    }

    @Override
    public void onRefresh() {
        if (!hasInit) return;

        if (currentFragment instanceof Refreshable) ((Refreshable) currentFragment).refresh(null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CHILD_CHOOSE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) chooseChild(data.getParcelableExtra("child"));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (childChooseRespSubscriber != null && !childChooseRespSubscriber.isUnsubscribed())
            childChooseRespSubscriber.unsubscribe();
    }
}
