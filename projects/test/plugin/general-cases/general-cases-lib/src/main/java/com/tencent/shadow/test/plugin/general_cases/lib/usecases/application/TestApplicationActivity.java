package com.tencent.shadow.test.plugin.general_cases.lib.usecases.application;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.tencent.shadow.test.plugin.general_cases.lib.R;
import com.tencent.shadow.test.plugin.general_cases.lib.gallery.BaseActivity;
import com.tencent.shadow.test.plugin.general_cases.lib.gallery.TestApplication;

public class TestApplicationActivity extends BaseActivity {

    private TextView mText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_common);
        mText = findViewById(R.id.text);
        mText.setText("isCallOnCreate:" + TestApplication.getInstance().isOnCreate);
    }

    public void doClick(View view){

    }
}
