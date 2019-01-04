package com.tencent.shadow.dynamic.impl

import android.content.Context
import com.tencent.shadow.dynamic.host.LoaderFactory
import com.tencent.shadow.dynamic.host.PluginLoaderImpl
import com.tencent.shadow.dynamic.loader.impl.DynamicPluginLoader
import com.tencent.shadow.dynamic.loader.impl.PluginLoaderBinder

class LoaderFactoryImpl : LoaderFactory {
    override fun buildLoader(p0: String, p2: Context): PluginLoaderImpl {
        return PluginLoaderBinder(DynamicPluginLoader(p2, p0))
    }
}