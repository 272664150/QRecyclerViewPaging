package com.example.qrecyclerviewpaging;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

/**
 * 实现RecyclerView分页滚动的工具类
 */
public class QPagingScrollHelper implements OnTouchListener {

    private int mStartX, mStartY;
    private int mOffsetX, mOffsetY;

    private ValueAnimator mAnimator;
    private RecyclerView mRecyclerView;

    public void attachToRecyclerView(RecyclerView recycleView) {
        if (recycleView == null) {
            throw new IllegalArgumentException("recycleView must be not null");
        }
        mRecyclerView = recycleView;

        //初始化滚动参数
        initScrollParams();
        //记录滚动开始的位置
        mRecyclerView.setOnTouchListener(this);
        //设置滚动监听，记录滚动的状态和总的偏移量
        mRecyclerView.addOnScrollListener(new MyOnScrollListener());
        //处理快速滚动操作
        mRecyclerView.setOnFlingListener(new MyOnFlingListener());
    }

    /**
     * 初始化滚动参数
     */
    private void initScrollParams() {
        mStartX = 0;
        mStartY = 0;
        mOffsetX = 0;
        mOffsetY = 0;
        if (mAnimator != null) {
            mAnimator.cancel();
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //手指按下的时候，记录开始滚动的坐标
                mStartX = mOffsetX;
                mStartY = mOffsetY;
                break;
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                break;
            default:
                break;
        }
        return false;
    }

    private class MyOnScrollListener extends RecyclerView.OnScrollListener {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            //当前的recycleView不滚动(滚动已经停止时)
            if (newState != recyclerView.SCROLL_STATE_IDLE) {
                return;
            }

            //处理回滚：如果滚动的距离超过屏幕的1/3，则需要滚动到下一页
            int velocityX = 0;
            int velocityY = 0;
            if (recyclerView.getLayoutManager().canScrollVertically()) {
                int absY = Math.abs(mOffsetY - mStartY);
                if (absY > recyclerView.getHeight() / 3) {
                    velocityY = mOffsetY - mStartY < 0 ? -1000 : 1000;
                }
            } else {
                int absX = Math.abs(mOffsetX - mStartX);
                if (absX > recyclerView.getWidth() / 3) {
                    velocityX = mOffsetX - mStartX < 0 ? -1000 : 1000;
                }
            }
            mRecyclerView.getOnFlingListener().onFling(velocityX, velocityY);
            super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            //滚动结束时，记录滚动的偏移量
            mOffsetX += dx;
            mOffsetY += dy;
            super.onScrolled(recyclerView, dx, dy);
        }
    }

    private class MyOnFlingListener extends RecyclerView.OnFlingListener {
        @Override
        public boolean onFling(int velocityX, int velocityY) {
            //记录滚动开始和结束的位置
            int startPoint, endPoint;
            //获取开始滚动时所在页面的下标
            int pageIndex = getStartPageIndex();

            //在Orientation方位上，根据不同的速度判断滚动的方向
            //当速度为0的时候，就滚动回到开始的页面，即实现页面复位
            if (mRecyclerView.getLayoutManager().canScrollVertically()) {
                startPoint = mOffsetY;
                if (velocityY < 0) {
                    pageIndex--;
                } else if (velocityY > 0) {
                    pageIndex++;
                }
                endPoint = pageIndex * mRecyclerView.getHeight();
            } else {
                startPoint = mOffsetX;
                if (velocityX < 0) {
                    pageIndex--;
                } else if (velocityX > 0) {
                    pageIndex++;
                }
                endPoint = pageIndex * mRecyclerView.getWidth();
            }
            endPoint = endPoint < 0 ? 0 : endPoint;

            //使用动画处理滚动
            if (mAnimator == null) {
                mAnimator = new ValueAnimator().ofInt(startPoint, endPoint);
                mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        //获取RecycleView可视区域的第一个子view
                        View childView = mRecyclerView.getLayoutManager().getChildAt(0);
                        //获取RecycleView可视区域的第一个子view的底部坐标
                        int bottom = childView.getBottom();
                        //严格考虑,当RecycleView设置了padding时,所有子view的实际边界都是以padding的位置为起始位置
                        int topEdge = mRecyclerView.getPaddingTop();
                        int bottomEdge = mRecyclerView.getPaddingBottom();
                        //RecycleView可视区域子view的高度超过当前屏幕显示的区域高度时，不做分页滚动处理，直到子view滚动到底部为止
                        if ((bottom + bottomEdge > mRecyclerView.getBottom()) && (childView.getHeight() - topEdge - bottomEdge > mRecyclerView.computeVerticalScrollExtent())) {
                            return;
                        }

                        int nowPoint = (int) animation.getAnimatedValue();
                        if (mRecyclerView.getLayoutManager().canScrollVertically()) {
                            int dy = nowPoint - mOffsetY;
                            mRecyclerView.scrollBy(0, dy);
                        } else {
                            int dx = nowPoint - mOffsetX;
                            mRecyclerView.scrollBy(dx, 0);
                        }
                    }
                });
                mAnimator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mRecyclerView.stopScroll(); //解决item项(第一项item除外)点击2次才能响应的问题
                        mStartX = mOffsetX;
                        mStartY = mOffsetY;
                    }
                });
                mAnimator.setDuration(300l);
            } else {
                mAnimator.cancel();
                mAnimator.setIntValues(startPoint, endPoint);
            }
            mAnimator.start();
            return true;
        }
    }

    /**
     * 获取开始滚动时，所在页面的下标
     *
     * @return
     */
    private int getStartPageIndex() {
        int pageIndex = 0;
        if (mRecyclerView.getHeight() == 0 || mRecyclerView.getWidth() == 0) {
            return pageIndex;
        }

        if (mRecyclerView.getLayoutManager().canScrollVertically()) {
            pageIndex = mStartY / mRecyclerView.getHeight();
        } else {
            pageIndex = mStartX / mRecyclerView.getWidth();
        }
        return pageIndex;
    }
}