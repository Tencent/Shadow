package com.tencent.shadow.dynamic.host;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.view.LayoutInflater;

import static android.content.pm.PackageManager.GET_META_DATA;

/**
 * 修改Context的apk路径的Wrapper。可将原Context的Resource和ClassLoader重新修改为新的Apk。
 */
class ChangeApkContextWrapper extends ContextWrapper {

    private Resources mResources;

    private LayoutInflater mLayoutInflater;

    final private ClassLoader mClassloader;

    ChangeApkContextWrapper(Context base, String apkPath, ClassLoader mClassloader) {
        super(base);
        this.mClassloader = mClassloader;
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
        return mClassloader;
    }
}


