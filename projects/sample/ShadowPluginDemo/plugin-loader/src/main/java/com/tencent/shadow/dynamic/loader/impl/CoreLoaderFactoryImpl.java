package com.tencent.shadow.dynamic.loader.impl;

import android.content.Context;

import com.leelu.plugin_loader.SamplePluginLoader;
import com.tencent.shadow.core.loader.ShadowPluginLoader;

import org.jetbrains.annotations.NotNull;

/**
 * CreateDate: 2022/3/16 16:27
 * Author: 李露
 * Email: lilu2@haier.com
 * Version: 1.0
 * Description:
 */
public class CoreLoaderFactoryImpl implements CoreLoaderFactory {

    @NotNull
    @Override
    public ShadowPluginLoader build(@NotNull Context hostAppContext) {
        return new SamplePluginLoader(hostAppContext);
    }
}
