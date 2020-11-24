package com.example.before;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.HashMap;
import java.util.Map;

public class FillActivity extends Activity {

    /* renamed from: db */
    private SQLiteDatabase f5db = null;
    private MyDBOpenHelper myDBOpenHelper = null;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fill);
        deal();
    }

    private void deal() {
        final EditText editText1 = (EditText) findViewById(R.id.editText);
        final EditText editText2 = (EditText) findViewById(R.id.editText2);
        final EditText editText3 = (EditText) findViewById((R.id.editText3));
        Button editButton = (Button) findViewById(R.id.button);
        Map<String, String> map = queryFromDB();
        if (map.size() >= 3) {
            editText1.setText(map.get("name"));
            editText2.setText(map.get("phone"));
            editText3.setText(map.get("reason"));
        } else {
            editText1.setText("输入名字");
            editText2.setText("输入电话");
            editText3.setText("输入请假原因");
        }
        editButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String name = editText1.getText().toString();
                String phone = editText2.getText().toString();
                String reason = editText3.getText().toString();
                Log.d("修改界面", name + "  " + phone + " " + reason);
                FillActivity.this.insertToDb(name, phone, reason);
                FillActivity.this.startActivity(new Intent(FillActivity.this, MainActivity.class));
            }
        });
    }

    /* access modifiers changed from: private */
    public void insertToDb(String name, String phone, String reason) {
        MyDBOpenHelper myDBOpenHelper2 = new MyDBOpenHelper(MainActivity.context, "ms.db", (SQLiteDatabase.CursorFactory) null, 1);
        this.myDBOpenHelper = myDBOpenHelper2;
        this.f5db = myDBOpenHelper2.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("phone", phone);
        contentValues.put("reason", reason);
        this.f5db.insert("lang", (String) null, contentValues);
    }

    public Map<String, String> queryFromDB() {
        Map<String, String> map = new HashMap<>();
        MyDBOpenHelper myDBOpenHelper2 = new MyDBOpenHelper(MainActivity.context, "ms.db", (SQLiteDatabase.CursorFactory) null, 1);
        this.myDBOpenHelper = myDBOpenHelper2;
        SQLiteDatabase writableDatabase = myDBOpenHelper2.getWritableDatabase();
        this.f5db = writableDatabase;
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
