package com.ningqiang.pullrefresh.sample.activity;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.ningqiang.pullrefresh.R;
import com.ningqiang.pullrefresh.sample.adapter.ListViewAdaper;
import com.ningqiang.pullrefresh.widget.RefreshLayout;

import java.util.ArrayList;

public class ListViewSample extends AppCompatActivity {
    private static final int WHAT_LOAD_DATA_OVER = 1;
    private RefreshLayout refreshlayout;
    private ListView listview;
    private ListViewAdaper mAdapter;


    ArrayList<String> mData;
    int refreshCount = 0;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case WHAT_LOAD_DATA_OVER://模拟数据加载完成
                    if (mData == null){
                        initData();
                        mAdapter.setData(mData);
                    }else {
                        refreshCount++;
                        mData.add(0,"load refresh data "+refreshCount);
                        mAdapter.setData(mData);
                    }
                    //数据加载完成，设置Refresh状态
                    refreshlayout.setRefresh(false);
                    break;
            }
        }
    };

    private void initData() {
        mData = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            mData.add(""+i);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view_sample);
        refreshlayout = (RefreshLayout) findViewById(R.id.refreshlayout);
        listview = (ListView) findViewById(R.id.listview);
        mAdapter = new ListViewAdaper();
        listview.setAdapter(mAdapter);

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
        //调用refreshlayout.setRefresh(true);方法将会回调OnRefreshListener的onRefresh方法，在onRefresh方法中刷新数据
        refreshlayout.setRefresh(true);
    }
}
