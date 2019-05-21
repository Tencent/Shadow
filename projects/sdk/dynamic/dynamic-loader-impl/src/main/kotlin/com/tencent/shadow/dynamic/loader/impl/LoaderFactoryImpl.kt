package com.tencent.shadow.dynamic.loader.impl

import android.content.Context
import com.tencent.shadow.dynamic.host.LoaderFactory
import com.tencent.shadow.dynamic.host.PluginLoaderImpl

open class LoaderFactoryImpl : LoaderFactory {
    override fun buildLoader(p0: String, p2: Context): PluginLoaderImpl {
        return PluginLoaderBinder(DynamicPluginLoader(p2, p0))
    }
}