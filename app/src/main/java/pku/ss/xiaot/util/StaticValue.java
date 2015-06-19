package pku.ss.xiaot.util;

import java.util.HashMap;

import pku.ss.xiaot.activity.R;

/**
 * Created by xiaot on 2015/3/26.
 */
public class StaticValue {

    public static final HashMap<String, Integer> weatherTypeMap = new HashMap<>();

    static{
        weatherTypeMap.put("暴雪", R.drawable.biz_plugin_weather_baoxue);
        weatherTypeMap.put("暴雨", R.drawable.biz_plugin_weather_baoyu);
        weatherTypeMap.put("大暴雨", R.drawable.biz_plugin_weather_dabaoyu);
        weatherTypeMap.put("大雪", R.drawable.biz_plugin_weather_daxue);
        weatherTypeMap.put("大雨", R.drawable.biz_plugin_weather_dayu);
        weatherTypeMap.put("多云", R.drawable.biz_plugin_weather_duoyun);
        weatherTypeMap.put("雷阵雨", R.drawable.biz_plugin_weather_leizhenyu);
        weatherTypeMap.put("雷阵雨冰雹", R.drawable.biz_plugin_weather_leizhenyubingbao);
        weatherTypeMap.put("晴", R.drawable.biz_plugin_weather_qing);
        weatherTypeMap.put("扬沙", R.drawable.biz_plugin_weather_shachenbao);
        weatherTypeMap.put("特大暴雨", R.drawable.biz_plugin_weather_tedabaoyu);
        weatherTypeMap.put("雾", R.drawable.biz_plugin_weather_wu);
        weatherTypeMap.put("小雪", R.drawable.biz_plugin_weather_xiaoxue);
        weatherTypeMap.put("小雨", R.drawable.biz_plugin_weather_xiaoyu);
        weatherTypeMap.put("阴", R.drawable.biz_plugin_weather_yin);
        weatherTypeMap.put("雨夹雪", R.drawable.biz_plugin_weather_yujiaxue);
        weatherTypeMap.put("阵雨", R.drawable.biz_plugin_weather_zhenyu);
        weatherTypeMap.put("中雪", R.drawable.biz_plugin_weather_zhongxue);
        weatherTypeMap.put("中雨", R.drawable.biz_plugin_weather_zhongyu);
        weatherTypeMap.put("阵雪", R.drawable.biz_plugin_weather_zhenxue);
    }

    public static int getPMImage(int value) {
        if (value <= 50) {
            return R.drawable.biz_plugin_weather_0_50;
        } else if (value <= 100) {
            return R.drawable.biz_plugin_weather_51_100;
        } else if (value <= 150) {
            return R.drawable.biz_plugin_weather_101_150;
        } else if (value <= 200) {
            return R.drawable.biz_plugin_weather_151_200;
        } else if (value <= 300) {
            return R.drawable.biz_plugin_weather_201_300;
        } else {
            return R.drawable.biz_plugin_weather_greater_300;
        }
    }
}
