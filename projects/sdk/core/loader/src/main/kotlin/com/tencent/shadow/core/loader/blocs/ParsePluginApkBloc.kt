package com.tencent.shadow.core.loader.blocs

import android.content.Context
import android.content.pm.PackageManager.*
import com.tencent.shadow.core.common.InstalledApk
import com.tencent.shadow.core.loader.LoadParameters
import com.tencent.shadow.core.loader.exceptions.ParsePluginApkException
import com.tencent.shadow.core.loader.infos.PluginActivityInfo
import com.tencent.shadow.core.loader.infos.PluginInfo
import com.tencent.shadow.core.loader.infos.PluginProviderInfo
import com.tencent.shadow.core.loader.infos.PluginServiceInfo

/**
 * 解析插件apk逻辑
 *
 * @author cubershi
 */
object ParsePluginApkBloc {
    /**
     * 解析插件apk
     *
     * @param pluginFile 插件apk文件
     * @return 解析信息
     * @throws ParsePluginApkException 解析失败时抛出
     */
    @Throws(ParsePluginApkException::class)
    fun parse(installedApk: InstalledApk, loadParameters: LoadParameters, hostAppContext: Context): PluginInfo {
        val archiveFilePath = installedApk.apkFilePath
        val packageManager = hostAppContext.packageManager
        val packageArchiveInfo = packageManager.getPackageArchiveInfo(
                archiveFilePath,
                GET_ACTIVITIES or GET_META_DATA or GET_SERVICES or GET_PROVIDERS or  GET_SIGNATURES
        )

        if (packageArchiveInfo.applicationInfo.packageName != hostAppContext.packageName) {
            /*
            要求插件和宿主包名一致有两方面原因：
            1.正常的构建过程中，aapt会将包名写入到arsc文件中。插件正常安装运行时，如果以
            android.content.Context.getPackageName为参数传给
            android.content.res.Resources.getIdentifier方法，可以正常获取到资源。但是在插件环境运行时，
            Context.getPackageName会得到宿主的packageName，则getIdentifier方法不能正常获取到资源。为此，
            一个可选的办法是继承Resources，覆盖getIdentifier方法。但是Resources的构造器已经被标记为
            @Deprecated了，未来可能会不可用，因此不首选这个方法。

            2.Android系统，更多情况下是OEM修改的Android系统，会在我们的context上调用getPackageName或者
            getOpPackageName等方法，然后将这个packageName跨进程传递做它用。系统的其他代码会以这个packageName
            去PackageManager中查询权限等信息。如果插件使用自己的包名，就需要在Context的getPackageName等实现中
            new Throwable()，然后判断调用来源以决定返回自己的包名还是插件的包名。但是如果保持采用宿主的包名，则没有
            这个烦恼。

            我们也可以始终认为Shadow App是宿主的扩展代码，使用是宿主的一部分，那么采用宿主的包名就是理所应当的了。
             */
            throw ParsePluginApkException("插件和宿主包名不一致。宿主:${hostAppContext.packageName} 插件:${packageArchiveInfo.applicationInfo.packageName}")
        }

        /*
        partKey的作用是用来区分一个Component是来自于哪个插件apk的
         */
        val partKey = loadParameters.partKey

        val pluginInfo = PluginInfo(
                partKey
                , packageArchiveInfo.applicationInfo.packageName
                , packageArchiveInfo.applicationInfo.className
                , packageArchiveInfo.applicationInfo.metaData
                , packageArchiveInfo.versionCode
                , packageArchiveInfo.versionName
                , packageArchiveInfo.signatures
        )
        packageArchiveInfo.activities?.forEach {
            pluginInfo.putActivityInfo(PluginActivityInfo(it.name, it.themeResource, it))
        }
        packageArchiveInfo.services?.forEach { pluginInfo.putServiceInfo(PluginServiceInfo(it.name)) }
        packageArchiveInfo.providers?.forEach {
            pluginInfo.putPluginProviderInfo(PluginProviderInfo(it.name, it.authority, it))
        }
        return pluginInfo
    }
}
