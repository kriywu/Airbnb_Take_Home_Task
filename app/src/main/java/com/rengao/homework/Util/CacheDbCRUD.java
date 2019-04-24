package com.rengao.homework.Util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.rengao.homework.Module.Project;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据库帮助类，封装CRUD操作
 * 使用DCL单例模式
 */

public class CacheDbCRUD {
    private static volatile CacheDbCRUD cacheDbCRUD;
    private static volatile MyDatabaseHelper dbHelper;

    private CacheDbCRUD(Context context) {
        this(context, 1);
    }

    private CacheDbCRUD(Context context, int version) {
        dbHelper = new MyDatabaseHelper(context, Constants.DB_NAME, null, version);
    }

    public static CacheDbCRUD getInstance(Context context) {
        if (dbHelper == null) {
            synchronized (CacheDbCRUD.class) {
                if (dbHelper == null) {
                    cacheDbCRUD = new CacheDbCRUD(context);
                }
            }
        }
        return cacheDbCRUD;
    }

    /*
     * 查询name的page页内容
     */
    public List<Project> query(String name, int page) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        Cursor cursor = database.query(Constants.DB_TABLE_CACHE,
                null,
                "name = ? and page = ? ",
                new String[]{name,String.valueOf(page)},
                null,
                null,
                null);
        List<Project> projects = new ArrayList<>();
        while (cursor.moveToNext()) {
            Project project = new Project(cursor.getString(cursor.getColumnIndex("name")));
            project.star = cursor.getInt(cursor.getColumnIndex("star"));
            project.avatar = cursor.getString(cursor.getColumnIndex("avatar"));
            projects.add(project);
        }
        cursor.close();
        database.close();
        return projects;
    }

    public void insertOne(List<Project> projects, int page) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        for (Project project : projects) {
            values.put("name", project.name);
            values.put("page", page);
            values.put("star", project.star);
            values.put("avatar", project.avatar);
            database.insert(Constants.DB_TABLE_CACHE, null, values);
        }
        database.close();
    }

    /**
     * 清楚所有为name的内容
     *
     * @param name
     */
    public void clearOne(String name) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        database.delete(Constants.DB_TABLE_CACHE, "name = ?", new String[]{name});
        database.close();
    }

    /**
     * 清空表格
     */
    public void clearAll() {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        database.delete(Constants.DB_TABLE_CACHE, null, null);
        database.close();
    }

}
