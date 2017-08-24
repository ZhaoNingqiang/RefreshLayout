package com.ningqiang.pullrefresh.adapter;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

/**
 * Create on 2017/7/26/下午6:00
 * recyclerView的ItemDecoration,简单的让item与item之间有间距，适用与LinearLayoutManager布局时。
 *
 * @author 赵凝强
 * @version 1.0.0
 */

public class SampleDecoration extends RecyclerView.ItemDecoration {
    public static final int HORIZONTAL = LinearLayout.HORIZONTAL;
    public static final int VERTICAL = LinearLayout.VERTICAL;

    /**
     * Current orientation. Either {@link #HORIZONTAL} or {@link #VERTICAL}.
     */
    protected int mOrientation;
    /**
     * 横向排列时，列表第一个item(不考虑headerview)距离左边的间距；
     * 竖直排列时，列表第一个item(不考虑headerview)距离顶部的间距
     */
    private int topOffset;
    /**
     * 横向排列时，列表最后一个item(不考虑headerview)距离右边的间距；
     * 竖直排列时，列表最后一个item(不考虑headerview)距离底部的间距
     */
    private int bottomOffset;
    /**
     * 横向排列时，表示上方间距；
     * 竖直排列时，表示左边间距
     */
    protected int leftOffset;
    /**
     * 横向排列时，表示底部间距；
     * 竖直排列时，表示右边间距
     */

    protected int rightOffset;
    /**
     * item与item之间的间隔
     */
    protected int middleOffset;

    private IOffsetCreater offsetCreater;

    private float density;

    public SampleDecoration(Context context, int mOrientation, int topOffset, int middleOffset, int bottomOffset) {
        density = context.getResources().getDisplayMetrics().density;

        this.mOrientation = mOrientation;
        this.topOffset = dpToPx(topOffset);
        this.middleOffset = dpToPx(middleOffset);
        this.bottomOffset = dpToPx(bottomOffset);
    }


    public SampleDecoration(Context context, int topOffset, int middleOffset, int bottomOffset) {
        this(context, VERTICAL, topOffset, middleOffset, bottomOffset);
    }


    public void setOrientation(int orientation) {
        if (orientation != HORIZONTAL && orientation != VERTICAL) {
            throw new IllegalArgumentException(
                    "Invalid orientation. It should be either HORIZONTAL or VERTICAL");
        }
        mOrientation = orientation;
    }

    public void setTopOffset(int topOffset) {
        this.topOffset = dpToPx(topOffset);
    }

    public void setBottomOffset(int bottomOffset) {
        this.bottomOffset = dpToPx(bottomOffset);
    }

    public void setLeftOffset(int leftOffset) {
        this.leftOffset = dpToPx(leftOffset);
    }

    public void setRightOffset(int rightOffset) {
        this.rightOffset = dpToPx(rightOffset);
    }


    private void getItemOffsetVertial(int position, Rect outRect, int lastPosition) {
        if (position < 0) {//headerviews
            outRect.set(0, 0, 0, 0);
        } else if (position == 0) {//first item
            outRect.set(leftOffset, topOffset, rightOffset, middleOffset);
        } else if (position == lastPosition) {//last item
            outRect.set(leftOffset, 0, rightOffset, bottomOffset);
        } else if (position > lastPosition) {//footer views
            outRect.set(0, 0, 0, 0);
        } else {//middle items
            outRect.set(leftOffset, 0, rightOffset, middleOffset);
        }
    }

    private void getItemOffsetHorizontal(int position, Rect outRect, int lastPosition) {
        if (position < 0) {//headerviews
            outRect.set(0, 0, 0, 0);
        } else if (position == 0) {//first item
            outRect.set(topOffset, leftOffset, middleOffset, rightOffset);
        } else if (position == lastPosition) {//last item
            outRect.set(0, leftOffset, bottomOffset, rightOffset);
        } else if (position > lastPosition) {//footer views
            outRect.set(0, 0, 0, 0);
        } else {//middle items
            outRect.set(0, leftOffset, middleOffset, rightOffset);
        }
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {

        int layoutPosition = ((RecyclerView.LayoutParams) view.getLayoutParams()).getViewLayoutPosition();
        RecyclerView.Adapter adapter = parent.getAdapter();
        int itemPosition;
        if (adapter instanceof BaseRecyclerAdapter) {
            BaseRecyclerAdapter bra = (BaseRecyclerAdapter) adapter;
//            Log.d("xxx","layoutPosition = "+layoutPosition);
            if (!bra.isFullSpanType(layoutPosition)) {
                itemPosition = layoutPosition - bra.getHeaderCount();

                if (mOrientation == VERTICAL) {
                    getItemOffsetVertial(itemPosition, outRect, bra.getRealItemCount() - 1);
                } else {
                    getItemOffsetHorizontal(itemPosition, outRect, bra.getRealItemCount() - 1);
                }
            }

        } else {
            itemPosition = layoutPosition;
            if (mOrientation == VERTICAL) {
                getItemOffsetVertial(itemPosition, outRect, adapter.getItemCount() - 1);
            } else {
                getItemOffsetHorizontal(itemPosition, outRect, adapter.getItemCount() - 1);
            }
        }
        if (offsetCreater != null) {
            offsetCreater.customOffset(layoutPosition, outRect);
        }
    }

    public void setOffsetCreater(IOffsetCreater offsetCreater) {
        this.offsetCreater = offsetCreater;
    }


    public interface IOffsetCreater {
        public void customOffset(int position, Rect outRect);

    }

    protected int dpToPx(int dps) {
        return Math.round(density * dps);
    }

}
