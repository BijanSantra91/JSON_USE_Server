package com.example.bijan.json_ex1;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Bijan on 1/23/2017.
 */

public class MyDatabase {

    MyHelper myHelper;
    SQLiteDatabase sqLiteDatabase;

    public MyDatabase(Context context){
        myHelper = new MyHelper(context, "data.db", null, 1);
    }

    public void open(){
        sqLiteDatabase = myHelper.getWritableDatabase();
    }

    public void inserData(String name, String email, String mobile){
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("email", email);
        contentValues.put("mobile", mobile);
        sqLiteDatabase.insert("data", null, contentValues);
    }

    public Cursor quaryData(){
        Cursor cursor = null;
        cursor = sqLiteDatabase.query("data", null, null, null, null, null, null);
        return cursor;
    }

    public void close(){
        sqLiteDatabase.close();
    }

    private  class MyHelper extends SQLiteOpenHelper{

        public MyHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL("create table data(_id integer primary key, name text, email text, mobile text);");
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        }
    }
}
