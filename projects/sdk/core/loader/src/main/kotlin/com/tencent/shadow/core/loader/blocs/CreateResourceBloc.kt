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

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.res.AssetManager
import android.content.res.Resources
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.webkit.WebView
import java.util.concurrent.CountDownLatch

object CreateResourceBloc {
    fun create(archiveFilePath: String, hostAppContext: Context): Resources {
        triggerWebViewHookResources(hostAppContext)

        val packageManager = hostAppContext.packageManager
        val applicationInfo = ApplicationInfo()
        val hostApplicationInfo = hostAppContext.applicationInfo
        applicationInfo.packageName = hostApplicationInfo.packageName
        applicationInfo.uid = hostApplicationInfo.uid

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            fillApplicationInfoForNewerApi(applicationInfo, hostApplicationInfo, archiveFilePath)
        } else {
            fillApplicationInfoForLowerApi(applicationInfo, hostApplicationInfo, archiveFilePath)
        }

        try {
            val pluginResource = packageManager.getResourcesForApplication(applicationInfo)

            // API 23以下设置applicationInfo.sharedLibraryFiles无效
            // API 26以下Resources.getIdentifier只支持从主apk中获取资源
            // 因此API 26以下采用传统方法通过addAssetPath添加宿主资源
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                pluginResource.assets.addAssetPath(hostApplicationInfo.sourceDir)
            }

            return pluginResource
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
     * API 25及以下系统，Resources.getIdentifier方法只能查询applicationInfo.sourceDir设置的
     * 主apk中的资源。因此把插件apk作为主apk添加进去。
     * 等构造完Resources对象再用addAssetPath方法将宿主资源添加进去。
     * 我们假设系统只会直接用宿主manifest中固定的资源ID来访问资源，
     * 并不会用getIdentifier方法查询。
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

    /**
     * f208e8be 去掉MixResources之后，低版本系统对资源ID分区的支持不完整，
     * 所以要么在低版本还采用MixResources方案，要么反射addAssetPath方法。
     * 考虑低版本系统的addAssetPath方法还是比较稳定的，
     * 并且我们只在API 26以下使用，此时还没有非公开API限制，
     * addAssetPath作为一个public方法还是比较稳定的。
     * 权衡之下，添加这个反射方法，而不是恢复MixResources方案。
     */
    @SuppressLint("PrivateApi")
    fun AssetManager.addAssetPath(path: String) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            throw IllegalAccessError("不支持API26以上环境调用")
        }
        val method =
            AssetManager::class.java.getDeclaredMethod("addAssetPath", String::class.java)
        method.invoke(this, path)
    }
}
