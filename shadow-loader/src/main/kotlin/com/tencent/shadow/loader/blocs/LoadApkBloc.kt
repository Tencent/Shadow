package com.tencent.shadow.loader.blocs

import android.content.Context
import com.tencent.shadow.loader.classloaders.PluginClassLoader
import com.tencent.shadow.loader.exceptions.LoadApkException
import java.io.File

/**
 * 加载插件到ClassLoader中
 *
 * @author cubershi
 */
object LoadApkBloc {
    /**
     * 加载插件到ClassLoader中.
     *
     * @param apk    插件apk
     * @return 加载了插件的ClassLoader
     */
    @Throws(LoadApkException::class)
    fun loadPlugin(hostAppContext:Context, apk: File): PluginClassLoader {
        val pluginLoaderClassLoader = LoadApkBloc::class.java.classLoader
        val mockClassLoader = pluginLoaderClassLoader.parent
        val hostAppClassLoader = mockClassLoader.parent
        val bootClassLoader = hostAppClassLoader.parent
        val odexDir = File(apk.parent, apk.name + "_odex")
        val libDir = File(apk.parent, apk.name + "_lib")
        prepareDirs(odexDir, libDir)
        return PluginClassLoader(
                hostAppContext,
                apk.absolutePath,
                odexDir.absolutePath,
                libDir.absolutePath,
                bootClassLoader,
                mockClassLoader,
                arrayOf(
                        "com.tencent.shadow.runtime.MockActivity"
                        , "com.tencent.shadow.runtime.MockApplication"
                        , "com.tencent.shadow.runtime.MockService"
                        , "com.tencent.shadow.runtime.ContainerFragment"
                        , "com.tencent.shadow.runtime.ContainerDialogFragment"
                        , "com.tencent.shadow.runtime.MockFragment"
                        , "com.tencent.shadow.runtime.MockDialogFragment"
                        , "com.tencent.shadow.runtime.MockDialog"
                        , "com.tencent.shadow.runtime.PluginFragmentManager"
                        , "com.tencent.shadow.runtime.PluginFragmentTransaction"
                        , "com.tencent.shadow.runtime.MockActivityLifecycleCallbacks"
                        , "com.tencent.shadow.runtime.MockContext"
                )
        )
    }

    @Throws(LoadApkException::class)
    private fun prepareDirs(odexDir: File, libDir: File) {
        if (odexDir.exists() && !odexDir.isDirectory) {
            throw LoadApkException("odexDir目标路径" + odexDir.absolutePath
                    + "已被其他文件占用")
        } else if (!odexDir.exists()) {
            val success = odexDir.mkdir()
            if (!success) {
                throw LoadApkException("odexDir目标路径" + odexDir.absolutePath
                        + "创建目录失败")
            }
        }

        if (!libDir.exists()) {
            if (!libDir.mkdirs()) {
                throw LoadApkException("libDir目标路径" + libDir.absolutePath
                        + "创建目录失败")
            }
        }
    }
}
