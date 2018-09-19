package com.tencent.shadow.runtime;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.IBinder;

import com.tencent.hydevteam.pluginframework.plugincontainer.HostServiceDelegator;

/**
 * Created by tracyluo on 2018/6/5.
 */
public abstract class ShadowService extends ShadowContext {
    HostServiceDelegator mHostServiceDelegator;

    public final void setHostContextAsBase(Context context) {
        attachBaseContext(context);
    }

    public void setHostServiceDelegator(HostServiceDelegator delegator) {
        mHostServiceDelegator = delegator;
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

    }

    public final void startForeground(int id, Notification notification) {
        mHostServiceDelegator.startForeground(id, notification);
    }

    public final void stopForeground(boolean removeNotification) {
        //todo cubershi: 这里没有考虑多Service复用的情况。多Service复用时不能其中一个Service要stop就stop。
        mHostServiceDelegator.stopForeground(removeNotification);
    }

    public final ShadowApplication getApplication() {
        return mShadowApplication;
    }
}
