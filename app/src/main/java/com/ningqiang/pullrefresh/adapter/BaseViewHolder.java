package com.ningqiang.pullrefresh.adapter;

import android.support.annotation.IdRes;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.View;

/**
 * Create on 2017/7/26/下午2:58
 *
 * @author 赵凝强
 * @version 1.0.0
 */

public class BaseViewHolder extends ViewHolder {
    public BaseViewHolder(View itemView) {
        super(itemView);
    }

    protected <V extends View> V findViewBy(@IdRes int id) {
        return (V) itemView.findViewById(id);
    }
}
