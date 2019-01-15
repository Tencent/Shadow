package com.tencent.shadow.demo.basicglsurfaceview;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

import test.ClassBothInHostAndPlugin;

public class TestClassConflictActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Button button = new Button(this);
        button.setText("测试宿主和插件同名类冲突");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button.setText(ClassBothInHostAndPlugin.test());
            }
        });
        setContentView(button);
    }
}
