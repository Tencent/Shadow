package com.tencent.shadow.demo.usecases.service;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.view.View;

import com.tencent.shadow.demo.gallery.BaseActivity;
import com.tencent.shadow.demo.gallery.R;
import com.tencent.shadow.demo.gallery.cases.entity.UseCase;
import com.tencent.shadow.demo.gallery.util.ToastUtil;

public class TestStartServiceActivity extends BaseActivity {

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        serviceIntent = new Intent(this, TestService.class);
        setContentView(R.layout.layout_service);
    }

    public void start(View view) {
        startService(serviceIntent);
    }

    public void bind(View view) {
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
       stopService(serviceIntent);
    }

    public void unbind(View view) {
       unbindService(serviceConnection);
    }

    public void testBinder(View view) {
        if (binder == null) {
            ToastUtil.showToast(this, "请先bindService");
        } else {
            binder.getMyLocalService().test();
        }
    }
}
