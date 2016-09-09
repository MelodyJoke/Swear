package com.teamsolo.swear.structure.ui.common;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.view.SimpleDraweeView;
import com.teamsolo.base.template.activity.BaseActivity;
import com.teamsolo.base.template.fragment.BaseFragment;
import com.teamsolo.swear.R;
import com.teamsolo.swear.foundation.ui.adapter.StatePagerAdapter;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * description: gallery page to display pictures
 * author: Melody
 * date: 2016/9/8
 * version: 0.0.0.1
 */
public class GalleryActivity extends BaseActivity implements BaseFragment.OnFragmentInteractionListener {

    private List<String> mList;

    private List<Fragment> mFragments = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        getBundle(getIntent());
        initViews();
        bindListeners();
    }

    @Override
    protected void getBundle(@NotNull Intent intent) {
        mList = intent.getStringArrayListExtra("urls");
    }

    @Override
    protected void initViews() {
        ViewPager mContainer = (ViewPager) findViewById(R.id.container);

        for (int i = 0; i < mList.size(); i++)
            mFragments.add(ImageFragment.newInstance(mList.get(i), i));

        mContainer.setAdapter(new StatePagerAdapter(getSupportFragmentManager(), new String[0], mFragments));
    }

    @Override
    protected void bindListeners() {

    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        int position = Integer.parseInt(uri.getQueryParameter("position"));
        if (position == 0 || position == mList.size() - 1) onBackPressed();
    }

    public static class ImageFragment extends BaseFragment {

        private SimpleDraweeView mImageView;

        private String url;

        private int position;

        public static ImageFragment newInstance(String url, int position) {
            ImageFragment imageFragment = new ImageFragment();
            Bundle args = new Bundle();
            args.putString("url", url);
            args.putInt("position", position);
            imageFragment.setArguments(args);

            return imageFragment;
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            System.out.println("onCreateView: " + url);
            setContentView(inflater, R.layout.fragment_image, container);
            initViews();
            bindListeners();

            return mLayoutView;
        }

        @Override
        protected void getBundle(@NotNull Bundle bundle) {
            url = bundle.getString("url");
            position = bundle.getInt("position");
        }

        @Override
        protected void initViews() {
            mImageView = (SimpleDraweeView) findViewById(R.id.image);
            try {
                mImageView.setImageURI(Uri.parse(url));
            } catch (Exception e) {
                mImageView.setImageURI(Uri.parse("http://error"));
            }
        }

        @Override
        protected void bindListeners() {
            mImageView.setOnClickListener(v -> onInteraction(Uri.parse("action?position=" + position)));

            mImageView.setOnLongClickListener(v -> {
                // TODO: save or share
                return true;
            });
        }
    }
}
