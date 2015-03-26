package pku.ss.xiaot.weatherapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.zip.GZIPInputStream;

import pku.ss.xiaot.bean.TodayWeather;
import pku.ss.xiaot.util.NetUtil;
import pku.ss.xiaot.util.StaticValue;


public class MainActivity extends ActionBarActivity implements View.OnClickListener {

    private static final int UPDATE_TODAY_WEATHER = 1;

    private String ACTIVITY_TAG = "MAIN_ACTIVITY";

    private ImageView UpdateImageView = null;
    private ImageView pmImageview = null;
    private ImageView weatherImageView = null;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_info);
        UpdateImageView = (ImageView) findViewById(R.id.title_update_btn);
        UpdateImageView.setOnClickListener(this);
        this.initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Toast toast;
        if (NetUtil.getNetworkState(this) == NetUtil.NETWORN_NONE) {
            toast = Toast.makeText(this, "请连接网络", Toast.LENGTH_LONG);
        } else {
            toast = Toast.makeText(this, "连接网络 ：" + NetUtil.getNetworkState(this), Toast.LENGTH_LONG);
        }
        toast.show();
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
    public void onClick(View view) {
        if (view.getId() == R.id.title_update_btn) {
            SharedPreferences sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
            String cityCode = sharedPreferences.getString("main_city_code", "101010100");
            Log.d(ACTIVITY_TAG, cityCode);
            queryWeatherCode(cityCode);
        }
    }

    private void queryWeatherCode(String cityCode) {
        final String address = "http://wthrcdn.etouch.cn/WeatherApi?citykey=" + cityCode;
        Log.d(ACTIVITY_TAG, address);
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
                        Log.d(ACTIVITY_TAG, sb.toString());
                        TodayWeather tw = parseXML(sb.toString());
                        if (tw != null) {
                            Message msg = new Message();
                            msg.what = UPDATE_TODAY_WEATHER;
                            msg.obj = tw;
                            mHandler.sendMessage(msg);
                        }
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
            Log.d(ACTIVITY_TAG, "parse XML");
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
        temperatrueTextView.setText(tw.getHigh() + "~" + tw.getLow());
        windTextView.setText("风力：" + tw.getFengli());
        weatherImageView.setBackgroundResource(StaticValue.weatherTypeMap.get(tw.getType().trim()));
        pmImageview.setBackgroundResource(StaticValue.getPMImage(Integer.parseInt(tw.getPm25().trim())));
        Log.d("updateTodayWeather", "更新完成");
        Toast.makeText(MainActivity.this, "更新成功", Toast.LENGTH_SHORT).show();
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case UPDATE_TODAY_WEATHER:
                    updateTodayWeather((TodayWeather) msg.obj);
                    break;
                default:
                    break;
            }
        }
    };
}
