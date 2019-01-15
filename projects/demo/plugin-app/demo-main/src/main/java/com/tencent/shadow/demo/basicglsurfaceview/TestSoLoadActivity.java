package com.tencent.shadow.demo.basicglsurfaceview;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

import com.tencent.sealsplatformteam.cppsdk.SealsJNI;

public class TestSoLoadActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Intent intent = getIntent();
        final String test = intent.getStringExtra("TEST");

        final Button button = new Button(this);
        button.setText(test);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final SealsJNI sealsJNI = new SealsJNI();
                button.setText("so返回版本号:" + SealsJNI.SDK_VERSION);

                setResult(888);
                finish();
            }
        });
        setContentView(button);
    }
}
