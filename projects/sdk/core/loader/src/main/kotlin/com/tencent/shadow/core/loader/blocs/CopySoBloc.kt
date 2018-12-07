package com.tencent.shadow.core.loader.blocs

import android.content.Context
import android.os.Build.VERSION_CODES.JELLY_BEAN_MR2
import com.tencent.hydevteam.pluginframework.installedplugin.InstalledPlugin
import com.tencent.hydevteam.pluginframework.pluginloader.LoadPluginException
import java.io.Closeable
import java.io.File
import java.util.zip.ZipFile

object CopySoBloc {
    fun copySo(hostContext: Context, installedPlugin: InstalledPlugin, abi: String): File {
        val apk = installedPlugin.pluginFile
        val soDir = PluginRunningPath.getPluginLibDir(hostContext, installedPlugin.pluginPackageName, installedPlugin.pluginVersionForPluginLoaderManage)
        val copiedTagFile = File(soDir, "copied")

        //如果不需要so或者so文件已复制完成的标记已存在则直接返回成功
        if (abi.isEmpty() || copiedTagFile.exists()) {
            return soDir
        }

        //如果so目录存在，但copiedTagFile不存在，可能是上一次复制中断了，因此清除目录
        if (soDir.exists() && soDir.isDirectory) {
            val success = soDir.deleteRecursively()
            if (!success) {
                throw LoadPluginException("soDir==${soDir.absolutePath}目录已存在，需要清理，但清理失败")
            }
        }

        //如果so目录存在但是个文件，不是目录，那超出预料了。删除了也不一定能工作正常。
        if (soDir.exists() && soDir.isFile) {
            throw LoadPluginException("soDir==${soDir.absolutePath}已存在，但它是个文件，不敢贸然删除")
        }

        //(重新)创建so目录
        val mkdiruccess = soDir.mkdirs()
        if (!mkdiruccess) {
            throw LoadPluginException("创建soDir==${soDir.absolutePath}目录失败")
        }

        try {
            val count = unzipSo(abi, apk, soDir)
            try {
                copiedTagFile.createNewFile()
            } catch (e: Exception) {
                throw LoadPluginException("创建so复制完毕Tag文件${copiedTagFile.absolutePath}失败", e)
            }
            if (count == 0) {
                throw LoadPluginException("apk==${apk.absolutePath}中没有${abi}的so")
            }
        } catch (e: Exception) {
            throw LoadPluginException("解压so失败", e)
        }

        return soDir
    }

    private fun unzipSo(abi: String, apk: File, soDir: File): Int {
        var count = 0
        val pattern = "lib/$abi/"

        val zipFile = if (android.os.Build.VERSION.SDK_INT <= JELLY_BEAN_MR2) {
            object : ZipFile(apk), Closeable {
            }
        } else {
            ZipFile(apk)
        }

        zipFile.use { zip ->
            zip.entries().asSequence().filter {
                it.name.startsWith(pattern)
            }.forEach { soFileEntry ->
                zip.getInputStream(soFileEntry).use { inputStream ->
                    File(soDir, soFileEntry.name.removePrefix(pattern)).createNewFile()
                    File(soDir, soFileEntry.name.removePrefix(pattern)).outputStream().use {
                        inputStream.copyTo(it)
                    }
                }
                count++
            }
        }
        return count
    }
}