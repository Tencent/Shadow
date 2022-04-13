package com.tencent.shadow.core.gradle

import com.android.SdkConstants
import com.android.build.gradle.BaseExtension
import com.android.build.gradle.api.ApplicationVariant
import com.android.build.gradle.api.BaseVariantOutput
import com.android.build.gradle.internal.dsl.ProductFlavor
import com.android.build.gradle.internal.res.LinkApplicationAndroidResourcesTask
import com.android.build.gradle.tasks.ProcessApplicationManifest
import com.android.build.gradle.tasks.ProcessMultiApkApplicationManifest
import com.android.sdklib.AndroidVersion.VersionCodes
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

    override fun getProcessResourcesTask(output: BaseVariantOutput): Task =
        try {
            output.processResourcesProvider.get()
        } catch (e: NoSuchMethodError) {
            output.processResources
        }

    @Suppress("PrivateApi")
    override fun getAaptAdditionalParameters(processResourcesTask: Task): List<String> =
        try {
            if (processResourcesTask is LinkApplicationAndroidResourcesTask) {
                processResourcesTask.aaptAdditionalParameters.get()
            } else {
                TODO("不支持的AGP版本")
            }
        } catch (ignored: NoSuchMethodError) {
            //AGP 4.0.0
            val aaptOptionsField =
                LinkApplicationAndroidResourcesTask::class.java.getDeclaredField("aaptOptions")
            aaptOptionsField.isAccessible = true
            val aaptOptions = aaptOptionsField.get(processResourcesTask)
            val additionalParametersField = try {
                aaptOptions.javaClass.getDeclaredField("additionalParameters")
            } catch (ignored: NoSuchFieldException) {
                //AGP 3.4.0
                aaptOptions.javaClass.superclass.getDeclaredField("additionalParameters")
            }

            additionalParametersField.isAccessible = true
            @Suppress("UNCHECKED_CAST")
            val additionalParameters = additionalParametersField.get(aaptOptions) as? List<String>
            additionalParameters ?: listOf()
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

    override fun getMinSdkVersion(pluginVariant: ApplicationVariant): Int {
        // AGP在版本升级中修改了MergedFlavor的包名，但是它实现的ProductFlavor接口没有变
        val mergedFlavor = pluginVariant.mergedFlavor as com.android.builder.model.ProductFlavor
        return mergedFlavor.minSdkVersion?.apiLevel ?: VersionCodes.BASE
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
