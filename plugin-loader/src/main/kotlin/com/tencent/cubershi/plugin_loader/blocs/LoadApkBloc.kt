package com.tencent.cubershi.plugin_loader.blocs

import android.content.Context
import com.tencent.cubershi.plugin_loader.classloaders.PluginClassLoader
import com.tencent.cubershi.plugin_loader.exceptions.LoadApkException
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
                        "com.tencent.cubershi.mock_interface.MockActivity"
                        , "com.tencent.cubershi.mock_interface.PluginFragment"
                        , "com.tencent.cubershi.mock_interface.MockApplication"
                        , "com.tencent.cubershi.mock_interface.MockService"
                        , "com.tencent.cubershi.mock_interface.ContainerFragment"
                        , "com.tencent.cubershi.mock_interface.ContainerDialogFragment"
                        , "com.tencent.cubershi.mock_interface.MockFragment"
                        , "com.tencent.cubershi.mock_interface.MockDialogFragment"
                        , "com.tencent.cubershi.mock_interface.MockDialog"
                        , "com.tencent.cubershi.mock_interface.PluginFragmentManager"
                        , "com.tencent.cubershi.mock_interface.PluginFragmentTransaction"
                        , "com.tencent.cubershi.mock_interface.MockActivityLifecycleCallbacks"
                        , "com.tencent.cubershi.mock_interface.MockWebView"
                        , "com.tencent.cubershi.mock_interface.MockPendingIntent"
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
