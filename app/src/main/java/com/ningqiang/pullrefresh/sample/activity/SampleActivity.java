package com.ningqiang.pullrefresh.sample.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.ningqiang.pullrefresh.R;

public class SampleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);
    }

    public void openListViewSample(View view) {
        Intent intent = new Intent(this,ListViewSample.class);
        startActivity(intent);
    }

    public void openRecyclerViewSample(View view) {
        Intent intent = new Intent(this,RecyclerViewSample.class);
        startActivity(intent);
    }
}
