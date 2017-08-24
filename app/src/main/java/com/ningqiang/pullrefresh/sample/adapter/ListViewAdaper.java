package com.ningqiang.pullrefresh.sample.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ningqiang.pullrefresh.R;

import java.util.ArrayList;

/**
 * Created by ningqiang on 2017/8/24.
 */

public class ListViewAdaper extends BaseAdapter {

    ArrayList<String> data;

    public void setData(ArrayList<String> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return data == null ? 0 : data.size();
    }

    @Override
    public String getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView =  LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_view,parent,false);
        TextView tv_content = (TextView) convertView.findViewById(R.id.tv_content);

        String item = getItem(position);
        tv_content.setText(item);
        return convertView;
    }
}
