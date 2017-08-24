package com.ningqiang.pullrefresh.sample.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ningqiang.pullrefresh.R;
import com.ningqiang.pullrefresh.adapter.BaseRecyclerAdapter;
import com.ningqiang.pullrefresh.adapter.BaseViewHolder;

import java.util.ArrayList;

/**
 * Created by ningqiang on 2017/8/24.
 */

public class RecyclerViewAdapter extends BaseRecyclerAdapter<String,RecyclerViewAdapter.ViewHolder> {
    @Override
    protected ViewHolder onCreateInnerViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycler_view,parent,false);
        return new ViewHolder(itemView);
    }

    @Override
    protected void onBinderInnerViewHolder(BaseViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        String item = getItem(position);
        viewHolder.tv_content.setText(item);
    }


    static class ViewHolder extends BaseViewHolder{

        TextView tv_content;
        public ViewHolder(View itemView) {
            super(itemView);
            tv_content = findViewBy(R.id.tv_content);
        }
    }
}
