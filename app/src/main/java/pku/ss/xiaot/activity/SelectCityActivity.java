package pku.ss.xiaot.activity;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import pku.ss.xiaot.etc.MyApplication;


public class SelectCityActivity extends ActionBarActivity implements View.OnClickListener {

    public static String CITY_CODE = "cityCode";

    private ImageView backImageView = null;
    private ListView cityListView = null;
    private ListView provinceListView = null;
    private EditText cityEditText = null;
    private MyApplication mApplication = null;

    private String tempProvince = "";

    String[] cityArray = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_city);
        backImageView = (ImageView) findViewById(R.id.title_back);
        backImageView.setOnClickListener(this);
        mApplication = (MyApplication) getApplication();
        provinceListView = (ListView) findViewById(R.id.province_list);
        cityListView = (ListView) findViewById(R.id.city_list);
        cityEditText = (EditText) findViewById(R.id.city_text);

        provinceListView.setAdapter(new ArrayAdapter<String>(SelectCityActivity.this, android.R.layout.simple_list_item_1, mApplication.getAllProvince()));
        provinceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                tempProvince = mApplication.getAllProvince()[position];
                cityArray = mApplication.getCityByProvince(tempProvince);
                cityListView.setAdapter(new ArrayAdapter<String>(SelectCityActivity.this, android.R.layout.simple_list_item_1, cityArray));

            }
        });
        cityListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle cityCodeBundle = new Bundle();
                String str = cityArray[position];
                String cityCode = null;
                if (!str.contains(",")) {
                    cityCode = mApplication.getNumberByCityAndProvince(cityArray[position], tempProvince);
                } else {
                    String provinceStr = cityArray[position].split(",")[0].trim();
                    String cityStr = cityArray[position].split(",")[1].trim();
                    cityCode = mApplication.getNumberByCityAndProvince(cityStr, provinceStr);
                }
                cityCodeBundle.putString(CITY_CODE, cityCode);
                Intent intent = new Intent(SelectCityActivity.this, MainActivity.class);
                intent.putExtras(cityCodeBundle);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        cityEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.d("Text Wacther", "beforeTextChanged");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d("Text Wacther", "onTextChanged");
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.d("Text Wacther", "afterTextChanged");
                cityArray = mApplication.getCityByName(cityEditText.getText().toString().trim());
                cityListView.setAdapter(new ArrayAdapter<String>(SelectCityActivity.this, android.R.layout.simple_list_item_1, cityArray));
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_select_city, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_back:
                finish();
                break;
            default:
                break;
        }
    }
}
