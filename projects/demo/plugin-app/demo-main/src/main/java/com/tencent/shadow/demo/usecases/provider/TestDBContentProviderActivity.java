package com.tencent.shadow.demo.usecases.provider;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.shadow.demo.gallery.BaseActivity;
import com.tencent.shadow.demo.gallery.R;

public class TestDBContentProviderActivity extends BaseActivity {

    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_provider_db);

        mTextView = findViewById(R.id.text);

    }

    public void insert(View view){
        ContentValues contentValues = new ContentValues();
        contentValues.put(TestProviderInfo.TestEntry.COLUMN_NAME, "test");
        contentValues.put(TestProviderInfo.TestEntry._ID, System.currentTimeMillis());
        getContentResolver().insert(TestProviderInfo.TestEntry.CONTENT_URI, contentValues);
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
}
