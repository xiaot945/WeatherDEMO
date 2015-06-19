package pku.ss.xiaot.etc;
/**
 * 下拉刷新的监听器，使用下拉刷新的地方应该注册此监听器来获取刷新回调。
 *
 * @author guolin
 */
public interface PullToRefreshListener {
    /**
     * 刷新时会去回调此方法，在方法内编写具体的刷新逻辑。注意此方法是在子线程中调用的， 你可以不必另开线程来进行耗时操作。
     */
    void onRefresh();
}