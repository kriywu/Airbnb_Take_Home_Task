package com.rengao.homework.Util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

/**
 * 应用再searchview中查询的内容 优先查询数据库
 * 下拉刷新时候更新数据库
 * 数据库没找到的时候更新数据库
 * 应用关闭的时候，clear数据库
 */
public class MyDatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "MyDatabaseHelper";

    // 复合主键
    private static final String CREATE_CACHE = "create table " + Constants.DB_TABLE_CACHE + " ("
            + "name text,"
            + "page integer,"
            + "star integer,"
            + "avatar text,"
            + "constraint id primary key (name,page))";

    public MyDatabaseHelper(@Nullable Context context,
                            @Nullable String name,
                            @Nullable SQLiteDatabase.CursorFactory factory,
                            int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_CACHE);
        Log.d(TAG, "onCreate: create db success");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
