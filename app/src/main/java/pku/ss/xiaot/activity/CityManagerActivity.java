package pku.ss.xiaot.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import pku.ss.xiaot.etc.MyApplication;


public class CityManagerActivity extends ActionBarActivity implements View.OnClickListener {

    private ImageView backToMainImageView = null;
    private ImageView newImageView = null;
    private ListView cityListView = null;
    private MyApplication mApplication = null;
    private String[] myCityArray = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_manager);
        mApplication = (MyApplication) getApplication();
        backToMainImageView = (ImageView) findViewById(R.id.city_manager_title_back);
        newImageView = (ImageView) findViewById(R.id.city_manager_title_newcity);
        newImageView.setOnClickListener(this);
        backToMainImageView.setOnClickListener(this);
        cityListView = (ListView) findViewById(R.id.mycity_listview);
        myCityArray = mApplication.getMycity();
        cityListView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, myCityArray));
        Log.d("mycity", "create" + myCityArray.length);
        cityListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle cityCodeBundle = new Bundle();
                String cityCode = null;
                String provinceStr = myCityArray[position].split(",")[0].trim();
                String cityStr = myCityArray[position].split(",")[1].trim();
                cityCode = mApplication.getNumberByCityAndProvince(cityStr, provinceStr);
                cityCodeBundle.putString(SelectCityActivity.CITY_CODE, cityCode);
                Intent intent = new Intent(CityManagerActivity.this, MainActivity.class);
                intent.putExtras(cityCodeBundle);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        cityListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final int p = position;
                new AlertDialog.Builder(CityManagerActivity.this).setTitle("确认删除吗？" + " " + myCityArray[p])
                        .setIcon(android.R.drawable.ic_dialog_info).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d("mycity", "before delete" + myCityArray.toString());
                        String cityCode = null;
                        String provinceStr = myCityArray[p].split(",")[0].trim();
                        String cityStr = myCityArray[p].split(",")[1].trim();
                        cityCode = mApplication.getNumberByCityAndProvince(cityStr, provinceStr);
                        mApplication.removeMycity(cityCode);
                        myCityArray = mApplication.getMycity();
                        Log.d("mycity", "after delete" + myCityArray.toString());
                        ArrayAdapter a = new ArrayAdapter<String>(CityManagerActivity.this, android.R.layout.simple_list_item_1, myCityArray);
                        cityListView.setAdapter(a);
                    }
                }).setNegativeButton("返回", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).show();

                return true;
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_city_manager, menu);
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
            String cityCode = intent.getStringExtra(SelectCityActivity.CITY_CODE);
            mApplication.insertNewMycity(cityCode);
            myCityArray = mApplication.getMycity();
            ArrayAdapter a = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, myCityArray);
            cityListView.setAdapter(a);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.city_manager_title_back:
                finish();
                break;
            case R.id.city_manager_title_newcity:
                Intent intent = new Intent(this, SelectCityActivity.class);
                startActivityForResult(intent, 1);
                break;
            default:
                break;
        }
    }
}
