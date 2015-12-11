package com.pulldownlistview.Activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.pulldownlistview.R;

import java.util.ArrayList;
import java.util.List;

import cn.psvmc.pulldownlistview.HeaderOrFooter.ZJPullListAnimate;
import cn.psvmc.pulldownlistview.Listener.ZJPullListListenerImpl;
import cn.psvmc.pulldownlistview.ZJPullListView;

public class MainActivity extends Activity {
    final String TAG = "MainActivity";
    List<String> adapterData = new ArrayList<>();
    private BaseAdapter mAdapter;
    private ListView mListView;
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        adapterData.add("111");
        adapterData.add("222");
        adapterData.add("333");
        adapterData.add("111");
        adapterData.add("222");
        adapterData.add("333");
        adapterData.add("111");
        adapterData.add("222");
        adapterData.add("333");
        adapterData.add("111");
        adapterData.add("222");
        adapterData.add("333");
        adapterData.add("111");
        adapterData.add("222");
        adapterData.add("333");
        adapterData.add("111");
        adapterData.add("222");
        adapterData.add("333");

        initAdapter();
        initListView();

    }

    private void initListView() {
        final ZJPullListView pullDownListView = (ZJPullListView) this.findViewById(R.id.pullDownListView);
        final ZJPullListAnimate listFooterView = (ZJPullListAnimate) this.findViewById(R.id.listFooterView);
        final ZJPullListAnimate listHeaderView = (ZJPullListAnimate) this.findViewById(R.id.listHeaderView);

        mListView = pullDownListView.getListView();
        mListView.setAdapter(mAdapter);

        pullDownListView.setOnPullHeightChangeListener(new ZJPullListListenerImpl(pullDownListView, listHeaderView, listFooterView) {

            @Override
            public void onRefresh() {
                super.onRefresh();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        adapterData.removeAll(adapterData);
                        adapterData.add("000");
                        mAdapter.notifyDataSetChanged();
                        pullDownListView.endingRefreshOrLoadMore();
                    }

                }, 2000);
            }

            @Override
            public void onLoadMore() {
                super.onLoadMore();
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        adapterData.add("000");
                        mAdapter.notifyDataSetChanged();
                        pullDownListView.endingRefreshOrLoadMore();
                    }

                }, 2000);
            }


        });
    }

    private void initAdapter() {
        mAdapter = new BaseAdapter() {

            @Override
            public int getCount() {
                return adapterData.size();
            }

            @Override
            public Object getItem(int position) {

                return adapterData.get(position);
            }

            @Override
            public long getItemId(int position) {

                return 0;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                TextView textView = new TextView(mContext);
                textView.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp2px(mContext, 50)));
                textView.setText(adapterData.get(position));
                textView.setTextSize(20);
                textView.setTextColor(0xff000000);
                textView.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
                textView.setPadding(50, 0, 0, 0);

                return textView;
            }

        };

    }


    public static int dp2px(Context context, int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                context.getResources().getDisplayMetrics());
    }
}
