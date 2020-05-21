package com.tencent.shadow.dynamic.manager;

import android.content.ComponentName;
import android.content.Context;
import android.os.IBinder;
import android.os.RemoteException;

import com.tencent.shadow.core.common.Logger;
import com.tencent.shadow.core.common.LoggerFactory;
import com.tencent.shadow.dynamic.host.FailedException;
import com.tencent.shadow.dynamic.host.MultiLoaderPluginProcessService;
import com.tencent.shadow.dynamic.host.MultiLoaderPpsController;
import com.tencent.shadow.dynamic.host.PluginManagerImpl;
import com.tencent.shadow.dynamic.host.PpsStatus;
import com.tencent.shadow.dynamic.host.SingleLoaderPpsController;
import com.tencent.shadow.dynamic.loader.PluginLoader;

import java.util.HashMap;

abstract public class PluginManagerThatSupportMultiLoader extends BaseDynamicPluginManager implements PluginManagerImpl {
    private static final Logger mLogger = LoggerFactory.getLogger(PluginManagerThatUseDynamicLoader.class);

    /**
     * 插件进程MultiLoaderPluginProcessService的接口
     */
    protected MultiLoaderPpsController mPpsController;

    private HashMap<String, SingleLoaderPpsController> mPpsHashMap = new HashMap<>();
    private HashMap<String, PluginLoader> mPluginLoaderHashMap = new HashMap<>();

    public PluginManagerThatSupportMultiLoader(Context context) {
        super(context);
    }

    @Override
    protected void onPluginServiceConnected(ComponentName name, IBinder service) {
        mPpsController = MultiLoaderPluginProcessService.wrapBinder(service);
    }

    @Override
    protected void onPluginServiceDisconnected(ComponentName name) {
        mPpsController = null;
        mPpsHashMap.clear();
    }

    private final SingleLoaderPpsController getSingleLoaderPps(String uuid) throws RemoteException {
        SingleLoaderPpsController ppsController = mPpsHashMap.get(uuid);
        if (ppsController == null) {
            IBinder pps = mPpsController.getSingleLoaderPps(uuid);
            ppsController = new SingleLoaderPpsController(pps);
            PpsStatus ppsStatus = ppsController.getPpsStatus();
            if (!ppsStatus.uuidManagerSet) {
                ppsController.setUuidManager(new UuidManagerBinder(PluginManagerThatSupportMultiLoader.this));
            }
            mPpsHashMap.put(uuid, ppsController);
        }
        return ppsController;
    }

    public final void loadRunTime(String uuid) throws RemoteException, FailedException {
        if (mLogger.isInfoEnabled()) {
            mLogger.info("loadRunTime mPpsController={} for uuid={}", mPpsController, uuid);
        }

        SingleLoaderPpsController ppsController = getSingleLoaderPps(uuid);
        PpsStatus ppsStatus = ppsController.getPpsStatus();
        if (!ppsStatus.runtimeLoaded) {
            ppsController.loadRuntime(uuid);
        }
    }

    public final PluginLoader fetchPluginLoader(String uuid) throws RemoteException, FailedException {
        PluginLoader pluginLoader = mPluginLoaderHashMap.get(uuid);
        if (pluginLoader == null) {
            SingleLoaderPpsController ppsController = getSingleLoaderPps(uuid);
            PpsStatus ppsStatus = ppsController.getPpsStatus();
            if (!ppsStatus.loaderLoaded) {
                ppsController.loadPluginLoader(uuid);
            }
            pluginLoader = new BinderPluginLoader(ppsController.getPluginLoader());
            mPluginLoaderHashMap.put(uuid, pluginLoader);
        }
        return pluginLoader;
    }

    public final void loadPluginLoader(String uuid) throws RemoteException, FailedException {
        if (mLogger.isInfoEnabled()) {
            mLogger.info("loadPluginLoader mPpsController={} for uuid={}", mPpsController, uuid);
        }

        PluginLoader pluginLoader = mPluginLoaderHashMap.get(uuid);
        if (pluginLoader == null) {
            SingleLoaderPpsController ppsController = getSingleLoaderPps(uuid);
            PpsStatus ppsStatus = ppsController.getPpsStatus();
            if (!ppsStatus.loaderLoaded) {
                ppsController.loadPluginLoader(uuid);
            }
            pluginLoader = new BinderPluginLoader(ppsController.getPluginLoader());
            mPluginLoaderHashMap.put(uuid, pluginLoader);
        }
    }
}
