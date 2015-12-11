package cn.psvmc.pulldownlistview.HeaderOrFooter;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.TextView;

/**
 * Created by PSVMC on 15/12/8.
 */
public class ZJPullListFooterText extends TextView implements ZJPullListAnimate {

    Boolean isShow = true;
    public ZJPullListFooterText(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setGravity(Gravity.CENTER);
    }

    @Override
    public void setProgress(float progress) {
        if(isShow){
            if (progress > 1.0f) {
                progress = 1.0f;
            }

            String str = "加载中....";
            int num = (int)((str.length()-1)*progress);

            this.setText(str.substring(0, num));
        }

    }

    @Override
    public void startAnimate() {
        isShow = true;
        this.setText("加载中...");
    }

    @Override
    public void stopAnimate() {
        isShow = false;
        this.setText("加载完毕");
        final ZJPullListFooterText objThis = this;
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                objThis.isShow = true;
            }

        }, 400);
    }
}
