/*
 * Tencent is pleased to support the open source community by making Tencent Shadow available.
 * Copyright (C) 2019 THL A29 Limited, a Tencent company.  All rights reserved.
 *
 * Licensed under the BSD 3-Clause License (the "License"); you may not use
 * this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 *     https://opensource.org/licenses/BSD-3-Clause
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.tencent.shadow.core.loader.blocs

import android.annotation.TargetApi
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.content.res.Resources
import android.content.res.XmlResourceParser
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.TypedValue
import android.webkit.WebView
import java.util.concurrent.CountDownLatch

object CreateResourceBloc {
    /**
     * 构造插件Resources时有两个方案，都要解决一个共同的问题：
     * 系统有可能从宿主Manifest中获取app icon或者logo的资源ID，
     * 然后直接向插件的Resources对象查询这些资源。
     *
     * 第一个方案是MixResources方案，但该方案依赖Resources的Deprecated构造器，
     * 未来可能会不可用。实际上Resources的构造器如果不取消的话，这个方案可以一直使用下去。
     *
     * 第二个方案是利用资源分区，这是一个和AAB设计中dynamic-feature相同的方案，
     * 将宿主和插件apk添加到同一个Resources对象中。
     * 尽管构造这种带有多资源ID分区的Resources对象所需的API在低版本系统上就已经有了，
     * 但通过不断测试发现MAX_API_FOR_MIX_RESOURCES及更低的API系统上，有个别API不能正确支持非0x7f分区的资源。
     */
    const val MAX_API_FOR_MIX_RESOURCES = Build.VERSION_CODES.O_MR1

    fun create(archiveFilePath: String, hostAppContext: Context): Resources {
        triggerWebViewHookResources(hostAppContext)

        val packageManager = hostAppContext.packageManager
        val applicationInfo = ApplicationInfo()
        val hostApplicationInfo = hostAppContext.applicationInfo
        applicationInfo.packageName = hostApplicationInfo.packageName
        applicationInfo.uid = hostApplicationInfo.uid

        if (Build.VERSION.SDK_INT > MAX_API_FOR_MIX_RESOURCES) {
            fillApplicationInfoForNewerApi(applicationInfo, hostApplicationInfo, archiveFilePath)
        } else {
            fillApplicationInfoForLowerApi(applicationInfo, hostApplicationInfo, archiveFilePath)
        }

        try {
            val pluginResource = packageManager.getResourcesForApplication(applicationInfo)

            return if (Build.VERSION.SDK_INT > MAX_API_FOR_MIX_RESOURCES) {
                pluginResource
            } else {
                val hostResources = hostAppContext.resources
                MixResources(pluginResource, hostResources)
            }
        } catch (e: PackageManager.NameNotFoundException) {
            throw RuntimeException(e)
        }

    }

    /**
     * WebView初始化时会向系统构造的Resources对象注入webview.apk，
     * 以便WebView可以使用自己的资源。
     *
     * 由于它不会向插件构造的Resources对象注入apk，
     * 所以我们先初始化它，让它注入给宿主，等插件构造Resources时从宿主中复制出该apk路径。
     */
    private fun triggerWebViewHookResources(hostAppContext: Context) {
        //先用宿主context初始化一个WebView，以便WebView的逻辑去修改sharedLibraryFiles，将webview.apk添加进去
        val latch = CountDownLatch(1)
        Handler(Looper.getMainLooper()).post {
            try {
                WebView(hostAppContext)
            } catch (ignored: Exception) {
                // API 26虚拟机报No WebView installed
            }
            latch.countDown()
        }
        latch.await()
    }

    private fun fillApplicationInfoForNewerApi(
        applicationInfo: ApplicationInfo,
        hostApplicationInfo: ApplicationInfo,
        pluginApkPath: String
    ) {
        /**
         * 这里虽然sourceDir和sharedLibraryFiles中指定的apk都会进入Resources对象，
         * 但是只有资源id分区大于0x7f时才能在加载之后保持住资源id分区。
         * 如果把宿主的apk路径放到sharedLibraryFiles中，我们假设宿主资源id分区是0x7f，
         * 则加载后会变为一个随机的分区，如0x30。因此放入sharedLibraryFiles中的apk的
         * 资源id分区都需要改为0x80或更大的值。
         *
         * 考虑到现网可能已经有旧方案运行的宿主和插件，而宿主不易更新。
         * 因此新方案假设宿主保持0x7f固定不能修改，但是插件可以重新编译新版本修改资源id分区。
         * 因此把插件apk路径放到sharedLibraryFiles中。
         *
         * 复制宿主的sharedLibraryFiles，主要是为了获取前面WebView初始化时，
         * 系统使用私有API注入的webview.apk
         */
        applicationInfo.publicSourceDir = hostApplicationInfo.publicSourceDir
        applicationInfo.sourceDir = hostApplicationInfo.sourceDir

        // hostSharedLibraryFiles中可能有webview通过私有api注入的webview.apk
        val hostSharedLibraryFiles = hostApplicationInfo.sharedLibraryFiles
        val otherApksAddToResources =
            if (hostSharedLibraryFiles == null)
                arrayOf(pluginApkPath)
            else
                arrayOf(
                    *hostSharedLibraryFiles,
                    pluginApkPath
                )

        applicationInfo.sharedLibraryFiles = otherApksAddToResources
    }

    /**
     * API 25及以下系统，单独构造插件资源
     */
    private fun fillApplicationInfoForLowerApi(
        applicationInfo: ApplicationInfo,
        hostApplicationInfo: ApplicationInfo,
        pluginApkPath: String
    ) {
        applicationInfo.publicSourceDir = pluginApkPath
        applicationInfo.sourceDir = pluginApkPath
        applicationInfo.sharedLibraryFiles = hostApplicationInfo.sharedLibraryFiles
    }
}

/**
 * 在API 25及以下代替设置sharedLibraryFiles后通过getResourcesForApplication创建资源的方案。
 * 因调用addAssetPath方法也无法满足CreateResourceTest涉及的场景。
 */
@Suppress("DEPRECATION", "OVERRIDE_DEPRECATION")
@TargetApi(CreateResourceBloc.MAX_API_FOR_MIX_RESOURCES)
private class MixResources(
    private val mainResources: Resources,
    private val sharedResources: Resources
) : Resources(mainResources.assets, mainResources.displayMetrics, mainResources.configuration) {

    private var beforeInitDone = false
    private var updateConfigurationCalledInInit = false

    /**
     * 低版本系统中Resources构造器中会调用updateConfiguration方法，
     * 此时mainResources还没有初始化。
     */
    init {
        if (updateConfigurationCalledInInit) {
            updateConfiguration(mainResources.configuration, mainResources.displayMetrics)
        }
        beforeInitDone = true
    }

    private fun <R> tryMainThenShared(function: (res: Resources) -> R) = try {
        function(mainResources)
    } catch (e: NotFoundException) {
        function(sharedResources)
    }

    override fun getText(id: Int) = tryMainThenShared { it.getText(id) }

    override fun getText(id: Int, def: CharSequence?) = tryMainThenShared { it.getText(id, def) }

    override fun getQuantityText(id: Int, quantity: Int) =
        tryMainThenShared { it.getQuantityText(id, quantity) }

    override fun getString(id: Int) =
        tryMainThenShared { it.getString(id) }

    override fun getString(id: Int, vararg formatArgs: Any?) =
        tryMainThenShared { it.getString(id, *formatArgs) }


    override fun getQuantityString(id: Int, quantity: Int, vararg formatArgs: Any?) =
        tryMainThenShared { it.getQuantityString(id, quantity, *formatArgs) }

    override fun getQuantityString(id: Int, quantity: Int) =
        tryMainThenShared {
            it.getQuantityString(id, quantity)
        }

    override fun getTextArray(id: Int) =
        tryMainThenShared {
            it.getTextArray(id)
        }

    override fun getStringArray(id: Int) =
        tryMainThenShared {
            it.getStringArray(id)
        }

    override fun getIntArray(id: Int) =
        tryMainThenShared {
            it.getIntArray(id)
        }

    override fun obtainTypedArray(id: Int) =
        tryMainThenShared {
            it.obtainTypedArray(id)
        }

    override fun getDimension(id: Int) =
        tryMainThenShared {
            it.getDimension(id)
        }

    override fun getDimensionPixelOffset(id: Int) =
        tryMainThenShared {
            it.getDimensionPixelOffset(id)
        }

    override fun getDimensionPixelSize(id: Int) =
        tryMainThenShared {
            it.getDimensionPixelSize(id)
        }

    override fun getFraction(id: Int, base: Int, pbase: Int) =
        tryMainThenShared {
            it.getFraction(id, base, pbase)
        }

    override fun getDrawable(id: Int) =
        tryMainThenShared {
            it.getDrawable(id)
        }

    override fun getDrawable(id: Int, theme: Theme?) =
        tryMainThenShared {
            it.getDrawable(id, theme)
        }

    override fun getDrawableForDensity(id: Int, density: Int) =
        tryMainThenShared {
            it.getDrawableForDensity(id, density)
        }

    override fun getDrawableForDensity(id: Int, density: Int, theme: Theme?) =
        tryMainThenShared {
            it.getDrawableForDensity(id, density, theme)
        }

    override fun getMovie(id: Int) =
        tryMainThenShared {
            it.getMovie(id)
        }

    override fun getColor(id: Int) =
        tryMainThenShared {
            it.getColor(id)
        }

    override fun getColor(id: Int, theme: Theme?) =
        tryMainThenShared {
            it.getColor(id, theme)
        }

    override fun getColorStateList(id: Int) =
        tryMainThenShared {
            it.getColorStateList(id)
        }

    override fun getColorStateList(id: Int, theme: Theme?) =
        tryMainThenShared {
            it.getColorStateList(id, theme)
        }

    override fun getBoolean(id: Int) =
        tryMainThenShared {
            it.getBoolean(id)
        }

    override fun getInteger(id: Int) =
        tryMainThenShared {
            it.getInteger(id)
        }

    override fun getLayout(id: Int) =
        tryMainThenShared {
            it.getLayout(id)
        }

    override fun getAnimation(id: Int) =
        tryMainThenShared {
            it.getAnimation(id)
        }

    override fun getXml(id: Int) =
        tryMainThenShared {
            it.getXml(id)
        }

    override fun openRawResource(id: Int) =
        tryMainThenShared {
            it.openRawResource(id)
        }

    override fun openRawResource(id: Int, value: TypedValue?) =
        tryMainThenShared {
            it.openRawResource(id, value)
        }

    override fun openRawResourceFd(id: Int) =
        tryMainThenShared {
            it.openRawResourceFd(id)
        }

    override fun getValue(id: Int, outValue: TypedValue?, resolveRefs: Boolean) =
        tryMainThenShared {
            it.getValue(id, outValue, resolveRefs)
        }

    override fun getValue(name: String?, outValue: TypedValue?, resolveRefs: Boolean) =
        tryMainThenShared {
            it.getValue(name, outValue, resolveRefs)
        }

    override fun getValueForDensity(
        id: Int,
        density: Int,
        outValue: TypedValue?,
        resolveRefs: Boolean
    ) =
        tryMainThenShared {
            it.getValueForDensity(id, density, outValue, resolveRefs)
        }

    override fun obtainAttributes(set: AttributeSet?, attrs: IntArray?) =
        tryMainThenShared {
            it.obtainAttributes(set, attrs)
        }

    override fun updateConfiguration(config: Configuration?, metrics: DisplayMetrics?) {
        if (beforeInitDone) {
            tryMainThenShared {
                it.updateConfiguration(config, metrics)
            }
        }
    }

    override fun getDisplayMetrics() =
        tryMainThenShared {
            it.getDisplayMetrics()
        }

    override fun getConfiguration() =
        tryMainThenShared {
            it.getConfiguration()
        }

    override fun getIdentifier(name: String?, defType: String?, defPackage: String?) =
        tryMainThenShared {
            it.getIdentifier(name, defType, defPackage)
        }

    override fun getResourceName(resid: Int) =
        tryMainThenShared {
            it.getResourceName(resid)
        }

    override fun getResourcePackageName(resid: Int) =
        tryMainThenShared {
            it.getResourcePackageName(resid)
        }

    override fun getResourceTypeName(resid: Int) =
        tryMainThenShared {
            it.getResourceTypeName(resid)
        }

    override fun getResourceEntryName(resid: Int) =
        tryMainThenShared {
            it.getResourceEntryName(resid)
        }

    override fun parseBundleExtras(parser: XmlResourceParser?, outBundle: Bundle?) =
        tryMainThenShared {
            it.parseBundleExtras(parser, outBundle)
        }

    override fun parseBundleExtra(tagName: String?, attrs: AttributeSet?, outBundle: Bundle?) =
        tryMainThenShared {
            it.parseBundleExtra(tagName, attrs, outBundle)
        }
}