package com.tencent.shadow.core.loader.infos

import android.content.res.Resources
import com.tencent.shadow.core.loader.classloaders.PluginClassLoader
import com.tencent.shadow.runtime.ShadowApplication

class PluginParts(val application: ShadowApplication,
                  val classLoader: PluginClassLoader,
                  val resources: Resources)