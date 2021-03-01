package com.yellowzero.listen.view;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

public class VerticalLayoutManager extends LinearLayoutManager implements RecyclerView.OnChildAttachStateChangeListener {
    private int drift;//位移，用来判断移动方向
    private PagerSnapHelper pagerSnapHelper;
    private OnPageSlideListener onPageSlideListener;

    public VerticalLayoutManager(Context context) {
        super(context, RecyclerView.VERTICAL, false);
        pagerSnapHelper = new PagerSnapHelper();
    }

    @Override
    public void onAttachedToWindow(RecyclerView view) {
        pagerSnapHelper.attachToRecyclerView(view);
        super.onAttachedToWindow(view);
    }

    @Override
    public void onChildViewAttachedToWindow(@NonNull View view) {
        if (drift > 0) { //向上
            if (onPageSlideListener != null)
                onPageSlideListener.onPageSelected(getPosition(view), true);
        } else { //向下
            if (onPageSlideListener != null)
                onPageSlideListener.onPageSelected(getPosition(view), false);
        }
    }

    @Override
    public void onChildViewDetachedFromWindow(@NonNull View view) {

    }

    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        drift = dy;
        return super.scrollVerticallyBy(dy, recycler, state);
    }

    public static interface OnPageSlideListener {
        void onPageSelected(int position, boolean isBottom);
    }

    public void setOnPageSlideListener(OnPageSlideListener mOnViewPagerListener) {
        this.onPageSlideListener = mOnViewPagerListener;
    }

    @Override
    public void onScrollStateChanged(int state) {
        View view = pagerSnapHelper.findSnapView(this);
        int position = getPosition(view);
        if (onPageSlideListener != null && state == RecyclerView.SCROLL_STATE_IDLE) {
            onPageSlideListener.onPageSelected(position, position == getItemCount() - 1);
        }
    }

}
