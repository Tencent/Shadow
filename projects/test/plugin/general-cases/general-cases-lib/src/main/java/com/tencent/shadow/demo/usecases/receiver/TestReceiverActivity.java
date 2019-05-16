package com.tencent.shadow.demo.usecases.receiver;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

import com.tencent.shadow.demo.gallery.BaseActivity;
import com.tencent.shadow.demo.gallery.R;
import com.tencent.shadow.demo.gallery.cases.entity.UseCase;

public class TestReceiverActivity extends BaseActivity {

    public static class Case extends UseCase {
        @Override
        public String getName() {
            return "静态广播测试";
        }

        @Override
        public String getSummary() {
            return "测试静态广播的发送和接收是否工作正常";
        }

        @Override
        public Class getPageClass() {
            return TestReceiverActivity.class;
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_receiver);
        Button button = findViewById(R.id.button);
        button.setText("测试静态广播发送");

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("com.tencent.test.action");
                intent.putExtra("msg", "收到测试静态广播发送");
                sendBroadcast(intent);
            }
        });
    }

}
