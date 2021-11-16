/*
 * Tencent is pleased to support the open source community by making Tencent Shadow available.
 * Copyright (C) 2019 THL A29 Limited, a Tencent company.  All rights reserved.
 *
 * Licensed under the BSD 3-Clause License (the "License"); you may not use
 * this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 *     https://opensource.org/licenses/BSD-3-Clause
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.tencent.shadow.sample.plugin.app.lib.usecases.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
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

    @SuppressWarnings("ConstantConditions")
    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        Uri returnUri;
        long _id;
        switch (buildUriMatcher().match(uri)) {
            case TEST:
                _id = db.insert(TestProviderInfo.TestEntry.TABLE_NAME, null, values);
                if (_id > 0) {
                    returnUri = TestProviderInfo.TestEntry.buildUri(_id);
                    getContext().getContentResolver().notifyChange(returnUri, null);
                } else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            default:
                throw new android.database.SQLException("Unknown uri: " + uri);
        }
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int result = db.delete(TestProviderInfo.TestEntry.TABLE_NAME, selection, selectionArgs);
        if (result > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return result;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int result = db.update(TestProviderInfo.TestEntry.TABLE_NAME, values, selection, selectionArgs);
        if (result > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return result;
    }

    public Bundle call(@NonNull String method, String arg, @Nullable Bundle extras) {
        switch (method) {
            case "getBeauty":
                Bundle bundle = new Bundle();
                bundle.putString("name", "Anne Hathaway");
                return bundle;
        }
        return null;
    }

    private final static int TEST = 100;

    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = TestProviderInfo.CONTENT_AUTHORITY;

        matcher.addURI(authority, TestProviderInfo.PATH_TEST, TEST);

        return matcher;
    }
}
