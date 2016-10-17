package com.foryou.truck.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dubin on 16/7/18.
 */
public class DataHelper {
    private static String DB_NAME = "foryou.db";
    // 数据库版本
    private static int DB_VERSION = 1;
    private SQLiteDatabase db;
    private SqliteHelper dbHelper;

    public DataHelper(Context context) {
        dbHelper = new SqliteHelper(context, DB_NAME, null, DB_VERSION);
        db = dbHelper.getWritableDatabase();
    }

    public void Close() {
        db.close();
        dbHelper.close();
    }

    // 获取users表中的UserID、Access Token、Access Secret的记录
    public List<LocationInfo> getLocationInfo(String orderid) {
        List<LocationInfo> userList = new ArrayList<LocationInfo>();
        Cursor cursor = db.query(SqliteHelper.TB_NAME, null, "order_id=?", new  String[]{ "orderid" }, null,
                null, LocationInfo.ID + " DESC");
        cursor.moveToFirst();
        while (!cursor.isAfterLast() && (cursor.getString(1) != null)) {
            LocationInfo user = new LocationInfo();
            user.id = cursor.getString(0);
            user.orderid = cursor.getString(1);
            user.lng = cursor.getString(2);
            user.lat = cursor.getString(3);
            user.location = cursor.getString(4);
            userList.add(user);
            cursor.moveToNext();
        }
        cursor.close();
        return userList;
    }

    // 添加users表的记录
    public Long SaveUserInfo(LocationInfo user) {
        ContentValues values = new ContentValues();
        values.put(LocationInfo.ORDERID, user.orderid);
        values.put(LocationInfo.LAT, user.lat);
        values.put(LocationInfo.LNG, user.lng);
        values.put(LocationInfo.LOCATION, user.location);
        Long uid = db.insert(SqliteHelper.TB_NAME, LocationInfo.ID, values);
        Log.e("SaveUserInfo", uid + "");
        return uid;
    }

    // // 添加users表的记录
    // public Long SaveUserInfo(UserInfo user, byte[] icon) {
    // ContentValues values = new ContentValues();
    // values.put(DriverInfo.USERID, user.getUserId());
    // values.put(UserInfo.USERNAME, user.getUserName());
    // values.put(UserInfo.TOKEN, user.getToken());
    // values.put(UserInfo.TOKENSECRET, user.getTokenSecret());
    // if (icon != null) {
    // values.put(UserInfo.USERICON, icon);
    // }
    // Long uid = db.insert(SqliteHelper.TB_NAME, UserInfo.ID, values);
    // Log.e("SaveUserInfo", uid + "");
    // return uid;
    // }

    // 删除users表的记录
    public int DelUserInfo(String orderid) {
        int id = db.delete(SqliteHelper.TB_NAME, LocationInfo.ORDERID + "=?",
                new String[] { orderid });
        Log.e("DelUserInfo", id + "");
        return id;
    }

    // 删除所有shu ju
    public void DelAllData() {
        int id = db.delete(SqliteHelper.TB_NAME, LocationInfo.ORDERID + "!=?",
                new String[] { "" });
    }
}
