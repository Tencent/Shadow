package com.tencent.shadow.demo.usecases.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * TestProvider
 * Created by 90Chris on 2016/5/1.
 */
public class TestProvider extends ContentProvider{
    private TestDBHelper mOpenHelper;

    @Override
    public boolean onCreate() {
        mOpenHelper = new TestDBHelper(getContext());
        return true;
    }


    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        final SQLiteDatabase db = mOpenHelper.getReadableDatabase();

        Cursor cursor = null;
        switch ( buildUriMatcher().match(uri)) {
            case TEST:
                cursor = db.query(TestProviderInfo.TestEntry.TABLE_NAME, projection, selection, selectionArgs, sortOrder, null, null);
                break;
        }

        return cursor;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        Uri returnUri;
        long _id;
        switch ( buildUriMatcher().match(uri)) {
            case TEST:
                _id = db.insert(TestProviderInfo.TestEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = TestProviderInfo.TestEntry.buildUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            default:
                throw new android.database.SQLException("Unknown uri: " + uri);
        }
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }

    private final static int TEST = 100;

    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = TestProviderInfo.CONTENT_AUTHORITY;

        matcher.addURI(authority, TestProviderInfo.PATH_TEST, TEST);

        return matcher;
    }
}
