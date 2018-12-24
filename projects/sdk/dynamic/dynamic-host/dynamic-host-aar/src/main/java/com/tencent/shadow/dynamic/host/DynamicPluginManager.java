package com.tencent.shadow.dynamic.host;

import android.content.Context;
import android.os.Bundle;

import com.tencent.shadow.core.common.Logger;
import com.tencent.shadow.core.common.LoggerFactory;

import java.io.File;

public final class DynamicPluginManager implements PluginManager {

    final private PluginManagerUpdater mUpdater;
    private PluginManagerImpl mManagerImpl;
    private long mLastModified;
    private static final Logger mLogger = LoggerFactory.getLogger(DynamicPluginManager.class);

    public DynamicPluginManager(PluginManagerUpdater updater) {
        if (updater.getLatest() == null) {
            throw new IllegalArgumentException("构造DynamicPluginManager时传入的PluginManagerUpdater" +
                    "必须已经已有本地文件，即getLatest()!=null");
        }
        mUpdater = updater;
    }

    @Override
    public void enter(Context context, long fromId, Bundle bundle, EnterCallback callback) {
        if (mLogger.isInfoEnabled()) {
            mLogger.info("enter fromId:" + fromId + " callback:" + callback);
        }
        mUpdater.setUpdatingState(true);
        updateManagerImpl(context);
        mManagerImpl.enter(context, fromId, bundle, callback);
        mUpdater.update();
    }

    public void release() {
        if (mLogger.isInfoEnabled()) {
            mLogger.info("release");
        }
        if (mManagerImpl != null) {
            mManagerImpl.onDestroy();
            mManagerImpl = null;
        }
    }

    private void updateManagerImpl(Context context) {
        File latestManagerImplApk = mUpdater.getLatest();
        long lastModified = latestManagerImplApk.lastModified();
        if (mLogger.isInfoEnabled()) {
            mLogger.info("mLastModified != lastModified : " + (mLastModified != lastModified));
        }
        if (mLastModified != lastModified) {
            ManagerImplLoader implLoader = new ManagerImplLoader(context, latestManagerImplApk);
            PluginManagerImpl newImpl = implLoader.load();
            Bundle state;
            if (mManagerImpl != null) {
                state = new Bundle();
                mManagerImpl.onSaveInstanceState(state);
                mManagerImpl.onDestroy();
            } else {
                state = null;
            }
            newImpl.onCreate(state);
            mManagerImpl = newImpl;
            mLastModified = lastModified;
        }
    }

    public PluginManager getManagerImpl() {
        return mManagerImpl;
    }
}
