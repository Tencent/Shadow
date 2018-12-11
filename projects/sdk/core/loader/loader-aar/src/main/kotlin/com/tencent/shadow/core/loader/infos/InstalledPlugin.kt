package com.tencent.shadow.core.loader.infos

import java.io.File

class InstalledPlugin(
        val pluginFile: File,
        val pluginFileType: Int,
        val pluginPackageName: String,
        val pluginVersionForPluginLoaderManage: String
)
