package com.tencent.shadow.runtime;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Pair;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;

import com.tencent.shadow.runtime.container.HostActivityDelegator;
import com.tencent.shadow.runtime.remoteview.ShadowRemoteViewCreatorProvider;

import java.io.File;

public class ShadowContext extends ContextThemeWrapper {
    PluginComponentLauncher mPluginComponentLauncher;
    ClassLoader mPluginClassLoader;
    ShadowApplication mShadowApplication;
    Resources mPluginResources;
    Resources mMixResources;
    LayoutInflater mLayoutInflater;
    String mLibrarySearchPath;
    protected String mPartKey;
    private String mBusinessName;
    /**
     * 缓存{@link ShadowContext#getDataDir()}等
     * GuardedBy mLock
     */
    private File mDataDir, mFilesDir, mCacheDir;
    final private Object mLock = new Object();
    private ShadowRemoteViewCreatorProvider mRemoteViewCreatorProvider;

    public ShadowContext() {
    }

    public ShadowContext(Context base, int themeResId) {
        super(base, themeResId);
    }

    public final void setPluginResources(Resources resources) {
        mPluginResources = resources;
    }

    public final void setPluginClassLoader(ClassLoader classLoader) {
        mPluginClassLoader = classLoader;
    }

    public void setPluginComponentLauncher(PluginComponentLauncher pluginComponentLauncher) {
        mPluginComponentLauncher = pluginComponentLauncher;
    }

    public void setShadowApplication(ShadowApplication shadowApplication) {
        mShadowApplication = shadowApplication;
    }

    public void setLibrarySearchPath(String mLibrarySearchPath) {
        this.mLibrarySearchPath = mLibrarySearchPath;
    }

    public void setBusinessName(String businessName) {
        if (businessName == null) {
            businessName = "";
        }
        this.mBusinessName = businessName;
    }

    public void setPluginPartKey(String partKey) {
        this.mPartKey = partKey;
    }

    public final void setRemoteViewCreatorProvider(ShadowRemoteViewCreatorProvider provider) {
        mRemoteViewCreatorProvider = provider;
    }

    public final ShadowRemoteViewCreatorProvider getRemoteViewCreatorProvider() {
        return mRemoteViewCreatorProvider;
    }

    @Override
    public Context getApplicationContext() {
        return mShadowApplication;
    }

    @Override
    public Resources getResources() {
        if (mMixResources == null) {
            Context baseContext = getBaseContext();
            Resources hostResources;
            if (baseContext instanceof HostActivityDelegator) {
                hostResources = ((HostActivityDelegator) baseContext).superGetResources();
            } else {
                hostResources = baseContext.getResources();
            }
            mMixResources = new MixResources(hostResources, mPluginResources);
        }
        return mMixResources;
    }

    @Override
    public AssetManager getAssets() {
        return mPluginResources.getAssets();
    }

    @Override
    public Object getSystemService(String name) {
        if (LAYOUT_INFLATER_SERVICE.equals(name)) {
            if (mLayoutInflater == null) {
                LayoutInflater inflater = (LayoutInflater) super.getSystemService(name);
                mLayoutInflater = ShadowLayoutInflater.build(inflater, this, mPartKey);
            }
            return mLayoutInflater;
        }
        return super.getSystemService(name);
    }

    @Override
    public ClassLoader getClassLoader() {
        return mPluginClassLoader;
    }

    public interface PluginComponentLauncher {
        /**
         * 启动Activity
         *
         * @param shadowContext 启动context
         * @param intent        插件内传来的Intent.
         * @return <code>true</code>表示该Intent是为了启动插件内Activity的,已经被正确消费了.
         * <code>false</code>表示该Intent不是插件内的Activity.
         */
        boolean startActivity(ShadowContext shadowContext, Intent intent);

        /**
         * 启动Activity
         *
         * @param delegator 发起启动的activity的delegator
         * @param intent    插件内传来的Intent.
         * @param callingActivity   调用者
         * @return <code>true</code>表示该Intent是为了启动插件内Activity的,已经被正确消费了.
         * <code>false</code>表示该Intent不是插件内的Activity.
         */
        boolean startActivityForResult(HostActivityDelegator delegator, Intent intent, int requestCode, Bundle option,ComponentName callingActivity);

        Pair<Boolean, ComponentName> startService(ShadowContext context, Intent service);

        Pair<Boolean, Boolean> stopService(ShadowContext context, Intent name);

        Pair<Boolean, Boolean> bindService(ShadowContext context, Intent service, ServiceConnection conn, int flags);

        Pair<Boolean, ?> unbindService(ShadowContext context, ServiceConnection conn);

        Intent convertPluginActivityIntent(Intent pluginIntent);

    }

    @Override
    public void startActivity(Intent intent) {
        final Intent pluginIntent = new Intent(intent);
        pluginIntent.setExtrasClassLoader(mPluginClassLoader);
        final boolean success = mPluginComponentLauncher.startActivity(this, pluginIntent);
        if (!success) {
            super.startActivity(intent);
        }
    }

    public void superStartActivity(Intent intent) {
        super.startActivity(intent);
    }

    @Override
    public void unbindService(ServiceConnection conn) {
        if (!mPluginComponentLauncher.unbindService(this, conn).first)
            super.unbindService(conn);
    }

    @Override
    public boolean bindService(Intent service, ServiceConnection conn, int flags) {
        if (service.getComponent() == null) {
            return super.bindService(service, conn, flags);
        }
        Pair<Boolean, Boolean> ret = mPluginComponentLauncher.bindService(this, service, conn, flags);
        if (!ret.first)
            return super.bindService(service, conn, flags);
        return ret.second;
    }

    @Override
    public boolean stopService(Intent name) {
        if (name.getComponent() == null) {
            return super.stopService(name);
        }
        Pair<Boolean, Boolean> ret = mPluginComponentLauncher.stopService(this, name);
        if (!ret.first)
            return super.stopService(name);
        return ret.second;
    }

    @Override
    public ComponentName startService(Intent service) {
        if (service.getComponent() == null) {
            return super.startService(service);
        }
        Pair<Boolean, ComponentName> ret = mPluginComponentLauncher.startService(this, service);
        if (!ret.first)
            return super.startService(service);
        return ret.second;
    }

    @Override
    public ApplicationInfo getApplicationInfo() {
        final ApplicationInfo applicationInfo = super.getApplicationInfo();
        applicationInfo.nativeLibraryDir = mLibrarySearchPath;
        return applicationInfo;
    }

    public PluginComponentLauncher getPendingIntentConverter() {
        return mPluginComponentLauncher;
    }

    /**
     * 该方法只有API>=N时生效
     */
    @Override
    public File getDataDir() {
        if (TextUtils.isEmpty(mBusinessName)) {//如果mBusinessName为空，表示插件和宿主是同一业务，使用同一个Data目录
            return super.getDataDir();
        } else {
            synchronized (mLock) {
                if (mDataDir == null) {
                    mDataDir = new File(super.getDataDir(), "ShadowPluginDataDir/" + mBusinessName);
                    mDataDir.mkdirs();
                }
                return mDataDir;
            }
        }
    }

    @Override
    public File getFilesDir() {
        if (TextUtils.isEmpty(mBusinessName)) {
            return super.getFilesDir();
        } else {
            synchronized (mLock) {
                if (mFilesDir == null) {
                    mFilesDir = new File(super.getFilesDir(), "ShadowPluginFilesDir/" + mBusinessName);
                    mFilesDir.mkdirs();
                }
                return mFilesDir;
            }
        }
    }

    @Override
    public File getCacheDir() {
        if (TextUtils.isEmpty(mBusinessName)) {
            return super.getCacheDir();
        } else {
            synchronized (mLock) {
                if (mCacheDir == null) {
                    mCacheDir = new File(super.getCacheDir(), "ShadowPluginCacheDir/" + mBusinessName);
                    mCacheDir.mkdirs();
                }
                return mCacheDir;
            }
        }
    }

    @Override
    public File getDir(String name, int mode) {
        if (mode != MODE_PRIVATE || TextUtils.isEmpty(mBusinessName)) {
            return super.getDir(name, mode);
        } else {
            File file = new File(super.getDir(name, mode), "ShadowPluginDir/" + mBusinessName);
            file.mkdirs();
            return file;
        }
    }

    @Override
    public File getDatabasePath(String name) {
        if (TextUtils.isEmpty(mBusinessName)) {
            return super.getDatabasePath(name);
        } else {
            File databasePath = super.getDatabasePath("ShadowPluginDatabase_" + mBusinessName + "_" + name);
            databasePath.mkdirs();
            return databasePath;
        }
    }

    @Override
    public SharedPreferences getSharedPreferences(String name, int mode) {
        if (mode != MODE_PRIVATE || TextUtils.isEmpty(mBusinessName)) {
            return super.getSharedPreferences(name, mode);
        } else {
            return super.getSharedPreferences("ShadowPlugin_" + mBusinessName + "_" + name, mode);
        }
    }

}
