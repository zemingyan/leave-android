package com.example.before;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.webkit.JavascriptInterface;

import java.util.HashMap;
import java.util.Map;

public class AndroidtoJs {

    /* renamed from: db */
    private SQLiteDatabase f4db = null;
    private MyDBOpenHelper myDBOpenHelper = null;

    @JavascriptInterface
    public String hello(String msg) {
        System.out.println("JS调用了Android的hello方法");
        Log.d("测试调用", "==============================================" + msg);
        return "变化后";
    }

    @JavascriptInterface
    public String getName(String name) {
        return queryFromDB().get("name");
    }

    @JavascriptInterface
    public String getPhone(String name) {
        return queryFromDB().get("phone");
    }

    @JavascriptInterface
    public String getReason(String name) {
        return queryFromDB().get("reason");
    }

    @JavascriptInterface
    public String getImage(String name) {
        Log.d("返回销假图片路径", "======  " + CommontUtils.WEB_FILE_PATH);
        return CommontUtils.WEB_FILE_PATH;
    }

    public Map<String, String> queryFromDB() {
        Map<String, String> map = new HashMap<>();
        MyDBOpenHelper myDBOpenHelper2 = new MyDBOpenHelper(MainActivity.context, "ms.db", (SQLiteDatabase.CursorFactory) null, 1);
        this.myDBOpenHelper = myDBOpenHelper2;
        SQLiteDatabase writableDatabase = myDBOpenHelper2.getWritableDatabase();
        this.f4db = writableDatabase;
        Cursor cursor = writableDatabase.rawQuery("SELECT * FROM lang ORDER BY id DESC LIMIT 1", (String[]) null);
        if (cursor.moveToFirst()) {
            String name = cursor.getString(cursor.getColumnIndex("name"));
            String phone = cursor.getString(cursor.getColumnIndex("phone"));
            String reason = cursor.getString(cursor.getColumnIndex("reason"));
            Log.d("读取数据", Integer.valueOf(cursor.getInt(cursor.getColumnIndex("id"))) + " " + name + "  " + phone + "  " + reason);
            map.put("name", name);
            map.put("phone", phone);
            map.put("reason", reason);
        }
        return map;
    }
}
