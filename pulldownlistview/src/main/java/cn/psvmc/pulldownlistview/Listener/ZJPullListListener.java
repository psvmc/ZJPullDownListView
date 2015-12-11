package cn.psvmc.pulldownlistview.Listener;

/**
 * Created by PSVMC on 15/12/9.
 */
// listener call back
public interface ZJPullListListener {
     void onTopHeightChange(int headerHeight, int pullHeight);

    void onBottomHeightChange(int footerHeight, int pullHeight);

    void onRefresh();

    void onLoadMore();

    void endRefresh();

    void endLoadMore();
}
