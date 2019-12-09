package com.tencent.shadow.dynamic.manager;

import android.content.ComponentName;
import android.content.Context;
import android.os.DeadObjectException;
import android.os.IBinder;
import android.os.RemoteException;

import com.tencent.shadow.core.common.Logger;
import com.tencent.shadow.core.common.LoggerFactory;
import com.tencent.shadow.dynamic.host.FailedException;
import com.tencent.shadow.dynamic.host.PluginManagerImpl;
import com.tencent.shadow.dynamic.host.PpsStatus;
import com.tencent.shadow.dynamic.host.MultiLoaderPluginProcessService;
import com.tencent.shadow.dynamic.host.MultiLoaderPpsController;
import com.tencent.shadow.dynamic.loader.PluginLoader;


abstract public class PluginManagerThatSupportMultiLoader extends BaseDynamicPluginManager implements PluginManagerImpl {
    private static final Logger mLogger = LoggerFactory.getLogger(PluginManagerThatUseDynamicLoader.class);

    /**
     * 插件进程MultiLoaderPluginProcessService的接口
     */
    protected MultiLoaderPpsController mPpsController;

    /**
     * 插件加载服务端接口
     */
    protected PluginLoader mPluginLoader;

    public PluginManagerThatSupportMultiLoader(Context context) {
        super(context);
    }

    /**
     * 多Loader的PPS，需要hack多个RuntimeContainer，因此需要使用pluginKey来作为插件业务的身份标识
     * Note：一个插件包有一份loader、一份runtime、多个pluginPart，该key与插件包一一对应
     */
    public abstract String getPluginKey();

    @Override
    protected void onPluginServiceConnected(ComponentName name, IBinder service) {
        mPpsController = MultiLoaderPluginProcessService.wrapBinder(service);
        try {
            mPpsController.setUuidManagerForPlugin(getPluginKey(), new UuidManagerBinder(PluginManagerThatSupportMultiLoader.this));
        } catch (DeadObjectException e) {
            if (mLogger.isErrorEnabled()) {
                mLogger.error("onServiceConnected RemoteException:" + e);
            }
        } catch (RemoteException e) {
            if (e.getClass().getSimpleName().equals("TransactionTooLargeException")) {
                if (mLogger.isErrorEnabled()) {
                    mLogger.error("onServiceConnected TransactionTooLargeException:" + e);
                }
            } else {
                throw new RuntimeException(e);
            }
        }

        try {
            IBinder iBinder = mPpsController.getPluginLoaderForPlugin(getPluginKey());
            if (iBinder != null) {
                mPluginLoader = new BinderPluginLoader(iBinder);
            }
        } catch (RemoteException ignored) {
            if (mLogger.isErrorEnabled()) {
                mLogger.error("onServiceConnected mPpsController getPluginLoader:", ignored);
            }
        }
    }

    @Override
    protected void onPluginServiceDisconnected(ComponentName name) {
        mPpsController = null;
        mPluginLoader = null;
    }

    public final void loadRunTime(String uuid) throws RemoteException, FailedException {
        if (mLogger.isInfoEnabled()) {
            mLogger.info("loadRunTime mPpsController:" + mPpsController);
        }
        PpsStatus ppsStatus = mPpsController.getPpsStatusForPlugin(getPluginKey());
        if (!ppsStatus.runtimeLoaded) {
            mPpsController.loadRuntimeForPlugin(getPluginKey(), uuid);
        }
    }

    public final void loadPluginLoader(String uuid) throws RemoteException, FailedException {
        if (mLogger.isInfoEnabled()) {
            mLogger.info("loadPluginLoader mPluginLoader:" + mPluginLoader);
        }
        if (mPluginLoader == null) {
            PpsStatus ppsStatus = mPpsController.getPpsStatusForPlugin(getPluginKey());
            if (!ppsStatus.loaderLoaded) {
                mPpsController.loadPluginLoaderForPlugin(getPluginKey(), uuid);
            }
            IBinder iBinder = mPpsController.getPluginLoaderForPlugin(getPluginKey());
            mPluginLoader = new BinderPluginLoader(iBinder);
        }
    }
}
