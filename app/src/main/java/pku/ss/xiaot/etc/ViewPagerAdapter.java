package pku.ss.xiaot.etc;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by xiaot on 2015/4/23.
 */
public class ViewPagerAdapter extends PagerAdapter {

    private List<View> viewList = null;
    private Context context = null;

    public ViewPagerAdapter(List<View> viewList, Context context) {
        this.viewList = viewList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return viewList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object o) {
        return view == o;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
       container.addView(viewList.get(position));
        return viewList.get(position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(viewList.get(position));
    }
}
