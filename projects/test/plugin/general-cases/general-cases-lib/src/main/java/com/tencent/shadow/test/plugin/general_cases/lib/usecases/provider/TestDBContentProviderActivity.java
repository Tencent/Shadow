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

package com.tencent.shadow.test.plugin.general_cases.lib.usecases.provider;

import android.app.Activity;
import android.content.ContentValues;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.shadow.test.plugin.general_cases.lib.R;

public class TestDBContentProviderActivity extends Activity {

    private static final String TAG = "ContentProviderActivity";

    private TextView mTextView;

    private Handler mHandler = new Handler();
    private ContentObserver mObserver = new ContentObserver(mHandler) {
        @Override
        public void onChange(boolean selfChange, Uri uri) {
            super.onChange(selfChange, uri);
            Log.d(TAG, uri + " onChange");
            Toast.makeText(TestDBContentProviderActivity.this, uri + " onChange", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_provider_db);

        mTextView = findViewById(R.id.text);

        getContentResolver().registerContentObserver(TestProviderInfo.TestEntry.CONTENT_URI,
                false, mObserver);
    }

    public void insert(View view){
        ContentValues contentValues = new ContentValues();
        contentValues.put(TestProviderInfo.TestEntry.COLUMN_NAME, "test");
        contentValues.put(TestProviderInfo.TestEntry._ID, System.currentTimeMillis());
        getContentResolver().insert(TestProviderInfo.TestEntry.CONTENT_URI, contentValues);

        query(view);
    }

    public void query(View view){
        Cursor cursor = getContentResolver().query(TestProviderInfo.TestEntry.CONTENT_URI, null, null, null, null);
        if(cursor != null){
            StringBuilder s = new StringBuilder();
            while (cursor.moveToNext()){
                long id = cursor.getLong(cursor.getColumnIndex(TestProviderInfo.TestEntry._ID));
                String name = cursor.getString(cursor.getColumnIndex(TestProviderInfo.TestEntry.COLUMN_NAME));
                s.append("id:").append(id).append(" name:").append(name).append(" \n");
            }
            mTextView.setText(s);
            cursor.close();
        }else {
            Toast.makeText(this,"请先插入数据",Toast.LENGTH_SHORT).show();
        }

    }

    public void update(View view) {
        Cursor cursor = getContentResolver().query(TestProviderInfo.TestEntry.CONTENT_URI,
                null, null, null, null);
        int count = cursor != null ? cursor.getCount() : 0;
        if (count > 0) {
            cursor.moveToFirst();
            ContentValues contentValues = new ContentValues();
            contentValues.put(TestProviderInfo.TestEntry.COLUMN_NAME, "name " + System.currentTimeMillis());

            long id = cursor.getLong(cursor.getColumnIndex(TestProviderInfo.TestEntry._ID));
            getContentResolver().update(TestProviderInfo.TestEntry.CONTENT_URI, contentValues,
                    TestProviderInfo.TestEntry._ID + " = ?",
                    new String[]{String.valueOf(id)});
        }
        if (cursor != null) {
            cursor.close();
        }

        query(view);
    }

    public void delete(View view) {
        Cursor cursor = getContentResolver().query(TestProviderInfo.TestEntry.CONTENT_URI,
                null, null, null, null);
        int count = cursor != null ? cursor.getCount() : 0;
        if (count > 0) {
            cursor.moveToFirst();
            long id = cursor.getLong(cursor.getColumnIndex(TestProviderInfo.TestEntry._ID));
            getContentResolver().delete(TestProviderInfo.TestEntry.CONTENT_URI,
                    TestProviderInfo.TestEntry._ID + " = ?",
                    new String[]{String.valueOf(id)});
        }
        if (cursor != null) {
            cursor.close();
        }

        query(view);
    }

    public void bulkInsert(View view) {
        ContentValues[] values = new ContentValues[3];
        ContentValues contentValues = new ContentValues();
        contentValues.put(TestProviderInfo.TestEntry.COLUMN_NAME, "test");
        contentValues.put(TestProviderInfo.TestEntry._ID, System.currentTimeMillis());
        values[0] = contentValues;

        contentValues = new ContentValues();
        contentValues.put(TestProviderInfo.TestEntry.COLUMN_NAME, "test");
        contentValues.put(TestProviderInfo.TestEntry._ID, System.currentTimeMillis() + 5);
        values[1] = contentValues;

        contentValues = new ContentValues();
        contentValues.put(TestProviderInfo.TestEntry.COLUMN_NAME, "test");
        contentValues.put(TestProviderInfo.TestEntry._ID, System.currentTimeMillis() + 10);
        values[2] = contentValues;

        getContentResolver().bulkInsert(TestProviderInfo.TestEntry.CONTENT_URI, values);

        query(view);
    }

    public void call(View view) {
        Bundle beauty = getContentResolver().call(TestProviderInfo.TestEntry.CONTENT_URI, "getBeauty", "18", null);
        if (beauty != null) {
            Toast.makeText(this, "get beauty who name is " + beauty.getString("name"), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getContentResolver().unregisterContentObserver(mObserver);
    }
}
