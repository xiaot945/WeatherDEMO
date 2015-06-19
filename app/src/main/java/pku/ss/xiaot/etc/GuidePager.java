package pku.ss.xiaot.etc;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import pku.ss.xiaot.activity.MainActivity;
import pku.ss.xiaot.activity.R;


public class GuidePager extends Activity implements ViewPager.OnPageChangeListener, View.OnClickListener {

    private ViewPagerAdapter viewPagerAdapter = null;
    private ViewPager viewPager = null;
    private List<View> viewList = null;
    private Button startBtn = null;

    private static final int[] dots = {R.id.t1, R.id.t2, R.id.t3};

    private List<ImageView> imageViews = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        SharedPreferences sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
        String isFrirstRun = sharedPreferences.getString("isFirst",
                "yes");
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("isFirst", "no");
        editor.commit();
        if (isFrirstRun.equals("no")) {
            Intent intent = new Intent(GuidePager.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        setContentView(R.layout.activity_guide_pager);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewList = new ArrayList<View>();
        LayoutInflater lf = getLayoutInflater().from(this);
        View page1 = lf.inflate(R.layout.page1, null);
        View page2 = lf.inflate(R.layout.page2, null);
        View page3 = lf.inflate(R.layout.page3, null);
        viewList.add(page1);
        viewList.add(page2);
        viewList.add(page3);
        viewPagerAdapter = new ViewPagerAdapter(viewList, this);
        viewPager.setAdapter(viewPagerAdapter);
        imageViews = new ArrayList<ImageView>();
        for (int i = 0; i < dots.length; i++) {
            ImageView tv = (ImageView) findViewById(dots[i]);
            imageViews.add(tv);
        }
        viewPager.setOnPageChangeListener(this);
        startBtn = (Button) viewList.get(2).findViewById(R.id.start_btn);
        startBtn.setOnClickListener(this);
    }


    @Override
    public void onPageScrolled(int i, float v, int i2) {

    }

    @Override
    public void onPageSelected(int i) {
        for (int j = 0; j < imageViews.size(); j++) {
            if (j == i) {
                imageViews.get(j).setBackgroundResource(R.drawable.page_indicator_focused);
            } else {
                imageViews.get(j).setBackgroundResource(R.drawable.page_indicator_unfocused);
            }
        }
    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start_btn:
                Intent intent = new Intent(GuidePager.this, MainActivity.class);
                startActivity(intent);
                finish();
                break;
            default:
                break;
        }
    }
}
