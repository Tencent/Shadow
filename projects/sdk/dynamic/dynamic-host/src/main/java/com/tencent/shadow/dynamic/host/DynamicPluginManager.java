package com.tencent.shadow.dynamic.host;

import android.content.Context;
import android.os.Bundle;

import com.tencent.shadow.core.interface_.EnterCallback;
import com.tencent.shadow.core.interface_.PluginManager;

import java.io.File;

public final class DynamicPluginManager implements PluginManager {

    final private PluginManagerUpdater mUpdater;
    private PluginManager mManagerImpl;
    private long mLastModified;

    public DynamicPluginManager(PluginManagerUpdater updater) {
        if (updater.getLatest() == null) {
            throw new IllegalArgumentException("构造DynamicPluginManager时传入的PluginManagerUpdater" +
                    "必须已经已有本地文件，即getLatest()!=null");
        }
        mUpdater = updater;
    }

    @Override
    public void enter(Context context, long fromId, Bundle bundle, EnterCallback callback) {
        updateManagerImpl(context);
        mManagerImpl.enter(context, fromId, bundle, callback);
        mUpdater.update();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

    }

    @Override
    public void onDestroy() {
        if (mManagerImpl != null) {
            mManagerImpl.onDestroy();
            mManagerImpl = null;
        }
    }

    private void updateManagerImpl(Context context) {
        File latestManagerImplApk = mUpdater.getLatest();
        long lastModified = latestManagerImplApk.lastModified();
        if (mLastModified != lastModified) {
            ManagerImplLoader implLoader = new ManagerImplLoader(context, latestManagerImplApk);
            PluginManager newImpl = implLoader.load();
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
}
