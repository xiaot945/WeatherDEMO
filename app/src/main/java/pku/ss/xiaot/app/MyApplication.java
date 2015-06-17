package pku.ss.xiaot.app;

import android.app.Application;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import pku.ss.xiaot.bean.City;
import pku.ss.xiaot.db.CityDB;

/**
 * Created by xiaot on 2015/3/29.
 */
public class MyApplication extends Application {

    private static final String TAG = "MyApplication";

    private static Application mApplication;

    private List<City> cityList = null;
    private CityDB mCityDB = null;

    private static String[] allProvince = null;

    public List<City> getCityList() {
        return cityList;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(this.TAG, "-> onCreate");
        mApplication = this;
        mCityDB = openCityDB();
        initCityList();
    }

    public static Application getInstance() {
        return mApplication;
    }

    private CityDB openCityDB() {
        String dbDir = "/data"
                + Environment.getDataDirectory().getAbsolutePath()
                + File.separator + getPackageName()
                + File.separator + "databases"
                + File.separator
                + CityDB.CITY_DB_NAME;
        if (android.os.Build.VERSION.SDK_INT >= 4.2) {
            dbDir = this.getApplicationInfo().dataDir + "/databases";
        } else {
            dbDir = "/data/data/" + this.getPackageName() + "/databases";
        }
        File db = new File(dbDir);
        Log.d(TAG, dbDir);
        String path = dbDir + "/" + CityDB.CITY_DB_NAME;
        File dbFile = new File(path);
        if (!dbFile.exists()) {
            Log.i(TAG, "DB is not exists");
            try {
                InputStream is = new BufferedInputStream(getAssets().open("city.db"));
                db.mkdirs();
                dbFile.createNewFile();
                OutputStream fos = new BufferedOutputStream(new FileOutputStream(dbFile));
                int len = -1;
                byte[] buffer = new byte[1024];
                while ((len = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, len);
//                    fos.flush();
                }
                fos.close();
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(0);
            }
        }
        return new CityDB(this, path);
    }

    private boolean prepareCityList() {
        cityList = mCityDB.getAllCity();
        for (City c : cityList) {
            Log.d(TAG, c.getCity());
        }
        return true;
    }

    private void initCityList() {
        cityList = new ArrayList<City>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                prepareCityList();
            }
        }).start();
    }

    public String[] getAllProvince() {
        if (allProvince == null) {
            allProvince = mCityDB.getAllProvice();
        }
        return allProvince;
    }

    public String[] getCityByProvince(String province) {
        return mCityDB.getCityByProvince(province);
    }

    public String getNumberByCityAndProvince(String city, String province) {
        return mCityDB.getNumberByCityAndProvince(city, province);
    }

    public String getNumberByCity(String city) {
        return mCityDB.getNumberByCity(city);
    }

    public String[] getCityByName(String name) {
        return mCityDB.getCityByName(name);
    }

    public String[] getMycity() {
        return mCityDB.getMycity();
    }

    public void insertNewMycity(String cityCode) {
        mCityDB.insertNewMyCity(cityCode);
    }

    public void removeMycity(String cityCode) {
        mCityDB.removeMycity(cityCode);
    }
}
