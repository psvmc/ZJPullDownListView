package cn.psvmc.pulldownlistview;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.animation.ValueAnimator.AnimatorUpdateListener;

import cn.psvmc.pulldownlistview.Listener.ZJPullListListener;

/**
 * 简单易扩展的刷新组件
 */
public class ZJPullListView extends RelativeLayout implements
        OnScrollListener {
    private String TAG = "ZJPullListView";
    //点击的position
    private int clickPosition;
    static int MAX_PULL_TOP_HEIGHT;
    static int MAX_PULL_BOTTOM_HEIGHT;

    static int REFRESHING_TOP_HEIGHT;
    static int REFRESHING_BOTTOM_HEIGHT;

    static int DURATION_TIME = 120;

    private boolean isTop;
    private boolean isBottom;
    private boolean isRefreshing;
    private boolean isAnimation;

    RelativeLayout layoutHeader;
    RelativeLayout layoutFooter;

    private int mCurrentY = 0;
    boolean pullTag = false;
    OnScrollListener mOnScrollListener;
    ZJPullListListener mOnPullHeightChangeListener;

    public void setOnPullHeightChangeListener(
            ZJPullListListener listener) {
        this.mOnPullHeightChangeListener = listener;
    }

    public void setOnScrollListener(OnScrollListener listener) {
        mOnScrollListener = listener;
    }

    public ZJPullListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    public boolean isRefreshing() {
        return this.isRefreshing;
    }

    /**
     * 获取点击的位置
     * @return
     */
    public int getClickPosition() {
        return clickPosition;
    }

    private ListView mListView = new ListView(getContext()) {

        int lastY = 0;

        @Override
        public boolean onTouchEvent(MotionEvent ev) {

            int x = (int) ev.getX();
            int y = (int) ev.getY();
            clickPosition = pointToPosition(x, y);

            if (isAnimation || isRefreshing) {
                return super.onTouchEvent(ev);
            }
            RelativeLayout parent = (RelativeLayout) mListView.getParent();

            int currentY = (int) ev.getRawY();

            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mListView.getLayoutParams();
            int topMargin = lp.topMargin;
            int bottomMargin = lp.bottomMargin;

            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    lastY = (int) ev.getRawY();
                    break;
                case MotionEvent.ACTION_MOVE: {


                    //手指是否下拉
                    boolean isToBottom = currentY - lastY >= 0 ? true : false;

                    int step = Math.abs(currentY - lastY);
                    //过滤掉微小的移动
                    if (step < 2) {
                        ev.setAction(MotionEvent.ACTION_UP);
                    } else {
                        lastY = currentY;

                        //listview的内容不足一屏时
                        if (isTop && isBottom) {
                            if (isToBottom) {
                                isTop = true;
                                isBottom = false;
                            } else {
                                isTop = false;
                                isBottom = true;
                            }
                        }

                        //下拉刷新
                        if (isTop && mListView.getTop() >= 0 && bottomMargin == 0) {

                            if (isToBottom && mListView.getTop() <= MAX_PULL_TOP_HEIGHT) {
                                ev.setAction(MotionEvent.ACTION_UP);
                                super.onTouchEvent(ev);
                                pullTag = true;

                                if (mListView.getTop() > layoutHeader.getHeight()) {
                                    step = step / 2;
                                }
                                if ((mListView.getTop() + step) > MAX_PULL_TOP_HEIGHT) {
                                    mCurrentY = MAX_PULL_TOP_HEIGHT;
                                    scrollTopTo(mCurrentY);
                                } else {
                                    mCurrentY += step;
                                    scrollTopTo(mCurrentY);
                                }
                            } else if (!isToBottom && topMargin > 0) {
                                ev.setAction(MotionEvent.ACTION_UP);
                                super.onTouchEvent(ev);
                            } else if (!isToBottom && topMargin == 0) {
                                if (!pullTag) {
                                    return super.onTouchEvent(ev);
                                }
                            }

                            return true;
                        }
                        //上拉加载更多
                        else if (isBottom && bottomMargin >= 0 && topMargin == 0) {
                            //上拉 上拉的距离小于bottom高时
                            if (!isToBottom && (parent.getHeight() - mListView.getBottom()) <= MAX_PULL_BOTTOM_HEIGHT && topMargin == 0) {
                                scrollBottomTo(bottomMargin + step);
                                pullTag = true;
                            } else if (!isToBottom && bottomMargin > MAX_PULL_BOTTOM_HEIGHT) {
                                ev.setAction(MotionEvent.ACTION_UP);
                                super.onTouchEvent(ev);
                            } else if (isToBottom && bottomMargin == 0) {
                                if (!pullTag) {
                                    return super.onTouchEvent(ev);
                                }
                            }
                            return true;
                        }

                    }

                    break;
                }
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                    pullTag = false;

                    if (topMargin > 0) {

                        if (topMargin > REFRESHING_TOP_HEIGHT) {
                            animateTopTo(REFRESHING_TOP_HEIGHT);
                            isRefreshing = true;
                            if (null != mOnPullHeightChangeListener) {
                                mOnPullHeightChangeListener.onRefresh();
                            }
                        } else {
                            animateTopTo(0);
                        }

                    } else if (bottomMargin < parent.getHeight()) {
                        if (bottomMargin > REFRESHING_BOTTOM_HEIGHT) {
                            animateBottomTo(REFRESHING_BOTTOM_HEIGHT);
                            isRefreshing = true;
                            if (null != mOnPullHeightChangeListener) {
                                mOnPullHeightChangeListener.onLoadMore();
                            }
                        } else {
                            animateBottomTo(0);
                        }
                    }
            }
            return super.onTouchEvent(ev);
        }

    };

    public void scrollBottomTo(int y) {
        LayoutParams lp = (LayoutParams) mListView.getLayoutParams();
        lp.bottomMargin = y;
        mListView.setLayoutParams(lp);
        mListView.requestLayout();
        //用该方式加载数据时闪屏
        //mListView.layout(mListView.getLeft(), y, mListView.getRight(),this.getMeasuredHeight() + y);
        if (null != mOnPullHeightChangeListener) {
            mOnPullHeightChangeListener.onBottomHeightChange(
                    layoutHeader.getHeight(), y);
        }
    }

    public void animateBottomTo(final int y) {
        LayoutParams lp = (LayoutParams) mListView.getLayoutParams();
        int bottomMargin = lp.bottomMargin;
        ValueAnimator animator = ValueAnimator.ofInt(bottomMargin, y);
        animator.setDuration(DURATION_TIME);
        animator.addUpdateListener(new AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int frameValue = (Integer) animation.getAnimatedValue();
                mCurrentY = frameValue;
                scrollBottomTo(frameValue);
                if (frameValue == y) {
                    isAnimation = false;
                }
            }
        });
        isAnimation = true;
        animator.start();
    }

    public void scrollTopTo(int y) {

        LayoutParams lp = (LayoutParams) mListView.getLayoutParams();
        lp.topMargin = y;
        mListView.setLayoutParams(lp);
        mListView.requestLayout();
        //用该方式加载数据时闪屏
        //mListView.layout(mListView.getLeft(), , mListView.getRight(), this.getMeasuredHeight() + y);
        if (null != mOnPullHeightChangeListener) {
            mOnPullHeightChangeListener.onTopHeightChange(
                    layoutHeader.getHeight(), y);
        }
    }


    public void animateTopTo(final int y) {
        LayoutParams lp = (LayoutParams) mListView.getLayoutParams();
        int topMargin = lp.topMargin;
        ValueAnimator animator = ValueAnimator.ofInt(topMargin, y);
        animator.setDuration(DURATION_TIME);
        animator.addUpdateListener(new AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {

                int frameValue = (Integer) animation.getAnimatedValue();

                mCurrentY = frameValue;
                scrollTopTo(frameValue);
                if (frameValue == y) {
                    isAnimation = false;
                }
            }
        });
        isAnimation = true;
        animator.start();
    }


    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        REFRESHING_TOP_HEIGHT = layoutHeader.getMeasuredHeight();
        REFRESHING_BOTTOM_HEIGHT = layoutFooter.getMeasuredHeight();

        MAX_PULL_TOP_HEIGHT = this.getMeasuredHeight();
        MAX_PULL_BOTTOM_HEIGHT = this.getMeasuredHeight();
    }

    @Override
    public void onFinishInflate() {
        mListView.setBackgroundColor(0xffffffff);
        mListView.setCacheColorHint(Color.TRANSPARENT);
        mListView.setVerticalScrollBarEnabled(false);
        mListView.setLayoutParams(new LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        mListView.setDivider(new ColorDrawable(Color.parseColor("#cccccc")));
        mListView.setDividerHeight(1);
        mListView.setOnScrollListener(this);
        this.addView(mListView);
        layoutHeader = (RelativeLayout) this.findViewById(R.id.layoutHeader);
        layoutFooter = (RelativeLayout) this.findViewById(R.id.layoutFooter);


        super.onFinishInflate();
    }


    public ListView getListView() {
        return this.mListView;
    }

    /**
     * 数据加载后调用该方法
     */
    public void endingRefreshOrLoadMore() {
        isRefreshing = false;
        if (mListView.getTop() > 0) {
            animateTopTo(0);
            if (null != mOnPullHeightChangeListener) {
                mOnPullHeightChangeListener.endRefresh();
            }
        } else if (mListView.getBottom() < this.getHeight()) {
            animateBottomTo(0);
            if (null != mOnPullHeightChangeListener) {
                mOnPullHeightChangeListener.endLoadMore();
            }
        }

    }


    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {

        if (null != mOnScrollListener) {
            mOnScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
        }
        if (mListView.getCount() > 0) {
            if ((firstVisibleItem + visibleItemCount) == totalItemCount) {
                View lastItem = (View) mListView
                        .getChildAt(visibleItemCount - 1);
                if (null != lastItem) {

                    if (lastItem.getBottom() <= mListView.getHeight()) {

                        isBottom = true;
                    } else {
                        isBottom = false;
                    }
                }
            } else {
                isBottom = false;
            }
        } else {
            isBottom = true;
        }

        if (mListView.getCount() > 0) {
            if (firstVisibleItem == 0) {
                View firstItem = mListView.getChildAt(0);
                if (null != firstItem) {
                    if (firstItem.getTop() == 0) {
                        isTop = true;
                    } else {
                        isTop = false;
                    }
                }
            } else {
                isTop = false;
            }
        } else {
            isTop = true;
        }

    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        // TODO Auto-generated method stub
        if (null != mOnScrollListener) {
            mOnScrollListener.onScrollStateChanged(view, scrollState);
        }
    }

}
