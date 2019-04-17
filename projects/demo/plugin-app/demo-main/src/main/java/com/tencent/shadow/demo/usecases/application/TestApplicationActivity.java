package com.tencent.shadow.demo.usecases.application;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.tencent.shadow.demo.gallery.BaseActivity;
import com.tencent.shadow.demo.gallery.DemoApplication;
import com.tencent.shadow.demo.gallery.R;

public class TestApplicationActivity extends BaseActivity {

    private TextView mText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_common);
        mText = findViewById(R.id.text);
        mText.setText("isCallOnCreate:"+ DemoApplication.getInstance().isOnCreate);
    }

    public void doClick(View view){

    }
}
