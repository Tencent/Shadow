package com.tencent.shadow.sdk.pluginmanager.context;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.view.LayoutInflater;

import static android.content.pm.PackageManager.GET_META_DATA;

/**
 * 当前PluginManager apk的context，可以用于查找当前apk的资源
 */
public class PluginManagerContext extends ContextWrapper {

    private Resources mResources;

    private LayoutInflater mLayoutInflater;

    public PluginManagerContext(Context base, String apkPath) {
        super(base);
        mResources = createResources(apkPath, base);
    }

    private Resources createResources(String apkPath, Context base) {
        PackageManager packageManager = base.getPackageManager();
        PackageInfo packageArchiveInfo = packageManager.getPackageArchiveInfo(apkPath, GET_META_DATA);
        packageArchiveInfo.applicationInfo.publicSourceDir = apkPath;
        try {
            return packageManager.getResourcesForApplication(packageArchiveInfo.applicationInfo);
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public AssetManager getAssets() {
        return mResources.getAssets();
    }

    @Override
    public Resources getResources() {
        return mResources;
    }

    @Override
    public Resources.Theme getTheme() {
        return mResources.newTheme();
    }

    @Override
    public Object getSystemService(String name) {
        if (Context.LAYOUT_INFLATER_SERVICE.equals(name)) {
            if (mLayoutInflater == null) {
                LayoutInflater layoutInflater = (LayoutInflater) super.getSystemService(name);
                mLayoutInflater = layoutInflater.cloneInContext(this);
            }
            return mLayoutInflater;
        }
        return super.getSystemService(name);
    }

    @Override
    public ClassLoader getClassLoader() {
        return PluginManagerContext.class.getClassLoader();
    }
}


