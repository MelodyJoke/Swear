package com.teamsolo.swear.structure.ui.mine;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.teamsolo.base.template.activity.HandlerActivity;
import com.teamsolo.swear.R;
import com.teamsolo.swear.foundation.bean.Child;
import com.teamsolo.swear.structure.ui.mine.adapter.ChildAdapter;
import com.teamsolo.swear.structure.util.UserHelper;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * description: child choose page
 * author: Melody
 * date: 2016/9/2
 * version: 0.0.0.1
 */
public class ChildChooseActivity extends HandlerActivity {

    private ChildAdapter mAdapter;

    private List<Child> mList = new ArrayList<>();

    private Child current;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_choose);

        getBundle(getIntent());
        initViews();
        bindListeners();

        new Thread(this::prepare).start();
    }

    @Override
    protected void getBundle(@NotNull Intent intent) {

    }

    @Override
    protected void initViews() {
        RecyclerView mListView = (RecyclerView) findViewById(R.id.listView);

        mListView.setHasFixedSize(true);
        mListView.setItemAnimator(new DefaultItemAnimator());
        mListView.setLayoutManager(new LinearLayoutManager(mContext));

        mAdapter = new ChildAdapter(mContext, mList);
        mListView.setAdapter(mAdapter);
    }

    @Override
    protected void bindListeners() {
        mAdapter.setOnItemClickListener((v, item) -> {
            if (item != null) {
                if (current == null || current.studentId != item.studentId) {
                    Intent data = new Intent();
                    data.putExtra("child", item);
                    setResult(RESULT_OK, data);
                }
                finish();
            }
        });

        findViewById(R.id.activity_child_choose).setOnClickListener(v -> onBackPressed());
    }

    private void prepare() {
        List<Child> children = UserHelper.getChildren(mContext);
        current = UserHelper.getChild(mContext);

        if (children == null || children.size() <= 1) return;

        if (current != null && current.studentId > 0) {
            for (Child child :
                    children)
                child.isChecked = child.studentId == current.studentId;
        }
        mList.addAll(children);
        handler.post(() -> mAdapter.notifyDataSetChanged());
    }

    @Override
    public void onBackPressed() {
        if (current == null && mList != null && mList.size() > 0) {
            Intent data = new Intent();
            data.putExtra("child", mList.get(0));
            setResult(RESULT_OK, data);
            finish();
        } else super.onBackPressed();
    }

    @Override
    protected void handleMessage(HandlerActivity activity, Message msg) {

    }
}
