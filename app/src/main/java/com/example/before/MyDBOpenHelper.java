package com.example.before;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/* renamed from: com.example.before.db.MyDBOpenHelper */
public class MyDBOpenHelper extends SQLiteOpenHelper {
    public MyDBOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, (SQLiteDatabase.CursorFactory) null, version);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS config (id integer primary key autoincrement, num integer)");
        db.execSQL("CREATE TABLE lang(id INTEGER PRIMARY KEY AUTOINCREMENT,name VARCHAR(20), phone INTEGER, reason VARCHAR(20))");
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
