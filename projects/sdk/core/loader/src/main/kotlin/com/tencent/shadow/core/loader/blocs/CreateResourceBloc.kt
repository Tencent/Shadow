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

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.res.Resources
import android.os.Handler
import android.os.Looper
import android.webkit.WebView
import java.util.concurrent.CountDownLatch

object CreateResourceBloc {
    fun create(archiveFilePath: String, hostAppContext: Context): Resources {
        //先用宿主context初始化一个WebView，以便WebView的逻辑去修改sharedLibraryFiles，将webview.apk添加进去
        val latch = CountDownLatch(1)
        Handler(Looper.getMainLooper()).post {
            WebView(hostAppContext)
            latch.countDown()
        }
        latch.await()

        val packageManager = hostAppContext.packageManager
        val applicationInfo = ApplicationInfo()
        applicationInfo.packageName = hostAppContext.applicationInfo.packageName
        applicationInfo.uid = hostAppContext.applicationInfo.uid

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
        applicationInfo.publicSourceDir = hostAppContext.applicationInfo.publicSourceDir
        applicationInfo.sourceDir = hostAppContext.applicationInfo.sourceDir
        val otherApksAddToResources = arrayOf(
            *hostAppContext.applicationInfo.sharedLibraryFiles,
            archiveFilePath
        )
        applicationInfo.sharedLibraryFiles = otherApksAddToResources

        try {
            return packageManager.getResourcesForApplication(applicationInfo)
        } catch (e: PackageManager.NameNotFoundException) {
            throw RuntimeException(e)
        }

    }
}
