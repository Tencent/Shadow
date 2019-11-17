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

package com.tencent.shadow.core.runtime;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.IBinder;

import java.io.FileDescriptor;
import java.io.PrintWriter;

/**
 * Created by tracyluo on 2018/6/5.
 */
public abstract class ShadowService extends ShadowContext {


    public final void setHostContextAsBase(Context context) {
        attachBaseContext(context);
    }


    public IBinder onBind(Intent intent) {
        return null;
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_NOT_STICKY;
    }

    public void onDestroy() {

    }

    public void onConfigurationChanged(Configuration newConfig) {

    }

    public void onLowMemory() {

    }

    public void onTrimMemory(int level) {

    }

    public boolean onUnbind(Intent intent) {
        return false;
    }

    public void onTaskRemoved(Intent rootIntent) {

    }

    public void onCreate() {

    }

    public void onRebind(Intent intent) {

    }

    @Deprecated
    public void onStart(Intent intent, int startId) {
    }

    @Deprecated
    public final void setForeground(boolean isForeground) {
        //todo #37 支持Service设置Foreground
    }

    public final void startForeground(int id, Notification notification) {
        //mHostServiceDelegator.startForeground(id, notification);
        //todo #37 支持Service设置Foreground
    }

    public final void stopForeground(boolean removeNotification) {
        //todo #37 支持Service设置Foreground
        //mHostServiceDelegator.stopForeground(removeNotification);
    }

    public final void stopForeground(int flags) {

    }

    public final void stopSelf() {
        stopService(new Intent(this, getClass()));
    }

    /**
     * 插件环境下Service不支持调用带参数的stopSelf
     */
    public final void stopSelf(int startId) {
        stopSelf();
    }

    /**
     * 插件环境下Service不支持调用带参数的stopSelf
     */
    public final boolean stopSelfResult(int startId) {
        stopSelf();
        return true;
    }

    public final ShadowApplication getApplication() {
        return mShadowApplication;
    }

    protected void dump(FileDescriptor fd, PrintWriter writer, String[] args) {
        writer.println("nothing to dump");
    }
}
