package com.tencent.shadow.core.manifest_parser

import java.io.File
import java.util.Collections

/**
 * manifest-parser的入口方法
 *
 * @param xmlFile       com.android.build.gradle.tasks.ManifestProcessorTask任务的输出文件，
 *                      一般位于apk工程的build/intermediates/merged_manifest目录中。
 * @param outputDir     生成文件的输出目录
 * @param packageName   生成类的包名
 * @param manifestValueParser 资源解析器
 */
fun generatePluginManifest(
    xmlFile: File,
    outputDir: File,
    packageName: String,
    manifestValueParser: ManifestValueParser? = null
) {
    val androidManifest = AndroidManifestReader().read(xmlFile)
    val generator = PluginManifestGenerator()
    generator.generate(androidManifest, outputDir, packageName, manifestValueParser)
}

/**
 * 创建资源解析器。
 *
 * @param rTxt R.txt文件
 * @return 资源解析器
 */
fun createManifestValueParser(rTxt: File): ManifestValueParser {
    val rTxtMap = parseRTxt(rTxt)

    return { resName ->
        if (resName.startsWith("@android:")) {
            // @android:style/Theme.NoTitleBar -> android.R.style.Theme_NoTitleBar
            val parts = resName.substringAfter("@android:").split("/")
            val type = parts[0]
            val name = parts[1].replace(".", "_")
            "android.R.$type.$name"
        } else {
            // @[package:]type/name -> id 值
            var raw = resName.substringAfter("@")
            if (raw.contains(":")) {
                raw = raw.substringAfter(":")
            }
            val parts = raw.split("/")
            val type = parts[0]
            val name = parts[1].replace('.', '_')
            val key = "@$type/$name"
            rTxtMap[key]
                ?: throw IllegalArgumentException("Resource not found in R.txt: $resName (normalized: $key)")
        }
    }
}

/**
 * 解析 R.txt 文件并生成资源 ID 映射表。 R.txt 包含项目引用的所有资源 ID。
 *
 * @param rTxtFile R.txt 文件对象
 * @return 资源全称（如 @string/app_name）到 ID 的映射
 */
fun parseRTxt(rTxtFile: File): Map<String, String> {
    if (!rTxtFile.exists()) return Collections.emptyMap()

    val map = mutableMapOf<String, String>()
    rTxtFile.useLines {
        it.forEach { line ->
            if (!(line.startsWith("int "))) {
                return@forEach
            }
            val parts = line.split(Regex("\\s+")).filter { it.isNotBlank() }
            if (parts.size == 4 && parts[0] == "int") {
                val type = parts[1]
                val name = parts[2]
                val idStr = parts[3]
                map["@$type/$name"] = idStr
            }
        }
    }
    return map
}
