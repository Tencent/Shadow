package com.tencent.shadow.demo.usecases.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.TextView;

import com.tencent.shadow.demo.gallery.R;
import com.tencent.shadow.demo.gallery.cases.entity.UseCase;
import com.tencent.shadow.demo.gallery.util.ToastUtil;
import com.tencent.shadow.demo.usecases.BaseAndroidTestActivity;

public class TestStartServiceActivity extends BaseAndroidTestActivity {

    public static class Case extends UseCase {
        @Override
        public String getName() {
            return "启动Service";
        }

        @Override
        public String getSummary() {
            return "测试startService,bindService,stopService,unBindService等调用";
        }

        @Override
        public Class getPageClass() {
            return TestStartServiceActivity.class;
        }
    }

    private Intent serviceIntent ;

    private TestService.MyLocalServiceBinder binder;

    private TextView mTextView;

    public final static String INTENT_ACTION = "com.tencent.shadow.test.service";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        serviceIntent = new Intent(this, TestService.class);
        setContentView(R.layout.layout_service);
        mTextView = findViewById(R.id.tv_msg);
        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver,new IntentFilter(INTENT_ACTION));
    }

    public void start(View view) {
        mIdlingResource.setIdleState(false);
        startService(serviceIntent);
    }

    public void bind(View view) {
        mIdlingResource.setIdleState(false);
        bindService(serviceIntent, serviceConnection, Service.BIND_AUTO_CREATE);
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            binder = (TestService.MyLocalServiceBinder) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            binder = null;
        }
    };

    public void stop(View view) {
        mIdlingResource.setIdleState(false);
        stopService(serviceIntent);
    }

    public void unbind(View view) {
        mIdlingResource.setIdleState(false);
        unbindService(serviceConnection);
    }

    public void testBinder(View view) {
        if (binder == null) {
            ToastUtil.showToast(this, "请先bindService");
        } else {
            binder.getMyLocalService().test();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiver);
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String text = intent.getStringExtra("result");
            mTextView.setText(text);
            mIdlingResource.setIdleState(true);
        }
    };
}
