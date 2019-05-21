package com.tencent.shadow.dynamic.host;

import android.content.Context;

import com.tencent.shadow.core.common.InstalledApk;

final class LoaderImplLoader extends ImplLoader {
    /**
     * 加载{@link #sLoaderFactoryImplClassName}时
     * 需要从宿主PathClassLoader（含双亲委派）中加载的类
     */
    private static final String[] sInterfaces = new String[]{
            //当runtime是动态加载的时候，runtime的ClassLoader是PathClassLoader的parent，
            // 所以不需要写在这个白名单里。但是写在这里不影响，也可以兼容runtime打包在宿主的情况。
            "com.tencent.shadow.runtime.container",
            "com.tencent.shadow.dynamic.host",
            "com.tencent.shadow.core.common"
    };

    private final static String sLoaderFactoryImplClassName
            = "com.tencent.shadow.dynamic.loader.impl.LoaderFactoryImpl";

    PluginLoaderImpl load(InstalledApk installedApk, String uuid, Context appContext) throws Exception {
        ApkClassLoader pluginLoaderClassLoader = new ApkClassLoader(
                installedApk,
                LoaderImplLoader.class.getClassLoader(),
                loadWhiteList(installedApk),
                1
        );
        LoaderFactory loaderFactory = pluginLoaderClassLoader.getInterface(
                LoaderFactory.class,
                sLoaderFactoryImplClassName
        );

        return loaderFactory.buildLoader(uuid, appContext);
    }

    @Override
    String[] getCustomWhiteList() {
        return sInterfaces;
    }
}
