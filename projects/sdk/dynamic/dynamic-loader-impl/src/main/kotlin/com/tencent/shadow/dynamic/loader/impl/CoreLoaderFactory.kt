package com.tencent.shadow.dynamic.loader.impl

import android.content.Context
import com.tencent.shadow.core.loader.ShadowPluginLoader

interface CoreLoaderFactory {
    fun build(hostAppContext: Context): ShadowPluginLoader
}