package com.ningqiang.pullrefresh.adapter;

import android.support.annotation.LayoutRes;
import android.support.v4.util.SparseArrayCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ningqiang.pullrefresh.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Create on 2017/7/26/下午2:51
 * 为RecyclerView的Adapter增加了addHeadView和addFooterView的方法，item可以更简单的设置点击事件.
 * 继承该adapter的viewType必须小于100000
 *
 * @author 赵凝强
 * @version 1.0.0
 */

public abstract class BaseRecyclerAdapter<T, VH extends BaseViewHolder> extends RecyclerView.Adapter<BaseViewHolder> {

    private static final String TAG = "BaseRecyclerAdapter";
    private static final int TYPE_BASE_HEADER = 100000;
    private static final int TYPE_BASE_FOOTER = 200000;
    protected static final int TYPE_EMPTY_VIEW = 300000;
    private static final int TYPE_LOADING_VIEW = 300001;

    private SparseArrayCompat<View> mHeaderViews = new SparseArrayCompat<>();
    private SparseArrayCompat<View> mFooterViews = new SparseArrayCompat<>();
    private View mEmptyView;
    private
    @LayoutRes
    int mLoadingRes;

    private List<T> mData = new ArrayList<>();


    public void add(T item) {
        mData.add(item);
        notifyItemInserted(mData.size() - 1 + mHeaderViews.size());
    }

    public void add(int position, T item) {
        if (position < 0 || position > mData.size()) {
            Log.e(TAG, "IndexOutOfBounds mData size = " + mData.size() + " insert position = " + position);
            return;
        }
        mData.add(item);
        notifyItemInserted(position + mHeaderViews.size());
    }

    public void addData(List<T> data) {
        if (data != null) {
            mData.addAll(data);
        }
        notifyDataSetChanged();
    }

    public void resetData(List<T> data) {
        mData.clear();
        addData(data);
    }

    public List<T> getData() {
        return mData;
    }

    public void remove(T item) {
        int index = mData.indexOf(item);
        boolean flag = mData.remove(item);
        if (flag) {
            notifyItemRemoved(index + getHeaderCount());
        }
    }

    public void remove(int position) {
        if (position < 0 || position >= mData.size()) {
            Log.e(TAG, "IndexOutOfBounds mData size = " + mData.size() + " remove position = " + position);
            return;
        }
        mData.remove(position);
        notifyItemRemoved(position + getHeaderCount());
    }


    //header
    public void addHeaderView(View headerView) {
        mHeaderViews.put(TYPE_BASE_HEADER + mHeaderViews.size(), headerView);
        notifyDataSetChanged();
    }

    public int getHeaderCount() {
        if (emptyDataShowHeader) {

            return mHeaderViews.size();

        } else {
            if (isEmpty()) {
                return 0;

            } else {
                return mHeaderViews.size();
            }
        }
    }

    //footer
    public void addFooterView(View footerView) {
        mFooterViews.put(TYPE_BASE_FOOTER + mFooterViews.size(), footerView);
        notifyDataSetChanged();
    }

    public int getFooterCount() {
        return mFooterViews.size();
    }

    //empty
    public void setEmptyView(View emptyView) {
        mEmptyView = emptyView;
        notifyDataSetChanged();
    }

    private int getEmptyViewCount() {
        return (isEmpty() && mEmptyView != null) ? 1 : 0;
    }

    //is need loading more
    private boolean needLoadingMore = false;
    //is loading now
    private boolean loading = false;

    public void setNeedLoadingMore(boolean needLoadingMore) {
        this.needLoadingMore = needLoadingMore;
        notifyDataSetChanged();
    }

    private boolean isNeedLoadingMore() {
        return needLoadingMore && !isEmpty();
    }

    public void setLoading(boolean loading) {
        this.loading = loading;
    }

    private int getLoadingViewCount() {
        return isNeedLoadingMore() ? 1 : 0;
    }

    public void setmLoadingRes(@LayoutRes int layout) {
        this.mLoadingRes = layout;
    }

    @Override
    public int getItemViewType(int position) {
        if (position < getHeaderCount()) {//header view

            return mHeaderViews.keyAt(position);
        } else if (position < getHeaderCount() + getEmptyViewCount()) {//empty view

            return TYPE_EMPTY_VIEW;
        } else if (position < getHeaderCount() + getEmptyViewCount() + getRealItemCount()) {// real view

            return getInnerViewType(position - (getHeaderCount() + getEmptyViewCount() + getRealItemCount()));
        } else if (position < getHeaderCount() + getEmptyViewCount() + getRealItemCount() + getLoadingViewCount()) {//loading view

            return TYPE_LOADING_VIEW;
        } else { //footer view
            int footerPosition = position - (getHeaderCount() + getEmptyViewCount() + getRealItemCount() + getLoadingViewCount());
            if (footerPosition > -1) {
                return mFooterViews.keyAt(footerPosition);
            } else {
                return -1;
            }

        }
    }

    @Override
    public int getItemCount() {
        return getHeaderCount()
                + getRealItemCount()
                + getFooterCount()
                + getEmptyViewCount()
                + getLoadingViewCount();
    }


    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        BaseViewHolder vh;
        if (mHeaderViews.get(viewType) != null) {
            vh = new BaseViewHolder(mHeaderViews.get(viewType));
        } else if (mFooterViews.get(viewType) != null) {
            vh = new BaseViewHolder(mFooterViews.get(viewType));
        } else {
            switch (viewType) {
                case TYPE_EMPTY_VIEW:
                    if (getHeaderCount() > 0) {
                        int childCount = parent.getChildCount();
                        int topGap = 0;
                        for (int i = 0; i < childCount; i++) {
                            View child = parent.getChildAt(i);
                            if (child != null && child.getVisibility() != View.GONE) {
                                topGap += child.getMeasuredHeight();
                            }
                        }
                        RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, parent.getMeasuredHeight() - topGap);
                        mEmptyView.setLayoutParams(params);
                    }
                    vh = new BaseViewHolder(mEmptyView);
                    break;
                case TYPE_LOADING_VIEW:
                    View laodingView = LayoutInflater.from(parent.getContext()).inflate(mLoadingRes == 0 ? R.layout.layout_loading_view : mLoadingRes, parent, false);
                    vh = new BaseViewHolder(laodingView);
                    break;
                default:
                    vh = onCreateInnerViewHolder(parent, viewType);
                    dispatchItemListener(vh, viewType);
                    break;
            }

        }
        return vh;
    }

    public T getItem(int position) {
        if (position < 0 || position >= mData.size()) {
            Log.e(TAG, "IndexOutOfBounds mData size = " + mData.size() + " getItem position = " + position);
            return null;
        }
        return mData.get(position);
    }

    protected abstract VH onCreateInnerViewHolder(ViewGroup parent, int viewType);

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        if (position < getHeaderCount()) {//header view
        } else if (position < getHeaderCount() + getEmptyViewCount()) {//empty view
        } else if (position < getHeaderCount() + getEmptyViewCount() + getRealItemCount()) {// real view

            onBinderInnerViewHolder(holder, position - getHeaderCount());

        } else if (position < getHeaderCount() + getEmptyViewCount() + getRealItemCount() + getLoadingViewCount()) {//loading view
        } else { //footer view
        }
    }

    protected abstract void onBinderInnerViewHolder(BaseViewHolder holder, int position);


    protected int getInnerViewType(int position) {
        return 0;
    }

    public boolean isEmpty() {
        return getRealItemCount() == 0;
    }


    public int getRealItemCount() {
        return mData == null ? 0 : mData.size();
    }


    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (needLoadingMore && !loading) {
                    RecyclerView.LayoutManager m = recyclerView.getLayoutManager();
                    if (m instanceof LinearLayoutManager) {
                        LinearLayoutManager lm = (LinearLayoutManager) m;
                        int lastVisibleItemPosition = lm.findLastVisibleItemPosition();
                        if (getItemViewType(lastVisibleItemPosition) == TYPE_LOADING_VIEW) {
                            //loading more
                            loading = true;
                            if (mLoadingMoreListener != null) {
                                mLoadingMoreListener.onLoadingMore();
                            }
                        }
                    }
                }
            }
        });
        RecyclerView.LayoutManager lm = recyclerView.getLayoutManager();
        if (lm instanceof GridLayoutManager) {
            final GridLayoutManager gm = (GridLayoutManager) lm;
            final GridLayoutManager.SpanSizeLookup oldSpanSizeLookup = gm.getSpanSizeLookup();
            gm.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    if (isFullSpanType(position)) {
                        return gm.getSpanCount();
                    } else {
                        return oldSpanSizeLookup != null ? oldSpanSizeLookup.getSpanSize(position) : 1;
                    }
                }
            });

        }
    }

    @Override
    public void onViewAttachedToWindow(BaseViewHolder holder) {
        int position = holder.getLayoutPosition();
        if (isFullSpanType(position)) {
            ViewGroup.LayoutParams params = holder.itemView.getLayoutParams();
            if (params != null && params instanceof StaggeredGridLayoutManager.LayoutParams) {
                StaggeredGridLayoutManager.LayoutParams lp = (StaggeredGridLayoutManager.LayoutParams) params;
                lp.setFullSpan(true);
            }
        }
    }

    public boolean isFullSpanType(int position) {
        return getItemViewType(position) >= TYPE_BASE_HEADER;
    }


    private ArrayList<OnItemClickListener> mOnItemClickListeners = new ArrayList<>();

    private ArrayList<OnItemLongClickListener> mOnItemLongClickListeners = new ArrayList<>();

    public void addOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListeners.add(listener);
    }

    public void removeOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListeners.remove(listener);
    }

    public void addOnItemLongClickListener(OnItemLongClickListener listener) {
        mOnItemLongClickListeners.add(listener);
    }

    public void removeOnItemLongClickListener(OnItemLongClickListener listener) {
        mOnItemLongClickListeners.remove(listener);
    }

    private void dispatchItemListener(final BaseViewHolder vh, final int viewType) {
        if (mOnItemClickListeners != null && mOnItemClickListeners.size() > 0) {
            vh.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (OnItemClickListener l : mOnItemClickListeners) {
                        l.onItemClick(vh.getLayoutPosition() - getHeaderCount(), vh, viewType);
                    }
                }
            });
        }

        if (mOnItemLongClickListeners != null && mOnItemLongClickListeners.size() > 0) {
            vh.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    for (OnItemLongClickListener l : mOnItemLongClickListeners) {
                        l.onItemLongClick(vh.getLayoutPosition() - getHeaderCount(), vh, viewType);
                    }
                    return true;
                }
            });
        }

    }

    public interface OnItemClickListener {
        void onItemClick(int position, BaseViewHolder vh, int viewType);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(int position, BaseViewHolder vh, int viewType);
    }

    private LoadingMoreListener mLoadingMoreListener;


    public void setLoadingMoreListener(LoadingMoreListener loadingMoreListener) {
        this.mLoadingMoreListener = loadingMoreListener;
    }

    public interface LoadingMoreListener {
        void onLoadingMore();
    }

    private boolean emptyDataShowHeader = true;

    public void emptyDataShowHeader(boolean emptyDataShowHeader) {
        this.emptyDataShowHeader = emptyDataShowHeader;
    }
}
