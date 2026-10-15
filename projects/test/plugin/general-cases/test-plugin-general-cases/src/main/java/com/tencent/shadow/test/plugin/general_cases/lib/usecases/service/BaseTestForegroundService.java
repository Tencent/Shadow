/*
 * Tencent is pleased to support the open source community by making Tencent Shadow available.
 * Copyright (C) 2019 THL A29 Limited, a Tencent company.  All rights reserved.
 *
 * Licensed under the BSD 3-Clause License (the "License"); you may not use
 * this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 *     https://opensource.org/licenses/BSD-3-Clause
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.tencent.shadow.test.plugin.general_cases.lib.usecases.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;

/**
 * 自动化测试用的插件前台Service基类。生命周期事件通过本地广播通知
 * {@link TestForegroundServiceActivity}。
 */
public abstract class BaseTestForegroundService extends Service {

    public static final String ACTION_EVENT =
            "com.tencent.shadow.test.foreground_service.event";
    public static final String EXTRA_EVENT = "event";

    private static final String CHANNEL_ID = "shadow_test_foreground_service";

    protected abstract String getName();

    protected abstract int getNotificationId();

    @Override
    public void onCreate() {
        super.onCreate();
        sendEvent("onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForeground(getNotificationId(), buildNotification());
        sendEvent("onStartCommand");
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        sendEvent("onDestroy");
        super.onDestroy();
    }

    private Notification buildNotification() {
        Notification.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager manager =
                    (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.createNotificationChannel(new NotificationChannel(
                    CHANNEL_ID, "Shadow test", NotificationManager.IMPORTANCE_LOW));
            builder = new Notification.Builder(this, CHANNEL_ID);
        } else {
            builder = new Notification.Builder(this);
        }
        // 前台通知由宿主容器Service发出，只能使用系统资源而不能使用插件资源ID。
        return builder.setSmallIcon(android.R.drawable.stat_notify_sync)
                .setContentTitle("Shadow test " + getName())
                .build();
    }

    private void sendEvent(String event) {
        LocalBroadcastManager.getInstance(this).sendBroadcast(
                new Intent(ACTION_EVENT).putExtra(EXTRA_EVENT, getName() + "." + event));
    }
}
