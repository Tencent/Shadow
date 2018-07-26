package com.tencent.shadow.loader.blocs

import com.tencent.hydevteam.pluginframework.pluginloader.LoadPluginException
import java.io.File
import java.util.zip.ZipFile

object CopySoBloc {
    fun copySo(apk: File, abi: String) {
        if (abi.isEmpty()) {
            return
        }
        val soDir = File(apk.parent, apk.name + "_lib")
        soDir.mkdirs()
        val pattern = "lib/$abi/"
        var count = 0
        ZipFile(apk).use { zip ->
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
        if (count == 0) {
            throw LoadPluginException("apk==${apk.absolutePath}中没有${abi}的so")
        }
    }
}