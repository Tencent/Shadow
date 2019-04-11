package com.tencent.shadow.demo.usecases.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

import com.tencent.shadow.demo.gallery.BaseActivity;
import com.tencent.shadow.demo.gallery.R;
import com.tencent.shadow.demo.gallery.cases.entity.UseCase;
import com.tencent.shadow.demo.gallery.util.ToastUtil;

public class TestDynamicReceiverActivity extends BaseActivity {

    public static class Case extends UseCase {
        @Override
        public String getName() {
            return "动态广播测试";
        }

        @Override
        public String getSummary() {
            return "测试动态广播的发送和接收是否工作正常";
        }

        @Override
        public Class getPageClass() {
            return TestDynamicReceiverActivity.class;
        }
    }

    private final static String INTENT_ACTION = "com.tencent.test.action.DYNAMIC";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_receiver);
        Button button = findViewById(R.id.button);
        button.setText("测试动态广播发送");

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(INTENT_ACTION);
                intent.putExtra("msg", "收到测试动态广播发送");
                sendBroadcast(intent);
            }
        });

        registerReceiver(new DynamicBroadcastReceiver(), new IntentFilter(INTENT_ACTION));
    }


    private class DynamicBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String msg = intent.getStringExtra("msg");
            ToastUtil.showToast(context, msg);
        }
    }

}
