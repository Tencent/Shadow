package com.tencent.cubershi.mock_interface;

import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.IBinder;

import com.tencent.hydevteam.pluginframework.plugincontainer.HostServiceDelegator;

/**
 * Created by tracyluo on 2018/6/5.
 */
public abstract class MockService {
    HostServiceDelegator mHostServiceDelegator;
    Resources mPluginResources;
    Context mHostAppContext;

    ClassLoader mPluginClassLoader;
    MockApplication mPluginApplication;

    public final void setPluginResources(Resources resources) {
        mPluginResources = resources;
    }

    public final void setHostContextAsBase(Context context) {
        mHostAppContext = context;
    }

    public final void setPluginClassLoader(ClassLoader classLoader) {
        mPluginClassLoader = classLoader;
    }

    public void setContainerService(HostServiceDelegator delegator) {
        mHostServiceDelegator = delegator;
    }
    public void setPluginApplication(MockApplication pluginApplication) {
        mPluginApplication = pluginApplication;
    }

    public IBinder onBind(Intent intent) {
        return null;
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        return -1;
    }

    public void onDestroy() {

    }

    public void onConfigurationChanged(Configuration newConfig) {

    }

    public void onLowMemory() {

    }

    public void onTrimMemory(int level) {

    }

    public boolean onUnbind(Intent intent){
        return false;
    }

    public void onTaskRemoved(Intent rootIntent){

    }

    public interface PluginServiceManager {

        boolean startService(Intent intent);
        boolean stopService(Intent name);
        boolean bindService(Intent service, ServiceConnection conn, int flags);
        boolean unbindService(ServiceConnection conn);

    }
}
