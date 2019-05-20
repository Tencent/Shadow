package com.tencent.shadow.dynamic.loader.impl;

import android.content.Context;

import com.tencent.shadow.core.loader.ShadowPluginLoader;
import com.tencent.shadow.test.dynamic.loader.TestPluginLoader;

/**
 * 这个类的包名类名是固定的。
 * <p>
 * 见com.tencent.shadow.dynamic.loader.impl.DynamicPluginLoader#CORE_LOADER_FACTORY_IMPL_NAME
 */
public class CoreLoaderFactoryImpl implements CoreLoaderFactory {
    @Override
    public ShadowPluginLoader build(Context hostAppContext) {
        return new TestPluginLoader(hostAppContext);
    }
}
