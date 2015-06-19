package pku.ss.xiaot.weatherapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.w3c.dom.Text;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.zip.GZIPInputStream;

import pku.ss.xiaot.bean.TodayWeather;
import pku.ss.xiaot.util.NetUtil;
import pku.ss.xiaot.util.StaticValue;


public class MainActivity extends ActionBarActivity implements View.OnClickListener {

    private static final int UPDATE_TODAY_WEATHER = 1;
    private static final int UPDATE_OTHER_WEATHER = 2;

    private String TAG = "MAINACTIVITY";

    private ImageView updateImageView = null;
    private ImageView pmImageview = null;
    private ImageView weatherImageView = null;
    private ImageView selectCityImageView = null;
    private ImageView mycityImageView = null;
    private ProgressBar updateProgressBar = null;

    private TextView cityTextView = null;
    private TextView updatetimeTextView = null;
    private TextView humidityTextView = null;
    private TextView pmTextView = null;
    private TextView todayInfoTextView = null;
    private TextView qualityTextView = null;
    private TextView temperatrueTextView = null;
    private TextView typeTextView = null;
    private TextView windTextView = null;
    private TextView wenduTextView = null;
    private LinearLayout ortherdayLinearLayout = null;
    private static final int[] otherIDArray = {R.id.orther1, R.id.orther2, R.id.orther3, R.id.orther4, R.id.orther5, R.id.orther6};
    //    private List<LinearLayout> otherWeatherLinearLayoutList = new ArrayList<LinearLayout>();
    private List<LinearLayout> otherLinearLayoutList = new ArrayList<LinearLayout>();
    private String cityCode = null;

    private static final String CITY_CODE = "main_city_code";
    private static final String PRE_TIME = "pre_time";
    private static final String PRE_XML = "pre_xml";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "-> onCreate");
        setContentView(R.layout.weather_info);
        updateImageView = (ImageView) findViewById(R.id.title_update_btn);
        selectCityImageView = (ImageView) findViewById(R.id.title_city_manager);
        updateProgressBar = (ProgressBar) findViewById(R.id.title_update_progress);
        ortherdayLinearLayout = (LinearLayout) findViewById(R.id.other_days);
        mycityImageView = (ImageView) findViewById(R.id.title_mycity);
        mycityImageView.setOnClickListener(this);
        updateImageView.setOnClickListener(this);
        selectCityImageView.setOnClickListener(this);

        for (int i = 0; i < otherIDArray.length; i++)
            otherLinearLayoutList.add((LinearLayout) findViewById(otherIDArray[i]));
        this.initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Toast toast;
        SharedPreferences sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
        if (NetUtil.getNetworkState(this) == NetUtil.NETWORN_NONE) {
            toast = Toast.makeText(this, "请连接网络", Toast.LENGTH_LONG);
            toast.show();
        }
        if (cityCode == null) {
            cityCode = sharedPreferences.getString(CITY_CODE, "101010100");
        }
        long preTime = sharedPreferences.getLong(PRE_TIME, 0L);
        String preXMl = sharedPreferences.getString(PRE_XML, "nothing");
        long nowTime = new Date().getTime();
        if ((nowTime - preTime > 2 * 60 * 1000) && !preXMl.equals("nothing")) {
            Log.d(TAG, "load the weather in SharedPreferences");
            convertVisibility();
            TodayWeather tw = parseXML(preXMl);
            List<HashMap<String, String>> otherWeatherList = parseOtherWeather(preXMl);
            if (tw != null) {
                Message msg = new Message();
                msg.what = UPDATE_TODAY_WEATHER;
                msg.obj = tw;
                mHandler.sendMessage(msg);
            }
            if (otherWeatherList.size() > 0) {
                Message msg = new Message();
                msg.what = UPDATE_OTHER_WEATHER;
                msg.obj = otherWeatherList;
                mHandler.sendMessage(msg);
            }
        } else {
            queryWeatherCode(cityCode);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            cityCode = intent.getStringExtra(SelectCityActivity.CITY_CODE);
            queryWeatherCode(cityCode);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_update_btn:
                if (cityCode == null) {
                    SharedPreferences sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
                    cityCode = sharedPreferences.getString("main_city_code", "101010100");
                }
                queryWeatherCode(cityCode);
                break;
            case R.id.title_city_manager:
                Intent intent = new Intent(MainActivity.this, SelectCityActivity.class);
                startActivityForResult(intent, 1);
                break;
            case R.id.title_mycity:
                Intent i = new Intent(MainActivity.this, CityManagerActivity.class);
                startActivityForResult(i, 1);
                break;
            default:
                break;
        }

    }

    private void queryWeatherCode(final String cityCode) {
        convertVisibility();
        final String address = "http://wthrcdn.etouch.cn/WeatherApi?citykey=" + cityCode;
        Log.d(TAG, address);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpGet httpGet = new HttpGet(address);
                    HttpResponse httpResponse = httpClient.execute(httpGet);
                    if (httpResponse.getStatusLine().getStatusCode() == 200) {
                        HttpEntity httpEntity = httpResponse.getEntity();
                        InputStream inputStream = httpEntity.getContent();
                        inputStream = new GZIPInputStream(inputStream);
                        BufferedReader bf = new BufferedReader(new InputStreamReader(inputStream));
                        StringBuilder sb = new StringBuilder();
                        String str;
                        while ((str = bf.readLine()) != null) {
                            sb.append(str);
                        }
//                        Log.d(TAG, sb.toString());
                        TodayWeather tw = parseXML(sb.toString());
                        List<HashMap<String, String>> otherWeatherList = parseOtherWeather(sb.toString());
                        if (tw != null) {
                            Message msg = new Message();
                            msg.what = UPDATE_TODAY_WEATHER;
                            msg.obj = tw;
                            mHandler.sendMessage(msg);
                        }
                        if (otherWeatherList.size() > 0) {
                            Message msg = new Message();
                            msg.what = UPDATE_OTHER_WEATHER;
                            msg.obj = otherWeatherList;
                            mHandler.sendMessage(msg);
                        }

                        SharedPreferences sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(PRE_XML, sb.toString());
                        editor.putLong(PRE_TIME, new Date().getTime());
                        editor.putString(CITY_CODE, cityCode);
                        editor.commit();
                        Log.d(TAG, "save the weather in SharedPreferences");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private TodayWeather parseXML(String xmldata) {
        TodayWeather tw = null;
        int fengxiangCount = 0;
        int fengliCount = 0;
        int dateCount = 0;
        int highCount = 0;
        int lowCount = 0;
        int typeCount = 0;
        try {
            XmlPullParserFactory fac = XmlPullParserFactory.newInstance();
            XmlPullParser parser = fac.newPullParser();
            parser.setInput(new StringReader(xmldata));
            int eventType = parser.getEventType();
            Log.d(TAG, "parse today XML");
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        if (parser.getName().equals("resp")) {
                            tw = new TodayWeather();
                        }

                        if (tw != null) {
                            if (parser.getName().equals("city")) {
                                eventType = parser.next();
                                tw.setCity(parser.getText());
                            } else if (parser.getName().equals("updatetime")) {
                                eventType = parser.next();
                                tw.setUpdatetime(parser.getText());
                            } else if (parser.getName().equals("shidu")) {
                                eventType = parser.next();
                                tw.setShidu(parser.getText());
                            } else if (parser.getName().equals("wendu")) {
                                eventType = parser.next();
                                tw.setWendu(parser.getText());
                            } else if (parser.getName().equals("pm25")) {
                                eventType = parser.next();
                                tw.setPm25(parser.getText());
                            } else if (parser.getName().equals("quality")) {
                                eventType = parser.next();
                                tw.setQuality(parser.getText());
                            } else if (parser.getName().equals("fengxiang") && fengxiangCount == 0) {
                                eventType = parser.next();
                                tw.setFengxiang(parser.getText());
                                fengxiangCount++;
                            } else if (parser.getName().equals("fengli") && fengliCount == 0) {
                                eventType = parser.next();
                                tw.setFengli(parser.getText());
                                fengliCount++;
                            } else if (parser.getName().equals("date") && dateCount == 0) {
                                eventType = parser.next();
                                tw.setDate(parser.getText());
                                dateCount++;
                            } else if (parser.getName().equals("high") && highCount == 0) {
                                eventType = parser.next();
                                tw.setHigh(parser.getText().substring(2).trim());
                                highCount++;
                            } else if (parser.getName().equals("low") && lowCount == 0) {
                                eventType = parser.next();
                                tw.setLow(parser.getText().substring(2).trim());
                                lowCount++;
                            } else if (parser.getName().equals("type") && typeCount == 0) {
                                eventType = parser.next();
                                tw.setType(parser.getText());
                                typeCount++;
                            }
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        break;
                }
                eventType = parser.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tw;
    }

    private List<HashMap<String, String>> parseOtherWeather(String xmlStr) {
        List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> map = null;
        int infoCount = 0;
        int highCount = 0;
        int lowCount = 0;
        int typeCount = 0;
        int windCount = 0;
        boolean isIn = false;
        try {
            XmlPullParserFactory fac = XmlPullParserFactory.newInstance();
            XmlPullParser parser = fac.newPullParser();
            parser.setInput(new StringReader(xmlStr));
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        if (parser.getName().equals("yesterday") || parser.getName().equals("weather")) {
                            map = new HashMap<String, String>();
                            isIn = true;
                            infoCount = 0;
                            highCount = 0;
                            lowCount = 0;
                            typeCount = 0;
                            windCount = 0;
                        } else if ((parser.getName().equals("date_1") || parser.getName().equals("date")) && infoCount == 0 && isIn) {
                            eventType = parser.next();
                            map.put("date", parser.getText());
                            infoCount++;
                        } else if ((parser.getName().equals("high_1") || parser.getName().equals("high")) && highCount == 0 && isIn) {
                            eventType = parser.next();
                            map.put("high", parser.getText().substring(2).trim());
                            highCount++;
                        } else if ((parser.getName().equals("low_1") || parser.getName().equals("low")) && lowCount == 0 && isIn) {
                            eventType = parser.next();
                            map.put("low", parser.getText().substring(2).trim());
                            lowCount++;
                        } else if ((parser.getName().equals("type_1") || parser.getName().equals("type")) && typeCount == 0 && isIn) {
                            eventType = parser.next();
                            map.put("type", parser.getText());
                            typeCount++;
                        } else if ((parser.getName().equals("fl_1") || parser.getName().equals("fengli")) && windCount == 0 && isIn) {
                            eventType = parser.next();
                            map.put("fengli", parser.getText());
                            windCount++;
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if (parser.getName().equals("yesterday") || parser.getName().equals("weather")) {
                            isIn = false;
                            list.add(map);
                        }
                        break;
                }
                eventType = parser.next();
            }
            Log.d(TAG, "parse otherday XML");
        } catch (Exception e)

        {
            e.printStackTrace();
        }

        return list;
    }

    private void initView() {
        cityTextView = (TextView) findViewById(R.id.city_loaction);
        updatetimeTextView = (TextView) findViewById(R.id.update_info);
        humidityTextView = (TextView) findViewById(R.id.humidity);
        pmTextView = (TextView) findViewById(R.id.pm_value);
        qualityTextView = (TextView) findViewById(R.id.quality);
        todayInfoTextView = (TextView) findViewById(R.id.today_info);
        temperatrueTextView = (TextView) findViewById(R.id.temperature);
        windTextView = (TextView) findViewById(R.id.wind);
        typeTextView = (TextView) findViewById(R.id.weather_change);
        wenduTextView = (TextView) findViewById(R.id.wendu);
        weatherImageView = (ImageView) findViewById(R.id.pic_today_weather);
        pmImageview = (ImageView) findViewById(R.id.pm_img);
        cityTextView.setText("N/A");
        updatetimeTextView.setText("N/A");
        humidityTextView.setText("N/A");
        pmTextView.setText("N/A");
        qualityTextView.setText("N/A");
        todayInfoTextView.setText("N/A");
        temperatrueTextView.setText("N/A");
        windTextView.setText("N/A");
        typeTextView.setText("N/A");
        wenduTextView.setText("N/A");

    }

    private void updateTodayWeather(TodayWeather tw) {
        Log.d("updateTodayWeather", tw.toString());
        cityTextView.setText(tw.getCity());
        updatetimeTextView.setText(tw.getUpdatetime() + "发布");
        humidityTextView.setText("湿度：" + tw.getShidu());
        wenduTextView.setText("气温:" + tw.getWendu() + "℃");
        qualityTextView.setText(tw.getQuality());
        pmTextView.setText(tw.getPm25());
        todayInfoTextView.setText(tw.getDate());
        typeTextView.setText(tw.getType());
        temperatrueTextView.setText(tw.getLow() + "~" + tw.getHigh());
        windTextView.setText("风力：" + tw.getFengli());
        int wId = 0;
        try {
            wId = StaticValue.weatherTypeMap.get(tw.getType().trim());
        } catch (NullPointerException e) {
            wId = R.drawable.biz_plugin_weather_qing;
            e.printStackTrace();
        } finally {
            weatherImageView.setBackgroundResource(wId);
        }
        try {
            pmImageview.setBackgroundResource(StaticValue.getPMImage(Integer.parseInt(tw.getPm25().trim())));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        Log.d("updateTodayWeather", "today 更新完成");
        Toast.makeText(MainActivity.this, "更新成功", Toast.LENGTH_SHORT).show();
    }

    private void updateOtherWeather(List<HashMap<String, String>> otherWeatherList) {
        for (int i = 0; i < otherWeatherList.size(); i++) {
            TextView dateTextViw = (TextView) otherLinearLayoutList.get(i).findViewById(R.id.other_day_info);
            TextView tempTextView = (TextView) otherLinearLayoutList.get(i).findViewById(R.id.other_day_temperature);
            TextView typeTextView = (TextView) otherLinearLayoutList.get(i).findViewById(R.id.other_day_weather_change);
            TextView windTextView = (TextView) otherLinearLayoutList.get(i).findViewById(R.id.other_day_wind);
            ImageView weatherImageView = (ImageView) otherLinearLayoutList.get(i).findViewById(R.id.other_day_weather_pic);
            dateTextViw.setText(otherWeatherList.get(i).get("date"));
            tempTextView.setText(otherWeatherList.get(i).get("low") + "~" + otherWeatherList.get(i).get("high"));
            typeTextView.setText(otherWeatherList.get(i).get("type"));
            windTextView.setText(otherWeatherList.get(i).get("fengli"));
            int wId = 0;
            try {
                wId = StaticValue.weatherTypeMap.get(otherWeatherList.get(i).get("type").trim());
            } catch (NullPointerException e) {
                wId = R.drawable.biz_plugin_weather_qing;
                e.printStackTrace();
            } finally {
                weatherImageView.setBackgroundResource(wId);
            }
            Log.d("add View", "" + i);
            // ortherdayLinearLayout.addView(otherWeatherLinearLayout);
        }
        Log.d("updateOtherWeather", "otherday 更新完成");
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case UPDATE_TODAY_WEATHER:
                    updateTodayWeather((TodayWeather) msg.obj);
                    break;
                case UPDATE_OTHER_WEATHER:
                    ArrayList<HashMap<String, String>> l = (ArrayList<HashMap<String, String>>) msg.obj;
                    Log.d("other day list size", "" + l.size());
                    updateOtherWeather(l);
                    convertVisibility();
                    break;
                default:
                    break;
            }
        }
    };

    private void convertVisibility() {
        if (updateImageView.getVisibility() == View.VISIBLE) {
            updateImageView.setVisibility(View.GONE);
            updateProgressBar.setVisibility(View.VISIBLE);
        } else {
            updateImageView.setVisibility(View.VISIBLE);
            updateProgressBar.setVisibility(View.GONE);
        }
    }
}
