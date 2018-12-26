package com.tencent.shadow.dynamic.impl

import android.content.Context
import com.tencent.shadow.dynamic.host.LoaderFactory
import com.tencent.shadow.dynamic.host.PluginLoaderImpl
import com.tencent.shadow.dynamic.loader.DynamicPluginLoader

class LoaderFactoryImpl : LoaderFactory {
    override fun buildLoader(p0: String, p2: Context): PluginLoaderImpl {
        return DynamicPluginLoader(p2, p0)
    }
}