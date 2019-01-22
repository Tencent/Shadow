package com.tencent.shadow.demo.usecases.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.tencent.shadow.demo.gallery.R;

public class TestActivityReCreateBySystem extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_re_create_by_system);
        String url = "url : " + getIntent().getStringExtra("url");
        ((TextView) findViewById(R.id.url_tv)).setText(url);
    }
}