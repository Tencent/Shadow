package com.tencent.shadow.runtime;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ApplicationInfo;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.util.Pair;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;

import com.tencent.hydevteam.pluginframework.plugincontainer.HostActivityDelegator;

public class ShadowContext extends ContextThemeWrapper {
    PluginActivityLauncher mPluginActivityLauncher;
    PluginServiceOperator mPluginServiceOperator;
    PendingIntentConverter mPendingIntentConverter;
    ClassLoader mPluginClassLoader;
    ShadowApplication mShadowApplication;
    Resources mPluginResources;
    LayoutInflater mLayoutInflater;
    String mLibrarySearchPath;

    public final void setPluginResources(Resources resources) {
        mPluginResources = resources;
    }

    public final void setPluginClassLoader(ClassLoader classLoader) {
        mPluginClassLoader = classLoader;
    }

    public final void setServiceOperator(PluginServiceOperator pluginServiceOperator) {
        mPluginServiceOperator = pluginServiceOperator;
    }


    public void setPluginActivityLauncher(PluginActivityLauncher pluginActivityLauncher) {
        mPluginActivityLauncher = pluginActivityLauncher;
    }

    public void setShadowApplication(ShadowApplication shadowApplication) {
        mShadowApplication = shadowApplication;
    }

    public void setLibrarySearchPath(String mLibrarySearchPath) {
        this.mLibrarySearchPath = mLibrarySearchPath;
    }

    public final void setPendingIntentConverter(PendingIntentConverter pendingIntentConverter) {
        this.mPendingIntentConverter = pendingIntentConverter;
    }

    @Override
    public Context getApplicationContext() {
        return mShadowApplication;
    }

    @Override
    public Resources getResources() {
        return mPluginResources;
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
                assert inflater != null;
                mLayoutInflater = new ShadowLayoutInflater(inflater.cloneInContext(this),this);
            }
            return mLayoutInflater;
        }
        return super.getSystemService(name);
    }

    @Override
    public ClassLoader getClassLoader() {
        return mPluginClassLoader;
    }

    public interface PluginActivityLauncher {
        /**
         * 启动Actvity
         *
         * @param context 启动context
         * @param intent  插件内传来的Intent.
         * @return <code>true</code>表示该Intent是为了启动插件内Activity的,已经被正确消费了.
         * <code>false</code>表示该Intent不是插件内的Activity.
         */
        boolean startActivity(Context context, Intent intent);

        /**
         * 启动Actvity
         *
         * @param delegator 发起启动的activity的delegator
         * @param intent    插件内传来的Intent.
         * @return <code>true</code>表示该Intent是为了启动插件内Activity的,已经被正确消费了.
         * <code>false</code>表示该Intent不是插件内的Activity.
         */
        boolean startActivityForResult(HostActivityDelegator delegator, Intent intent, int requestCode);

    }

    public interface PluginServiceOperator {

        Pair<Boolean, ComponentName> startService(ShadowContext context, Intent intent);

        Pair<Boolean, Boolean> stopService(ShadowContext context, Intent name);

        Pair<Boolean, Boolean> bindService(ShadowContext context, Intent service, ServiceConnection conn, int flags);

        Pair<Boolean, ?> unbindService(ShadowContext context, ServiceConnection conn);

    }

    public interface PendingIntentConverter{
        Pair<Context,Intent> convertPluginActivityIntent(Intent pluginIntent);

        Pair<Context,Intent> convertPluginServiceIntent(Intent pluginIntent);
    }

    @Override
    public void startActivity(Intent intent) {
        final Intent pluginIntent = new Intent(intent);
        pluginIntent.setExtrasClassLoader(mPluginClassLoader);
        final boolean success = mPluginActivityLauncher.startActivity(this, pluginIntent);
        if (!success) {
            super.startActivity(intent);
        }
    }

    @Override
    public void unbindService(ServiceConnection conn) {
        if (!mPluginServiceOperator.unbindService(this, conn).first)
            super.unbindService(conn);
    }

    @Override
    public boolean bindService(Intent service, ServiceConnection conn, int flags) {
        if (service.getComponent() == null) {
            return super.bindService(service, conn, flags);
        }
        Pair<Boolean, Boolean> ret = mPluginServiceOperator.bindService(this, service, conn, flags);
        if (!ret.first)
            return super.bindService(service, conn, flags);
        return ret.second;
    }

    @Override
    public boolean stopService(Intent name) {
        if (name.getComponent() == null) {
            return super.stopService(name);
        }
        Pair<Boolean, Boolean> ret = mPluginServiceOperator.stopService(this, name);
        if (!ret.first)
            return super.stopService(name);
        return ret.second;
    }

    @Override
    public ComponentName startService(Intent service) {
        if (service.getComponent() == null) {
            return super.startService(service);
        }
        Pair<Boolean, ComponentName> ret = mPluginServiceOperator.startService(this, service);
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

    public PendingIntentConverter getPendingIntentConverter() {
        return mPendingIntentConverter;
    }
}
