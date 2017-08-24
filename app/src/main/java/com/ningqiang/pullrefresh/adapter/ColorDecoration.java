package com.ningqiang.pullrefresh.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.ColorRes;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Create on 2017/7/26/下午6:00
 *
 * @author 赵凝强
 * @version 1.0.0
 */

public class ColorDecoration extends SampleDecoration {
    private Paint paint;
    private int paddingLeft;
    private int paddingRight;
    private final Rect mBounds = new Rect();

    public ColorDecoration(Context context, int mOrientation, int topOffset, int middleOffset, int bottomOffset, @ColorRes int colorRes, int paddingLeft, int paddingRight) {
        super(context,mOrientation, topOffset, middleOffset, bottomOffset);
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        int paintColor = context.getResources().getColor(colorRes);
        paint.setColor(paintColor);
        this.paddingLeft = paddingLeft;
        this.paddingRight = paddingRight;
    }


    public ColorDecoration(Context context,int middleOffset, @ColorRes int colorRes) {
        this(context,VERTICAL, 0, middleOffset, 0, colorRes, 0, 0);
    }

    public void setPaddingLeft(int paddingLeft) {
        this.paddingLeft = dpToPx( paddingLeft);
    }

    public void setPaddingRight(int paddingRight) {
        this.paddingRight = dpToPx( paddingRight);
    }

    @ColorRes
    int backColorRes;
    Paint backPaint;
    Rect backRect;

    public void setBackColor(Context context,@ColorRes int colorRes) {
        this.backColorRes = colorRes;
        backPaint = new Paint();
        int backColor = context.getResources().getColor(backColorRes);
        backPaint.setColor(backColor);
    }


    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        if (parent.getLayoutManager() == null) {
            return;
        }

        if (backColorRes != 0) {
            if (backRect == null) {
                backRect = new Rect(parent.getLeft() + leftOffset, 0, parent.getRight() - rightOffset, parent.getBottom());
            }
            c.drawRect(backRect, backPaint);
        }

        RecyclerView.Adapter adapter = parent.getAdapter();
        if (adapter instanceof BaseRecyclerAdapter) {
            BaseRecyclerAdapter bra = (BaseRecyclerAdapter) adapter;
            if (mOrientation == VERTICAL) {
                drawVertical(c, parent, bra.getHeaderCount() + bra.getRealItemCount() - 1);
            } else {
                drawHorizontal(c, parent, bra.getHeaderCount() + bra.getRealItemCount() - 1);
            }
        } else {
            if (mOrientation == VERTICAL) {
                drawVertical(c, parent, adapter.getItemCount() - 1);
            } else {
                drawHorizontal(c, parent, adapter.getItemCount() - 1);
            }
        }

    }


    @SuppressLint("NewApi")
    private void drawVertical(Canvas canvas, RecyclerView parent, int lastPosition) {
        canvas.save();
        final int left;
        final int right;
        if (parent.getClipToPadding()) {
            left = parent.getPaddingLeft() + paddingLeft + leftOffset;
            right = parent.getWidth() - parent.getPaddingRight() - paddingRight - rightOffset;
            canvas.clipRect(left, parent.getPaddingTop(), right,
                    parent.getHeight() - parent.getPaddingBottom());
        } else {
            left = paddingLeft + leftOffset;
            right = parent.getWidth() - paddingRight - rightOffset;
        }

        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            boolean isDraw = true;
            if (!showBottomDivider) {
                int layoutPosition = ((RecyclerView.LayoutParams) child.getLayoutParams()).getViewLayoutPosition();
                isDraw = layoutPosition != lastPosition;
            }

            if (isDraw) {
                parent.getDecoratedBoundsWithMargins(child, mBounds);
                final int bottom = mBounds.bottom + Math.round(ViewCompat.getTranslationY(child));
                final int top = bottom - middleOffset;
                canvas.drawRect(left, top, right, bottom, paint);
            }
        }
        canvas.restore();
    }

    @SuppressLint("NewApi")
    private void drawHorizontal(Canvas canvas, RecyclerView parent, int lastPosition) {
        canvas.save();
        final int top;
        final int bottom;
        if (parent.getClipToPadding()) {
            top = parent.getPaddingTop() + paddingLeft + leftOffset;
            bottom = parent.getHeight() - parent.getPaddingBottom() - paddingRight - rightOffset;
            canvas.clipRect(parent.getPaddingLeft(), top,
                    parent.getWidth() - parent.getPaddingRight(), bottom);
        } else {
            top = paddingLeft + leftOffset;
            bottom = parent.getHeight() - paddingRight - rightOffset;
        }

        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            boolean isDraw = true;
            if (!showBottomDivider) {
                int layoutPosition = ((RecyclerView.LayoutParams) child.getLayoutParams()).getViewLayoutPosition();
                isDraw = layoutPosition != lastPosition;
            }

            if (isDraw) {
                parent.getLayoutManager().getDecoratedBoundsWithMargins(child, mBounds);
                final int right = mBounds.right + Math.round(ViewCompat.getTranslationX(child));
                final int left = right - middleOffset;
                canvas.drawRect(left, top, right, bottom, paint);
            }
        }
        canvas.restore();
    }

    private boolean showBottomDivider;

    public void setShowBottomDivider(boolean showBottomDivider) {
        this.showBottomDivider = showBottomDivider;
    }
}
