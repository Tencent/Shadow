package com.tencent.shadow.runtime;

import android.content.pm.PackageManager;
import android.content.res.Resources;

public class PluginPartInfo {

    public ShadowApplication application;

    public Resources resources;

    public ClassLoader classLoader;

    public PackageManager packageManager;


    public PluginPartInfo(ShadowApplication application, Resources resources, ClassLoader classLoader, PackageManager packageManager) {
        this.application = application;
        this.resources = resources;
        this.classLoader = classLoader;
        this.packageManager = packageManager;
    }
}
