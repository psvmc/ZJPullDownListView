package cn.psvmc.zjslideitem;

import android.content.Context;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Scroller;

public class SlideView extends LinearLayout {

    private static final String TAG = "SlideView";

    private Context mContext;
    private LinearLayout mViewContent;
    private Scroller mScroller;
    private OnSlideListener mOnSlideListener;
    private boolean mIsMoveClick = false;
    private int leftSlideWidth = 100;
    private int rightSlideWidth = 200;

    private int mLastX = 0;
    private int mLastY = 0;
    private static final int TAN = 2;

    public interface OnSlideListener {
        int SLIDE_STATUS_OFF = 0;
        int SLIDE_STATUS_START_SCROLL = 1;
        int SLIDE_STATUS_ON = 2;

        /**
         * @param view   current SlideView
         * @param status SLIDE_STATUS_ON or SLIDE_STATUS_OFF
         */
        void onSlide(View view, int status);
    }

    public SlideView(Context context,int slideLayoutId,int viewContentId) {
        super(context);
        initView(slideLayoutId,viewContentId);
    }

    private void initView(int slideLayoutId,int viewContentId) {
        mContext = getContext();
        mScroller = new Scroller(mContext);

        setOrientation(LinearLayout.HORIZONTAL);
        View.inflate(mContext, slideLayoutId, this);
        mViewContent = (LinearLayout) findViewById(viewContentId);
        leftSlideWidth = getPxByDp(leftSlideWidth);
        rightSlideWidth = getPxByDp(rightSlideWidth);
    }


    public int getPxByDp(int dp) {
        return Math.round(TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        dp,
                        getResources().getDisplayMetrics())
        );

    }

    public void setContentView(View view) {
        mViewContent.addView(view);
    }

    public void setOnSlideListener(OnSlideListener onSlideListener) {
        mOnSlideListener = onSlideListener;
    }

    public void shrink() {
        if (getScrollX() != 0) {
            this.smoothScrollTo(0, 0);
        }
        mIsMoveClick = false;
    }

    public void onRequireTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        int scrollX = getScrollX();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }
                if (mOnSlideListener != null) {
                    mOnSlideListener.onSlide(this,
                            OnSlideListener.SLIDE_STATUS_START_SCROLL);
                }
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                int deltaX = x - mLastX;
                int deltaY = y - mLastY;
                if (Math.abs(deltaX) < Math.abs(deltaY) * TAN) {
                    break;
                }

                int newScrollX = scrollX - deltaX;
                if (deltaX != 0) {
                    if (newScrollX < 0 && newScrollX < -leftSlideWidth) {
                        newScrollX = -leftSlideWidth;
                    } else if (newScrollX > rightSlideWidth) {
                        newScrollX = rightSlideWidth;
                    } else {

                    }
                    this.scrollTo(newScrollX, 0);
                }
                break;
            }
            case MotionEvent.ACTION_UP: {
                int newScrollX = 0;
                if (scrollX - rightSlideWidth * 0.75 > 0) {
                    newScrollX = rightSlideWidth;
                    mIsMoveClick = !mIsMoveClick;
                } else if (-scrollX - leftSlideWidth * 0.75 > 0) {
                    newScrollX = -leftSlideWidth;
                    mIsMoveClick = !mIsMoveClick;
                } else {
                    mIsMoveClick = false;
                }
                this.smoothScrollTo(newScrollX, 0);
                if (mOnSlideListener != null) {
                    mOnSlideListener.onSlide(this,
                            newScrollX == 0 ? OnSlideListener.SLIDE_STATUS_OFF
                                    : OnSlideListener.SLIDE_STATUS_ON);
                }
                break;
            }
            default:
                break;
        }

        mLastX = x;
        mLastY = y;
    }

    private void smoothScrollTo(int destX, int destY) {
        // 缓慢滚动到指定位置
        int scrollX = getScrollX();
        int delta = destX - scrollX;
        mScroller.startScroll(scrollX, 0, delta, 0, Math.abs(delta) * 3);
        invalidate();
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            postInvalidate();
        }
    }

    public void open() {
        if (getScrollX() < rightSlideWidth) {
            int newScrollX = rightSlideWidth;
            this.smoothScrollTo(newScrollX, 0);
            if (mOnSlideListener != null) {
                mOnSlideListener.onSlide(this,
                        newScrollX == 0 ? OnSlideListener.SLIDE_STATUS_OFF
                                : OnSlideListener.SLIDE_STATUS_ON);
            }
        } else {
            mScroller.startScroll(getScrollX(), 0, -getScrollX(), 0, Math.abs(leftSlideWidth) * 3);
            invalidate();
        }
    }

    public boolean close() {
        if (getScrollX() != 0) {
            mScroller.startScroll(getScrollX(), 0, -getScrollX(), 0, Math.abs(leftSlideWidth) * 3);
            invalidate();
            return false;
        }
        return true;
    }

    public void reset() {
        mScroller.startScroll(getScrollX(), 0, -getScrollX(), 0, 0);
        invalidate();
    }

    public boolean ismIsMoveClick() {
        return mIsMoveClick;
    }
}
