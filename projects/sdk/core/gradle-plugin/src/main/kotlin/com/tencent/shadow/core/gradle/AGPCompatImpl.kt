package com.tencent.shadow.core.gradle

import com.android.SdkConstants
import com.android.build.gradle.BaseExtension
import com.android.build.gradle.api.BaseVariantOutput
import com.android.build.gradle.internal.dsl.ProductFlavor
import com.android.build.gradle.tasks.ProcessApplicationManifest
import com.android.build.gradle.tasks.ProcessMultiApkApplicationManifest
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.provider.Property
import java.io.File

internal class AGPCompatImpl : AGPCompat {

    override fun getProcessManifestTask(output: BaseVariantOutput): Task =
        try {
            output.processManifestProvider.get()
        } catch (e: NoSuchMethodError) {
            output.processManifest
        }

    override fun getManifestFile(processManifestTask: Task) =
        when (processManifestTask.javaClass.superclass.simpleName) {
            "ProcessMultiApkApplicationManifest" -> {
                (processManifestTask as ProcessMultiApkApplicationManifest)
                    .mainMergedManifest.get().asFile
            }
            "ProcessApplicationManifest" -> {
                try {
                    (processManifestTask as ProcessApplicationManifest)
                        .mergedManifest.get().asFile
                } catch (e: NoSuchMethodError) {
                    //AGP小于4.1.0
                    val dir =
                        processManifestTask.outputs.files.files
                            .first { it.parentFile.name == "merged_manifests" }
                    File(dir, SdkConstants.ANDROID_MANIFEST_XML)
                }
            }
            "MergeManifests" -> {
                val dir = try {// AGP 3.2.0
                    processManifestTask.outputs.files.files
                        .first { it.parentFile.parentFile.parentFile.name == "merged_manifests" }
                } catch (e: NoSuchElementException) {
                    // AGP 3.1.0
                    processManifestTask.outputs.files.files
                        .first { it.path.contains("intermediates${File.separator}manifests${File.separator}full${File.separator}") }
                }
                File(dir, SdkConstants.ANDROID_MANIFEST_XML)
            }
            else -> throw IllegalStateException("不支持的Task类型:${processManifestTask.javaClass}")
        }

    override fun getPackageForR(project: Project, variantName: String): String {
        val linkApplicationAndroidResourcesTask =
            project.tasks.getByName("process${variantName.capitalize()}Resources")
        return getStringFromProperty(
            when {
                linkApplicationAndroidResourcesTask.hasProperty("namespace") -> {
                    linkApplicationAndroidResourcesTask.property("namespace")
                }
                linkApplicationAndroidResourcesTask.hasProperty("originalApplicationId") -> {
                    linkApplicationAndroidResourcesTask.property("originalApplicationId")
                }
                linkApplicationAndroidResourcesTask.hasProperty("packageName") -> {
                    linkApplicationAndroidResourcesTask.property("packageName")
                }
                else -> throw IllegalStateException("不支持的AGP版本")
            }
        )
    }

    override fun addFlavorDimension(baseExtension: BaseExtension, dimensionName: String) {
        val flavorDimensionList = baseExtension.flavorDimensionList
                as MutableList<String>? // AGP 3.6.0版本可能返回null
        if (flavorDimensionList != null) {
            flavorDimensionList.add(dimensionName)
        } else {
            baseExtension.flavorDimensions(dimensionName)
        }
    }

    override fun setProductFlavorDefault(productFlavor: ProductFlavor, isDefault: Boolean) {
        try {
            productFlavor.isDefault = isDefault
        } catch (ignored: NoSuchMethodError) {
            // AGP 3.6.0版本没有这个方法，就不设置了。
            // 设置Default主要是为了IDE中的Build Variants上下文自动选择时不要选成插件，
            // 以便在IDE直接运行插件apk模块时运行Normal版本
        }
    }

    companion object {
        fun getStringFromProperty(x: Any?): String {
            return when (x) {
                is String -> x
                is Property<*> -> x.get() as String
                else -> throw Error("不支持的AGP版本")
            }
        }
    }
}
