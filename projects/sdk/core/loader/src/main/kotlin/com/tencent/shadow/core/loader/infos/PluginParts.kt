package com.tencent.shadow.core.loader.infos

import android.content.res.Resources
import com.tencent.shadow.core.loader.classloaders.PluginClassLoader
import com.tencent.shadow.core.loader.managers.PluginPackageManager
import com.tencent.shadow.runtime.ShadowApplication

class PluginParts(val packageManager: PluginPackageManager,
                  val application: ShadowApplication,
                  val classLoader: PluginClassLoader,
                  val resources: Resources)