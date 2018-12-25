package com.tencent.shadow.dynamic.impl

import android.content.Context
import android.os.IBinder
import com.tencent.shadow.dynamic.host.LoaderFactory
import com.tencent.shadow.dynamic.host.UuidManager
import com.tencent.shadow.dynamic.loader.DynamicPluginLoader

class LoaderFactoryImpl : LoaderFactory {
    override fun buildLoader(p0: String, p1: UuidManager, p2: Context): IBinder {
        return DynamicPluginLoader(p2, p1, p0)
    }
}