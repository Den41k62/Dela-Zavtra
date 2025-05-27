package com.example.todoapp.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "todo.db";
    private static final int DATABASE_VERSION = 2;

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TaskContract.TaskEntry.TABLE_NAME + " (" +
                    TaskContract.TaskEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    TaskContract.TaskEntry.COLUMN_TITLE + " TEXT NOT NULL," +
                    TaskContract.TaskEntry.COLUMN_DESCRIPTION + " TEXT," +
                    TaskContract.TaskEntry.COLUMN_DUE_DATE + " INTEGER," +
                    TaskContract.TaskEntry.COLUMN_COMPLETED + " INTEGER DEFAULT 0," +
                    TaskContract.TaskEntry.COLUMN_CATEGORY + " TEXT," +
                    TaskContract.TaskEntry.COLUMN_PRIORITY + " INTEGER," +
                    TaskContract.TaskEntry.COLUMN_IMAGE_PATH + " TEXT)";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TaskContract.TaskEntry.TABLE_NAME);
        onCreate(db);
    }
}