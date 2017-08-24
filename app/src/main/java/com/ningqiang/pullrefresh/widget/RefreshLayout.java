package com.ningqiang.pullrefresh.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Scroller;
import android.widget.TextView;

import com.ningqiang.pullrefresh.R;

/**
 * Create on 2017/7/28/下午2:51
 *
 * @author 赵凝强
 * @version 1.0.0
 */
public class RefreshLayout extends ViewGroup {
    private static final float FRICTION = .5f;

    private static final int TITLE_HEIGHT = 40;

    private static final int PULL_TO_REFRESH = 0x0;
    private static final int RELEASE_TO_REFRESH = 0x1;
    private static final int REFRESHING = 0x2;

    private View mTargetView;
    private TextView mHeaderView;
    private Scroller mScroller;

    private int mTouchSlop;

    private int mHeaderHeight;


    private float lastMotionY, initMotionY, lastMotionX;
    private boolean isBeingDragged;

    private int state = PULL_TO_REFRESH;

    CharSequence pullToRefreshText;
    CharSequence releaseToRefreshText;
    CharSequence refreshingText;


    public RefreshLayout(Context context) {
        this(context, null);
    }

    public RefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        mHeaderHeight = (int) (TITLE_HEIGHT * metrics.density);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mScroller = new Scroller(context);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RefreshLayout);
        mHeaderHeight = typedArray.getDimensionPixelSize(R.styleable.RefreshLayout_headerHeight, mHeaderHeight);

        pullToRefreshText = typedArray.getText(R.styleable.RefreshLayout_pullToRefreshText);
        releaseToRefreshText = typedArray.getText(R.styleable.RefreshLayout_releaseToRefreshText);
        refreshingText = typedArray.getText(R.styleable.RefreshLayout_refreshingText);
        createTitleView(typedArray);

        typedArray.recycle();
    }

    private void createTitleView(TypedArray typedArray) {
        int textColor = typedArray.getColor(R.styleable.RefreshLayout_headerTextColor, Color.BLACK);
        float textSize = typedArray.getDimensionPixelSize(R.styleable.RefreshLayout_headerTextSize, 15);
        int backgroundColor = typedArray.getColor(R.styleable.RefreshLayout_headerBackgroundColor, Color.WHITE);
        mHeaderView = new TextView(getContext());
        mHeaderView.setGravity(Gravity.CENTER);
        mHeaderView.setTextColor(textColor);
        mHeaderView.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSize);
        mHeaderView.setBackgroundColor(backgroundColor);
        mHeaderView.setText(pullToRefreshText);
        addView(mHeaderView);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (getChildCount() == 0) {
            return;
        }
        if (mTargetView == null) {
            ensureTarget();
        }
        if (mTargetView == null) {
            return;
        }
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        View target = mTargetView;
        int targetLeft = getPaddingLeft();
        int targetTop = getPaddingTop();
        int targetRight = width - getPaddingRight();
        int targetBottom = height - getPaddingBottom();
        target.layout(targetLeft, targetTop, targetRight, targetBottom);

        int titleHeight = mHeaderView.getMeasuredHeight();
        mHeaderView.layout(targetLeft, -titleHeight, targetRight, 0);
    }

    private void ensureTarget() {
        // Don't bother getting the parent height if the parent hasn't been laid
        // out yet.
        if (mTargetView == null) {
            for (int i = 0; i < getChildCount(); i++) {
                View child = getChildAt(i);
                if (!child.equals(mHeaderView)) {
                    mTargetView = child;
                    break;
                }
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mTargetView == null) {
            ensureTarget();
        }
        if (mTargetView == null) {
            return;
        }
        int innerWidth = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
        int innerHeight = getMeasuredHeight() - getPaddingTop() - getPaddingBottom();

        mTargetView.measure(MeasureSpec.makeMeasureSpec(innerWidth, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(innerHeight, MeasureSpec.EXACTLY));

        mHeaderView.measure(MeasureSpec.makeMeasureSpec(innerWidth, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(mHeaderHeight, MeasureSpec.EXACTLY));
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        ensureTarget();
        if (isRefreshing() || canChildScrollUp()) {
            return false;
        }
        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                initMotionY = lastMotionY = ev.getY();
                lastMotionX = ev.getX();
                isBeingDragged = false;
                break;
            case MotionEvent.ACTION_MOVE:
                final float y = ev.getY(), x = ev.getX(), diffX, diffY;
                diffX = x - lastMotionX;
                diffY = y - lastMotionY;
                float absX = Math.abs(diffX);
                float absY = Math.abs(diffY);
                if (absY > mTouchSlop && absY > absX && diffY >= 0/*下拉拦截*/) {
                    lastMotionY = y;
                    lastMotionX = x;
                    isBeingDragged = true;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                isBeingDragged = false;
                break;
        }
        return isBeingDragged;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isRefreshing()) {
            return true;
        }
        if (canChildScrollUp()) {
            return false;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                initMotionY = lastMotionY = event.getY();
                lastMotionX = event.getX();
                return true;
            case MotionEvent.ACTION_MOVE:
                if (isBeingDragged) {
                    lastMotionY = event.getY();
                    lastMotionX = event.getX();
                    final int newHeight = Math.round(Math.min(initMotionY - lastMotionY, 0) * FRICTION);
                    scrollTo(0, newHeight);
                    if (newHeight != 0) {
                        if (state == PULL_TO_REFRESH && Math.abs(newHeight) > mHeaderHeight) {
                            state = RELEASE_TO_REFRESH;
                            mHeaderView.setText(releaseToRefreshText);
                        } else if (state == RELEASE_TO_REFRESH && Math.abs(newHeight) <= mHeaderHeight) {
                            state = PULL_TO_REFRESH;
                            mHeaderView.setText(pullToRefreshText);
                        }
                    }
                    return true;
                }

                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (isBeingDragged) {
                    isBeingDragged = false;
                    if (state == RELEASE_TO_REFRESH) {
                        doRefresh(true);
                    } else {
                        smoothScrollTo(0);
                    }
                    return true;
                }
                break;
        }
        return false;
    }


    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            invalidate();
        }
    }

    private void smoothScrollTo(int y) {
        mScroller.startScroll(0, getScrollY(), 0, -getScrollY() - y);
        postInvalidate();
    }

    @SuppressLint("ObsoleteSdkInt")
    public boolean canChildScrollUp() {
        if (android.os.Build.VERSION.SDK_INT < 14) {
            if (mTargetView instanceof AbsListView) {
                final AbsListView absListView = (AbsListView) mTargetView;
                return absListView.getChildCount() > 0
                        && (absListView.getFirstVisiblePosition() > 0 || absListView.getChildAt(0)
                        .getTop() < absListView.getPaddingTop());
            } else {
                return ViewCompat.canScrollVertically(mTargetView, -1) || mTargetView.getScrollY() > 0;
            }
        } else {
            return ViewCompat.canScrollVertically(mTargetView, -1);
        }
    }


    private void doRefresh(boolean isScroll) {
        if (!isRefreshing()) {
            state = REFRESHING;
            mHeaderView.setText(refreshingText);
            if (isScroll){
                smoothScrollTo(mHeaderHeight);
            }else {
                scrollTo(0,-mHeaderHeight);
            }

            if (mOnRefreshListener != null) {
                mOnRefreshListener.onRefresh();
            }
        }
    }

    public void setRefresh(boolean refresh) {
        setRefresh(refresh,false);
    }

    private void setRefresh(boolean refresh,boolean isScroll) {
        if (refresh) {
            doRefresh(isScroll);
            isBeingDragged = false;
        } else {
            state = PULL_TO_REFRESH;
            smoothScrollTo(0);
            mHeaderView.setText(pullToRefreshText);
            isBeingDragged = false;
        }
    }

    public final boolean isRefreshing() {
        return state == REFRESHING;
    }

    private OnRefreshListener mOnRefreshListener;

    public void setOnRefreshListener(OnRefreshListener mOnRefreshListener) {
        this.mOnRefreshListener = mOnRefreshListener;
    }

    public interface OnRefreshListener {
        public void onRefresh();
    }
}
