package com.tencent.hydevteam.pluginframework.plugincontainer;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.IBinder;

/**
 * HostService的被委托者接口
 * <p>
 * 被委托者通过实现这个接口中声明的方法达到替代委托者实现的目的，从而将HostService的行为动态化。
 *
 * @author cubershi
 */
public interface HostServiceDelegate {
    void setDelegator(HostServiceDelegator delegator);

    IBinder onBind(Intent intent);

    void onCreate(Intent intent);

    int onStartCommand(Intent intent, int flags, int startId);

    void onDestroy();

    void onConfigurationChanged(Configuration newConfig);

    void onLowMemory();

    void onTrimMemory(int level);

    boolean onUnbind(Intent intent, Boolean allUnBind);

    void onTaskRemoved(Intent rootIntent);
}
