package com.tencent.shadow.test.plugin.general_cases.lib.usecases.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

import com.tencent.shadow.test.plugin.general_cases.lib.gallery.util.ToastUtil;

public class TestService extends Service {
    private IBinder mBinder;

    @Override
    public void onCreate() {
        super.onCreate();
        mBinder = new MyLocalServiceBinder();
        ToastUtil.showToast(this, "TestService onCreate");
        sendMsg("onCreate");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sendMsg("onDestroy");
        ToastUtil.showToast(this, "TestService onDestroy");
        mBinder = null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        ToastUtil.showToast(this, "TestService onStartCommand");
        sendMsg("onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        ToastUtil.showToast(this, "TestService onBind");
        sendMsg("onBind");
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        ToastUtil.showToast(this, "TestService unbindService");
        sendMsg("onUnbind");
        return super.onUnbind(intent);
    }


    public class MyLocalServiceBinder extends Binder {
        public TestService getMyLocalService() {
            return TestService.this;
        }
    }

    public void test() {
        sendMsg("callTest");
        ToastUtil.showToast(this, "TestService");
    }


    private void sendMsg(String msg){
        Intent intent = new Intent(TestStartServiceActivity.INTENT_ACTION);
        intent.putExtra("result",msg);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}
