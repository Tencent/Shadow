package com.tencent.shadow.demo.basicglsurfaceview;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.view.View;

import com.tencent.shadow.demo.main.R;
import com.tencent.shadow.demo.service.MyForegroundService;
import com.tencent.shadow.demo.service.MyIntentService;
import com.tencent.shadow.demo.service.MyLocalService;

public class ServiceTestActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar();
        setContentView(R.layout.layout_test_service);
    }

    public void callMyIntentServiceFoo(View view) {
        MyIntentService.startActionFoo(this, "A", "B");
    }

    public void callMyIntentServiceBaz(View view) {
        MyIntentService.startActionBaz(this, "C", "D");
    }

    public void callMyLocalService(View view) {
        Intent intent = new Intent(this, MyLocalService.class);
        bindService(intent, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                final MyLocalService.MyLocalServiceBinder service1 = (MyLocalService.MyLocalServiceBinder) service;
                service1.getMyLocalService().test();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        }, Context.BIND_AUTO_CREATE);
    }

    private MyForegroundService.MyLocalServiceBinder mMyForegroundServiceBinder;
    private ServiceConnection mMyForegroundServiceSC;

    public void callMyForegroundService(View view) {
        if (mMyForegroundServiceBinder == null) {
            Intent intent = new Intent(this, MyForegroundService.class);
            mMyForegroundServiceSC = new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName name, IBinder service) {
                    mMyForegroundServiceBinder = (MyForegroundService.MyLocalServiceBinder) service;
                }

                @Override
                public void onServiceDisconnected(ComponentName name) {
                }
            };
            startService(intent);
            bindService(intent, mMyForegroundServiceSC, Context.BIND_AUTO_CREATE);
        } else {
            mMyForegroundServiceBinder.getMyLocalService().stopForeground();
            unbindService(mMyForegroundServiceSC);
            mMyForegroundServiceBinder = null;
            mMyForegroundServiceSC = null;
        }
    }
}
