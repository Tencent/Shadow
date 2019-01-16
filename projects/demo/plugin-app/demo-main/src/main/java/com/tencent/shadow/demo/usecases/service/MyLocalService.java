package com.tencent.shadow.demo.usecases.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

public class MyLocalService extends Service {
    private IBinder mBinder;

    @Override
    public void onCreate() {
        super.onCreate();
        mBinder = new MyLocalServiceBinder();
        Toast.makeText(this, "MyLocalService onCreate", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "MyLocalService onDestroy", Toast.LENGTH_SHORT).show();
        mBinder = null;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class MyLocalServiceBinder extends Binder {
        public MyLocalService getMyLocalService() {
            return MyLocalService.this;
        }
    }

    public void test() {
        Toast.makeText(this, "MyLocalService", Toast.LENGTH_SHORT).show();
    }
}
