package com.tencent.shadow.pluginloader.blocs

import java.io.File
import java.util.zip.ZipFile

object CopySoBloc {
    fun copySo(apk: File, abi: String) {
        val soDir = File(apk.parent, apk.name + "_lib")
        soDir.mkdirs()
        val pattern = "lib/$abi/"
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
            }
        }
    }
}