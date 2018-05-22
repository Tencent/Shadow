package com.tencent.hydevteam.pluginframework.plugincontainer;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.IBinder;
import android.util.Log;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * 插件的容器Service。PluginLoader将把插件的Service放在其中。
 * PluginContainerService以委托模式将Service的所有回调方法委托给DelegateProviderHolder提供的Delegate。
 *
 * @author cubershi
 */
public class PluginContainerService extends Service implements HostService, HostServiceDelegator {
    private static final String TAG = "PluginContainerService";

    private ServiceDelegateManager delegateManager = new ServiceDelegateManager();

    public PluginContainerService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        if (intent == null) {
            return null;
        }
        HostServiceDelegate hostServiceDelegate = delegateManager.getDelegate(intent);
        if (hostServiceDelegate != null) {
            return hostServiceDelegate.onBind(intent);
        } else {
            return null;
        }
    }

    /**
     * http://tapd.oa.com/androidQQ/bugtrace/bugs/view?bug_id=1010066461057998459
     * 2.17.6.6 目前默认认为：所有的service的onStartCommand都只返回{@link Service#START_STICKY}
     * 在这种默认情况下
     * 1、强制返回 {@link Service#START_REDELIVER_INTENT}
     * 2、对于 {@link Service#START_FLAG_REDELIVERY}的调用，强制用null作为intent参数传入实际调用
     * <p>
     * 2017/11/28   add by owenguo
     * 多插件的时候，由于群视频插件和交友插件的包名一致，先启动群视频插件后，需要杀进程再启动花样交友插件，这样
     * service如果这个时候被重启了，在SixGodServiceDelegate创建service的逻辑里面会重新加载群视频插件，导致交友插件无法被加载
     * 且启动交友插件变成了启动群视频插件了。这里返回值改成START_NOT_STICKY，让service不要在杀死后自动重启
     */
    @SuppressWarnings("WrongConstant")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            return Service.START_NOT_STICKY;
        }
        HostServiceDelegate hostServiceDelegate = delegateManager.getDelegate(intent);
        if (hostServiceDelegate != null) {
            hostServiceDelegate.onStartCommand(intent, flags, startId);
        } else {
            super.onStartCommand(intent, flags, startId);
        }
        return Service.START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        Collection<HostServiceDelegate> delegates = delegateManager.getAllDelegates();
        for (HostServiceDelegate hostServiceDelegate : delegates) {
            if (hostServiceDelegate != null) {
                hostServiceDelegate.onDestroy();
            }
        }
        delegateManager.onDestroy();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        Collection<HostServiceDelegate> delegates = delegateManager.getAllDelegates();
        for (HostServiceDelegate hostServiceDelegate : delegates) {
            if (hostServiceDelegate != null) {
                hostServiceDelegate.onConfigurationChanged(newConfig);
            }
        }
    }

    @Override
    public void onLowMemory() {
        Collection<HostServiceDelegate> delegates = delegateManager.getAllDelegates();
        for (HostServiceDelegate hostServiceDelegate : delegates) {
            if (hostServiceDelegate != null) {
                hostServiceDelegate.onLowMemory();
            }
        }
    }

    @Override
    public void onTrimMemory(int level) {
        Collection<HostServiceDelegate> delegates = delegateManager.getAllDelegates();
        for (HostServiceDelegate hostServiceDelegate : delegates) {
            if (hostServiceDelegate != null) {
                hostServiceDelegate.onTrimMemory(level);
            }
        }
    }

    @Override
    public boolean onUnbind(Intent intent) {
        HostServiceDelegate hostServiceDelegate = delegateManager.removeDelegate(intent);
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

    class ServiceDelegateManager {
        private Map<ComponentName, HostServiceDelegate> serviceDelegates = new HashMap<>();
        // hardcode了sixgod中的intent参数
        private static final String KEY_PKG_NAME = "packageName";
        private static final String KEY_CLASS_NAME = "className";

        HostServiceDelegate getDelegate(Intent intent) {
            String pkg = intent.getStringExtra(KEY_PKG_NAME);
            String cls = intent.getStringExtra(KEY_CLASS_NAME);
            return getDelegate(pkg, cls);
        }

        HostServiceDelegate removeDelegate(Intent intent) {
            String pkg = intent.getStringExtra(KEY_PKG_NAME);
            String cls = intent.getStringExtra(KEY_CLASS_NAME);
            ComponentName componentName = new ComponentName(pkg, cls);
            return serviceDelegates.remove(componentName);
        }

        Collection<HostServiceDelegate> getAllDelegates() {
            return serviceDelegates.values();
        }

        HostServiceDelegate getDelegate(String pkg, String cls) {
            ComponentName componentName = new ComponentName(pkg, cls);
            HostServiceDelegate delegate = serviceDelegates.get(componentName);
            if (delegate == null) {
                if (DelegateProviderHolder.delegateProvider != null) {
                    delegate = DelegateProviderHolder.delegateProvider.getHostServiceDelegate(PluginContainerService.this.getClass());
                    delegate.setDelegator(PluginContainerService.this);
                    serviceDelegates.put(componentName, delegate);
                } else {
                    Log.e(TAG, "PluginContainerService: DelegateProviderHolder没有初始化");
                    delegate = null;
                }
            }

            return delegate;
        }

        void onDestroy() {
            serviceDelegates.clear();
        }
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.i(TAG, "PluginContainerService: onTaskRemoved");
        Collection<HostServiceDelegate> delegates = delegateManager.getAllDelegates();
        for (HostServiceDelegate hostServiceDelegate : delegates) {
            if (hostServiceDelegate != null) {
                hostServiceDelegate.onTaskRemoved(rootIntent);
            }
        }
    }
}
