package com.tencent.cubershi.plugin_loader.blocs

import android.content.Context
import android.content.pm.PackageManager
import android.content.pm.PackageManager.GET_META_DATA
import android.content.res.Resources

object CreateResourceBloc {
    fun create(archiveFilePath: String, hostAppContext: Context): Resources {
        val packageManager = hostAppContext.packageManager
        val packageArchiveInfo = packageManager.getPackageArchiveInfo(archiveFilePath, GET_META_DATA)
        packageArchiveInfo.applicationInfo.publicSourceDir = archiveFilePath
        try {
            return packageManager.getResourcesForApplication(packageArchiveInfo.applicationInfo)
        } catch (e: PackageManager.NameNotFoundException) {
            throw RuntimeException(e)
        }

    }
}
