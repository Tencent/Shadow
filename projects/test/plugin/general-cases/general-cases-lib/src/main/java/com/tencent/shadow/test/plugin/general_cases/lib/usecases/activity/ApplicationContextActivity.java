package com.tencent.shadow.test.plugin.general_cases.lib.usecases.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;

public class ApplicationContextActivity extends Activity {

    private boolean noError = false;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        Context applicationContext = getApplicationContext();
        noError = applicationContext != null;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TextView textView = new TextView(this);
        textView.setText(Boolean.toString(noError));
        textView.setTag("noError");
        setContentView(textView);
    }
}
