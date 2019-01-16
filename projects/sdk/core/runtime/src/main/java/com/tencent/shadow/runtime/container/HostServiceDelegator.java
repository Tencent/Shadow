package com.tencent.shadow.runtime.container;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;

/**
 * HostService作为委托者的接口。主要提供它的委托方法的super方法，
 * 以便Delegate可以通过这个接口调用到Service的super方法。
 *
 * @author cubershi
 */
public interface HostServiceDelegator {
    void superOnCreate();

    void superStopSelf();

    boolean superOnUnbind(Intent intent);

    Context getApplicationContext();

    Context getBaseContext();

    void startForeground(int id, Notification notification);

    void stopForeground(boolean removeNotification);
}
