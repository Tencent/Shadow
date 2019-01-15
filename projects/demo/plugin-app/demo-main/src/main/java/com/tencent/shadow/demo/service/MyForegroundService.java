package com.tencent.shadow.demo.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.tencent.shadow.demo.basicglsurfaceview.BasicGLSurfaceViewActivity;
import com.tencent.shadow.demo.main.R;

public class MyForegroundService extends Service {
    private final IBinder mBinder = new MyLocalServiceBinder();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "MyForegroundService onStartCommand", Toast.LENGTH_SHORT).show();


        Notification notification = new Notification(R.drawable.icon, "MyForegroundService",
                System.currentTimeMillis());
        Intent notificationIntent = new Intent(this, BasicGLSurfaceViewActivity.class);
        notification.contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        startForeground(888, notification);
        return super.onStartCommand(intent, flags, startId);
    }

    public void stopForeground() {
        stopForeground(true);
    }

    public class MyLocalServiceBinder extends Binder {
        public MyForegroundService getMyLocalService() {
            return MyForegroundService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
}
