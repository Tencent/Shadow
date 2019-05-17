package com.tencent.shadow.test.plugin.general_cases.lib.usecases.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.tencent.shadow.test.plugin.general_cases.lib.R;
import com.tencent.shadow.test.plugin.general_cases.lib.gallery.cases.entity.UseCase;
import com.tencent.shadow.test.plugin.general_cases.lib.gallery.util.ToastUtil;
import com.tencent.shadow.test.plugin.general_cases.lib.usecases.BaseAndroidTestActivity;

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

    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        serviceIntent = new Intent(this, TestService.class);
        setContentView(R.layout.layout_service);
        mTextView = findViewById(R.id.tv_msg);
        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver,new IntentFilter(INTENT_ACTION));
    }

    public void start(View view) {
        setIdle();
        startService(serviceIntent);
    }

    public void bind(View view) {
        setIdle();
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
        setIdle();
        stopService(serviceIntent);
    }

    public void unbind(View view) {
        setIdle();
        unbindService(serviceConnection);
    }

    public void testBinder(View view) {
        setIdle();
        if (binder == null) {
            ToastUtil.showToast(this, "请先bindService");
        } else {
            binder.getMyLocalService().test();
        }
    }

    private void setIdle(){
        mHandler.removeCallbacksAndMessages(null);
        mIdlingResource.setIdleState(false);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mIdlingResource.setIdleState(true);
            }
        },2000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiver);
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mHandler.removeCallbacksAndMessages(null);
            String text = intent.getStringExtra("result");
            String oldText = mTextView.getText().toString();
            if(!TextUtils.isEmpty(oldText)){
                text = oldText+"-"+text;
            }
            mTextView.setText(text);
            mIdlingResource.setIdleState(true);
        }
    };
}
