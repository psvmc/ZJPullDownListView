package cn.psvmc.pulldownlistview.Listener;

import cn.psvmc.pulldownlistview.HeaderOrFooter.ZJPullListAnimate;
import cn.psvmc.pulldownlistview.ZJPullListView;

/**
 * Created by PSVMC on 15/12/9.
 */
public class ZJPullListListenerImpl implements ZJPullListListener {
    public ZJPullListListenerImpl(ZJPullListView pullDownListView, ZJPullListAnimate listHeaderView, ZJPullListAnimate listFooterView) {
        this.pullDownListView = pullDownListView;
        this.listFooterView = listFooterView;
        this.listHeaderView = listHeaderView;
    }

    private ZJPullListView pullDownListView = null;
    private ZJPullListAnimate listFooterView = null;
    private ZJPullListAnimate listHeaderView = null;

    @Override
    public void onTopHeightChange(int headerHeight, int pullHeight) {
        float progress = (float) pullHeight / (float) headerHeight;
        if (progress < 0.5) {
            progress = 0.0f;
        } else {
            progress = (progress - 0.5f) / 0.5f;
        }

        if (!pullDownListView.isRefreshing()) {
            listHeaderView.setProgress(progress);
        }
    }

    @Override
    public void onBottomHeightChange(int footerHeight, int pullHeight) {
        float progress = (float) pullHeight / (float) footerHeight;

        if (progress < 0.5) {
            progress = 0.0f;
        } else {
            progress = (progress - 0.5f) / 0.5f;
        }

        if (progress > 1.0f) {
            progress = 1.0f;
        }

        if (!pullDownListView.isRefreshing()) {
            listFooterView.setProgress(progress);
        }
    }

    @Override
    public void onRefresh() {
        listHeaderView.startAnimate();
    }

    @Override
    public void onLoadMore() {
        listFooterView.startAnimate();
    }

    @Override
    public void endRefresh() {
        listHeaderView.stopAnimate();
    }

    @Override
    public void endLoadMore() {
        listFooterView.stopAnimate();
    }
}
