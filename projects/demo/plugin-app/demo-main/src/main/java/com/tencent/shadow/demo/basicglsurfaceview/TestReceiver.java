package com.tencent.shadow.demo.basicglsurfaceview;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.tencent.shadow.demo.main.R;

public class TestReceiver extends Activity {
    public final String action ="testBroadCast";
    private TestBroadcastReceiver mBroadcastReceiver;
    @Override
    protected void onResume() {
        super.onResume();
        mBroadcastReceiver = new TestBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(action);
        registerReceiver(mBroadcastReceiver, intentFilter);
    }
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mBroadcastReceiver);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_receiver);
    }

    public class TestBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(context,  intent.getStringExtra("msg"), Toast.LENGTH_SHORT).show();
        }
    }

    public void sendBroadCast(View view) {
        Intent intent=new Intent();
        intent.setAction(action);
        intent.putExtra("msg", "dynamic broadcast");
        sendBroadcast(intent);
    }
}
