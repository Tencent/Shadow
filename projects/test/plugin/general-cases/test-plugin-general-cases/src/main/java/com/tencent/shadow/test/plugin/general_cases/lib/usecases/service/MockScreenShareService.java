/*
 * Tencent is pleased to support the open source community by making Tencent Shadow available.
 * Copyright (C) 2019 THL A29 Limited, a Tencent company. All rights reserved.
 *
 * Licensed under the BSD 3-Clause License (the "License"); you may not use
 * this file except in compliance with the License.
 */
package com.tencent.shadow.test.plugin.general_cases.lib.usecases.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

public class MockScreenShareService extends Service {

    static final String ACTION_STATUS =
            "com.tencent.shadow.test.plugin.FOREGROUND_SERVICE_STATUS";
    static final String EXTRA_MESSAGE = "message";
    static final String EXTRA_RESULT_CODE = "media_projection_result_code";
    static final String EXTRA_RESULT_DATA = "media_projection_result_data";

    private static final String TAG = "MockScreenShareFgs";
    private static final String CHANNEL_ID = "shadow_mock_screen_share";
    private static final int NOTIFICATION_ID = 31001;

    private MediaProjection mMediaProjection;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand startId=" + startId);
        createNotificationChannel();
        Notification notification = createNotification();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(NOTIFICATION_ID, notification,
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PROJECTION);
        } else {
            startForeground(NOTIFICATION_ID, notification);
        }
        sendStatus("屏幕共享Service已调用startForeground，startId=" + startId);

        if (intent != null && mMediaProjection == null) {
            int resultCode = intent.getIntExtra(EXTRA_RESULT_CODE, ActivityResultCodes.CANCELED);
            Intent resultData = intent.getParcelableExtra(EXTRA_RESULT_DATA);
            MediaProjectionManager manager = (MediaProjectionManager)
                    getSystemService(MEDIA_PROJECTION_SERVICE);
            if (resultCode == ActivityResultCodes.OK && resultData != null && manager != null) {
                mMediaProjection = manager.getMediaProjection(resultCode, resultData);
                mMediaProjection.registerCallback(new MediaProjection.Callback() {
                    @Override
                    public void onStop() {
                        Log.i(TAG, "MediaProjection stopped by system");
                        stopSelf();
                    }
                }, null);
                sendStatus("MediaProjection已创建，模拟第三方SDK持有录屏会话");
            } else {
                sendStatus("ERROR: 缺少有效MediaProjection授权数据");
                stopSelf();
            }
        }
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy");
        if (mMediaProjection != null) {
            mMediaProjection.stop();
            mMediaProjection = null;
        }
        stopForeground(true);
        sendStatus("屏幕共享Service已销毁");
        super.onDestroy();
    }

    private Notification createNotification() {
        Notification.Builder builder = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
                ? new Notification.Builder(this, CHANNEL_ID)
                : new Notification.Builder(this);
        return builder.setContentTitle("模拟第三方SDK正在共享屏幕")
                .setContentText("Shadow插件前台Service回归测试")
                .setSmallIcon(android.R.drawable.ic_menu_camera)
                .setOngoing(true)
                .build();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager manager = (NotificationManager)
                    getSystemService(NOTIFICATION_SERVICE);
            if (manager != null) {
                manager.createNotificationChannel(new NotificationChannel(
                        CHANNEL_ID, "模拟屏幕共享", NotificationManager.IMPORTANCE_LOW));
            }
        }
    }

    private void sendStatus(String message) {
        sendBroadcast(new Intent(ACTION_STATUS).putExtra(EXTRA_MESSAGE, message));
    }

    private static final class ActivityResultCodes {
        static final int OK = -1;
        static final int CANCELED = 0;
    }
}
