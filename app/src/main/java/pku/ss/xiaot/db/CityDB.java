package pku.ss.xiaot.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import pku.ss.xiaot.bean.City;
import pku.ss.xiaot.util.StaticFunction;

/**
 * Created by xiaot on 2015/3/29.
 */
public class CityDB {

    public static final String CITY_DB_NAME = "city2.db";
    private static final String CITY_TABLE_NAME = "city";
    private SQLiteDatabase db;

    public CityDB(Context context, String path) {
        db = context.openOrCreateDatabase(CITY_DB_NAME, Context.MODE_PRIVATE, null);
    }

    public List<City> getAllCity() {
        List<City> list = new ArrayList<City>();
        Cursor c = db.rawQuery("SELECT * FROM " + CITY_TABLE_NAME, null);
        while (c.moveToNext()) {
            String province = c.getString(c.getColumnIndex("province"));
            String city = c.getString(c.getColumnIndex("city"));
            String number = c.getString(c.getColumnIndex("number"));
            String allPY = c.getString(c.getColumnIndex("allpy"));
            String allFirstPY = c.getString(c.getColumnIndex("allfirstpy"));
            String firstPY = c.getString(c.getColumnIndex("firstpy"));
            City cityItem = new City(province, city, number, allPY, allFirstPY, firstPY);
            list.add(cityItem);
        }
        return list;
    }

    public String[] getAllProvice() {
        List<String> list = new ArrayList<String>();

        Cursor c = db.rawQuery("SELECT DISTINCT province FROM " + CITY_TABLE_NAME, null);
        while (c.moveToNext()) {
            String province = c.getString(c.getColumnIndex("province"));
            list.add(province);
        }
        return StaticFunction.list2StringArray(list);
    }

    public String[] getCityByProvince(String province) {
        List<String> list = new ArrayList<String>();
        Cursor c = db.rawQuery("SELECT city FROM " + CITY_TABLE_NAME + " WHERE province=?", new String[]{province});
        while (c.moveToNext()) {
            String p = c.getString(c.getColumnIndex("city"));
            list.add(p);
        }
        return StaticFunction.list2StringArray(list);
    }

    public String getNumberByCityAndProvince(String city, String province) {
        Cursor c = db.rawQuery("SELECT number FROM " + CITY_TABLE_NAME + " WHERE city=? AND province=?", new String[]{city, province});
        String n = "000000000";
        while (c.moveToNext()) {
            n = c.getString(c.getColumnIndex("number"));
        }
        return n;
    }

    public String[] getCityByName(String name) {
        List<String> list = new ArrayList<String>();
        Cursor c = db.rawQuery("SELECT city, province FROM " + CITY_TABLE_NAME + " WHERE city LIKE ?", new String[]{"%" + name + "%"});
        while (c.moveToNext()) {
            String city = c.getString(c.getColumnIndex("city"));
            String province = c.getString(c.getColumnIndex("province"));
            list.add(province + ", " + city);
        }
        return StaticFunction.list2StringArray(list);
    }

    public String getNumberByCity(String city) {
        Cursor c = db.rawQuery("SELECT number FROM " + CITY_TABLE_NAME + " WHERE city=?", new String[]{city});
        String n = "000000000";
        while (c.moveToNext()) {
            n = c.getString(c.getColumnIndex("number"));
        }
        return n;
    }
}
