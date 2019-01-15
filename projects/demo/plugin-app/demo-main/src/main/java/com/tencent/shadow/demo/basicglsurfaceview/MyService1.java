package com.tencent.shadow.demo.basicglsurfaceview;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

public class MyService1 extends Service {
    private String mInitString;
    private final IBinder mBinder = new LocalBinder();
    public MyService1() {
    }
    public class LocalBinder extends Binder {
        MyService1 getService() {
            // Return this instance of LocalService so clients can call public methods
            return MyService1.this;
        }
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mInitString = "my service init!";
        return super.onStartCommand(intent, flags, startId);
    }
}
