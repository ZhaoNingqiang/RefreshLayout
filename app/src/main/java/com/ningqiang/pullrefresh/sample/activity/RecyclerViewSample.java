package com.ningqiang.pullrefresh.sample.activity;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ListView;

import com.ningqiang.pullrefresh.R;
import com.ningqiang.pullrefresh.adapter.ColorDecoration;
import com.ningqiang.pullrefresh.sample.adapter.ListViewAdaper;
import com.ningqiang.pullrefresh.sample.adapter.RecyclerViewAdapter;
import com.ningqiang.pullrefresh.widget.RefreshLayout;

import java.util.ArrayList;

public class RecyclerViewSample extends AppCompatActivity {
    private static final int WHAT_LOAD_DATA_OVER = 1;
    private RefreshLayout refreshlayout;
    private RecyclerView recyclerView;
    private RecyclerViewAdapter mAdapter;


    ArrayList<String> mData;
    int refreshCount = 0;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case WHAT_LOAD_DATA_OVER://模拟数据加载完成
                    if (mData == null) {
                        initData();
                        mAdapter.resetData(mData);
                    } else {
                        refreshCount++;
                        mData.add(0, "load refresh data " + refreshCount);
                        mAdapter.resetData(mData);
                    }
                    refreshlayout.setRefresh(false);
                    break;
            }
        }
    };

    private void initData() {
        mData = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            mData.add("" + i);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_view_sample);
        refreshlayout = (RefreshLayout) findViewById(R.id.refreshlayout);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        mAdapter = new RecyclerViewAdapter();
        recyclerView.setAdapter(mAdapter);

        ColorDecoration colorDecoration = new ColorDecoration(this, 1, R.color.recycler_divider);
        recyclerView.addItemDecoration(colorDecoration);

        refreshlayout.setOnRefreshListener(new RefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //模拟加载数据
                mHandler.sendEmptyMessageDelayed(WHAT_LOAD_DATA_OVER, 3000);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshlayout.setRefresh(true);
    }


}
