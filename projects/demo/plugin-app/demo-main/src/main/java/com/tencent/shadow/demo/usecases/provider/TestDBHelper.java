package com.tencent.shadow.demo.usecases.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class TestDBHelper extends SQLiteOpenHelper{
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "shadow.db";

    public TestDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_CONTACT_TABLE = "CREATE TABLE " + TestProviderInfo.TestEntry.TABLE_NAME + "( "
                + TestProviderInfo.TestEntry._ID + " TEXT PRIMARY KEY, "
                + TestProviderInfo.TestEntry.COLUMN_NAME + " TEXT NOT NULL );";

        db.execSQL(SQL_CREATE_CONTACT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TestProviderInfo.TestEntry.TABLE_NAME);
        onCreate(db);
    }
}
