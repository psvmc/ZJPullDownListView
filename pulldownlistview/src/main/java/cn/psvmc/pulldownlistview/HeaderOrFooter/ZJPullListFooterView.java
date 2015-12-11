package cn.psvmc.pulldownlistview.HeaderOrFooter;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.util.AttributeSet;
import android.widget.ImageView;

public class ZJPullListFooterView extends ImageView implements ZJPullListAnimate{

    int TOP_BOTTOM_MARGIN_OUTTER = 10;
    int TOP_BOTTOM_MARGIN_INNER = 10;
    float progress;
    Paint mPaint;
    float progress2;
    Handler mHandler = new Handler();
    boolean isAnimate;

    public ZJPullListFooterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        this.setWillNotDraw(false);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        progress = 0.0f;
        progress2 = 0.0f;
        isAnimate = false;
    }


    @Override
    public void onDraw(Canvas canvas) {
        mPaint.setColor(Color.parseColor("#249df3"));
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(2);
        canvas.drawArc(new RectF(2, 2 + TOP_BOTTOM_MARGIN_OUTTER, getWidth() - 2, getHeight() - 2 - TOP_BOTTOM_MARGIN_OUTTER), 270, 90 * progress, false, mPaint);
        canvas.drawArc(new RectF(2, 2 + TOP_BOTTOM_MARGIN_OUTTER, getWidth() - 2, getHeight() - 2 - TOP_BOTTOM_MARGIN_OUTTER), 270, -90 * progress, false, mPaint);

        canvas.drawArc(new RectF(2, 2 + TOP_BOTTOM_MARGIN_OUTTER, getWidth() - 2, getHeight() - 2 - TOP_BOTTOM_MARGIN_OUTTER), 90, -90 * progress, false, mPaint);
        canvas.drawArc(new RectF(2, 2 + TOP_BOTTOM_MARGIN_OUTTER, getWidth() - 2, getHeight() - 2 - TOP_BOTTOM_MARGIN_OUTTER), 90, 90 * progress, false, mPaint);

        int marginSize = getWidth() / 4;
        canvas.drawArc(new RectF(marginSize, marginSize + TOP_BOTTOM_MARGIN_INNER, getWidth() - marginSize, getHeight() - marginSize - TOP_BOTTOM_MARGIN_INNER), 270, 90 * progress, false, mPaint);
        canvas.drawArc(new RectF(marginSize, marginSize + TOP_BOTTOM_MARGIN_INNER, getWidth() - marginSize, getHeight() - marginSize - TOP_BOTTOM_MARGIN_INNER), 270, -90 * progress, false, mPaint);

        canvas.drawArc(new RectF(marginSize, marginSize + TOP_BOTTOM_MARGIN_INNER, getWidth() - marginSize, getHeight() - marginSize - TOP_BOTTOM_MARGIN_INNER), 90, -90 * progress, false, mPaint);
        canvas.drawArc(new RectF(marginSize, marginSize + TOP_BOTTOM_MARGIN_INNER, getWidth() - marginSize, getHeight() - marginSize - TOP_BOTTOM_MARGIN_INNER), 90, 90 * progress, false, mPaint);

        if (isAnimate) {
            mPaint.setColor(Color.parseColor("#ff6600"));
            mPaint.setStrokeWidth(5);
            mPaint.setStrokeJoin(Paint.Join.ROUND); // 圆角
            mPaint.setStrokeCap(Paint.Cap.ROUND); // 圆角
            canvas.drawArc(new RectF(marginSize, marginSize + TOP_BOTTOM_MARGIN_INNER, getWidth() - marginSize, getHeight() - marginSize - TOP_BOTTOM_MARGIN_INNER), 270 + 360 * progress2, 10, false, mPaint);
            canvas.drawArc(new RectF(2, 2 + TOP_BOTTOM_MARGIN_OUTTER, getWidth() - 2, getHeight() - 2 - TOP_BOTTOM_MARGIN_OUTTER), 270 - 360 * progress2, 5, false, mPaint);
        }

    }

    public void setProgress(float progress) {
        if (progress > 1.0f) {
            progress = 1.0f;
        }
        this.progress = progress;
        this.invalidate();
    }

    public void startAnimate() {
        if (!isAnimate) {
            isAnimate = true;
            mHandler.post(mRunnable);
        }

    }

    public void stopAnimate() {
        isAnimate = false;
        mHandler.removeCallbacks(mRunnable);

    }

    public Runnable mRunnable = new Runnable() {

        @Override
        public void run() {
            progress2 += 0.01;
            if (isAnimate) {
                mHandler.postDelayed(this, 10);
            }
            ZJPullListFooterView.this.invalidate();
        }

    };


}
