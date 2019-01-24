package com.tencent.shadow.demo.usecases.service;

import android.app.Service;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.tencent.shadow.demo.gallery.util.ToastUtil;

public class TestService extends Service {
    private IBinder mBinder;

    @Override
    public void onCreate() {
        super.onCreate();
        mBinder = new MyLocalServiceBinder();
        ToastUtil.showToast(this, "TestService onCreate");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ToastUtil.showToast(this, "TestService onDestroy");
        mBinder = null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        ToastUtil.showToast(this, "TestService onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        ToastUtil.showToast(this, "TestService onBind");
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        ToastUtil.showToast(this, "TestService unbindService");
        return super.onUnbind(intent);
    }


    public class MyLocalServiceBinder extends Binder {
        public TestService getMyLocalService() {
            return TestService.this;
        }
    }

    public void test() {
        ToastUtil.showToast(this, "TestService");
    }
}
