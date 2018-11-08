package com.tencent.hydevteam.pluginframework.plugincontainer;

import android.app.Service;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Process;
import android.util.Log;

import com.tencent.shadow.runtime.BuildConfig;

import static com.tencent.hydevteam.pluginframework.plugincontainer.DelegateProvider.LOADER_VERSION_KEY;
import static com.tencent.hydevteam.pluginframework.plugincontainer.DelegateProvider.PROCESS_VERSION_KEY;

/**
 * 插件的容器Service。PluginLoader将把插件的Service放在其中。
 * PluginContainerService以委托模式将Service的所有回调方法委托给DelegateProviderHolder提供的Delegate。
 *
 * @author cubershi
 */
public class PluginContainerService extends Service implements HostService, HostServiceDelegator {

    private static final String TAG = "PluginContainerService";

    HostServiceDelegate hostServiceDelegate;

    public PluginContainerService() {
        HostServiceDelegate delegate;
        if (DelegateProviderHolder.delegateProvider != null) {
            delegate = DelegateProviderHolder.delegateProvider.getHostServiceDelegate(this.getClass());
            delegate.setDelegator(this);
        } else {
            Log.e(TAG, "PluginContainerService: DelegateProviderHolder没有初始化");
            delegate = null;
        }
        hostServiceDelegate = delegate;
    }

    @Override
    public void onCreate() {
        if (hostServiceDelegate != null) {
            hostServiceDelegate.onCreate();
        } else {
            stopSelf();
            Log.e(TAG, "hostServiceDelegate==null 杀进程");
            System.exit(0);
        }
    }

    private boolean isIllegalIntent(Intent intent) {
        if (intent == null) {
            //intent == null是Service被重启的情况，这种情况下我们也无法分辨Loader是否升级了
            return true;
        }
        Bundle extras = intent.getExtras();
        if (extras == null) {
            return true;
        }
        String loaderVersion = extras.getString(LOADER_VERSION_KEY);
        int processVersion = extras.getInt(PROCESS_VERSION_KEY);
        return !BuildConfig.VERSION_NAME.equals(loaderVersion) || processVersion != Process.myPid();
    }

    @Override
    public IBinder onBind(Intent intent) {
        if (isIllegalIntent(intent)) {
            Log.e(TAG, "illegalIntent intent.getExtras()==" + intent.getExtras());
            stopSelf();
            hostServiceDelegate = null;
            Log.e(TAG, "illegalIntent 杀进程");
            System.exit(0);
        }
        if (hostServiceDelegate != null) {
            return hostServiceDelegate.onBind(intent);
        } else {
            return null;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (isIllegalIntent(intent)) {
            Log.e(TAG, "illegalIntent intent.getExtras()==" + intent.getExtras());
            stopSelf();
            hostServiceDelegate = null;
            Log.e(TAG, "illegalIntent 杀进程");
            System.exit(0);
        }
        if (hostServiceDelegate != null) {
            return hostServiceDelegate.onStartCommand(intent, flags, startId);
        } else {
            return super.onStartCommand(intent, flags, startId);
        }
    }

    @Override
    public void onDestroy() {
        if (hostServiceDelegate != null) {
            hostServiceDelegate.onDestroy();
        } else {
            super.onDestroy();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (hostServiceDelegate != null) {
            hostServiceDelegate.onConfigurationChanged(newConfig);
        } else {
            super.onConfigurationChanged(newConfig);
        }
    }

    @Override
    public void onLowMemory() {
        if (hostServiceDelegate != null) {
            hostServiceDelegate.onLowMemory();
        } else {
            super.onLowMemory();
        }
    }

    @Override
    public void onTrimMemory(int level) {
        if (hostServiceDelegate != null) {
            hostServiceDelegate.onTrimMemory(level);
        } else {
            super.onTrimMemory(level);
        }
    }

    @Override
    public boolean onUnbind(Intent intent) {
        if (hostServiceDelegate != null) {
            return hostServiceDelegate.onUnbind(intent);
        } else {
            return super.onUnbind(intent);
        }
    }

    @Override
    final public void superOnCreate() {
        super.onCreate();
    }

    @Override
    public void superStopSelf() {
        super.stopSelf();
    }

    @Override
    public boolean superOnUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        if (hostServiceDelegate != null) {
            hostServiceDelegate.onTaskRemoved(rootIntent);
        } else {
            super.onTaskRemoved(rootIntent);
        }
    }
}
