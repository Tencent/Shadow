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

package com.tencent.shadow.core.loader.classloaders

import android.os.Build
import dalvik.system.BaseDexClassLoader
import java.io.File


/**
 * 用于加载插件的ClassLoader,插件内部的classLoader树结构如下
 *                       BootClassLoader
 *                              |
 *                      xxxClassLoader
 *                        |        |
 *               PathClassLoader   |
 *                 |               |
 *     PluginClassLoaderA  CombineClassLoader
 *                                 |
 *  PluginClassLoaderB        PluginClassLoaderC
 *
*/
class PluginClassLoader(
        dexPath: String,
        optimizedDirectory: File?,
        librarySearchPath: String?,
        parent: ClassLoader,
        private val specialClassLoader: ClassLoader?, hostWhiteList: Array<String>?
) : BaseDexClassLoader(dexPath, optimizedDirectory, librarySearchPath, parent) {

    /**
     * 宿主的白名单包名
     * 在白名单包里面的宿主类，插件才可以访问
     */
    private val allHostWhiteList: Array<String>

    private val loaderClassLoader = PluginClassLoader::class.java.classLoader!!

    init {
        val defaultWhiteList = arrayOf(
                               "org.apache.commons.logging"//org.apache.commons.logging是非常特殊的的包,由系统放到App的PathClassLoader中.
        )
        if (hostWhiteList != null) {
            allHostWhiteList = defaultWhiteList.plus(hostWhiteList)
        }else {
            allHostWhiteList = defaultWhiteList
        }
    }

    @Throws(ClassNotFoundException::class)
    override fun loadClass(className: String, resolve: Boolean): Class<*> {
        if (specialClassLoader == null) {//specialClassLoader 为null 表示该classLoader依赖了其他的插件classLoader，需要遵循双亲委派
            return super.loadClass(className, resolve)
        } else if (className.subStringBeforeDot() == "com.tencent.shadow.core.runtime") {
            return loaderClassLoader.loadClass(className)
        } else if (className.inPackage(allHostWhiteList)
                || (Build.VERSION.SDK_INT < 28 && className.startsWith("org.apache.http"))) {//Android 9.0以下的系统里面带有http包，走系统的不走本地的) {
            return super.loadClass(className, resolve)
        } else {
            var clazz: Class<*>? = findLoadedClass(className)

            if (clazz == null) {
                var suppressed: ClassNotFoundException? = null
                try {
                    clazz = specialClassLoader.loadClass(className)!!
                } catch (e: ClassNotFoundException) {
                    suppressed = e
                }
                if (clazz == null) {
                    try {
                        clazz = findClass(className)!!
                    } catch (e: ClassNotFoundException) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                            e.addSuppressed(suppressed)
                        }
                        throw e
                    }

                }
            }

            return clazz
        }
    }

}

private fun String.subStringBeforeDot() = substringBeforeLast('.', "")

internal fun String.inPackage(packageNames: Array<String>): Boolean {
    val packageName = subStringBeforeDot()

    return packageNames.any {
        return@any when {
            it == "" -> false
            it == ".*" -> false
            it == ".**" -> false
            it.endsWith(".*") -> {//只允许一级子包
                val sub = packageName.subStringBeforeDot()
                if (sub.isEmpty()) {
                    false
                } else {
                    sub == it.subStringBeforeDot()
                }
            }
            it.endsWith(".**") -> {//允许所有子包
                val sub = packageName.subStringBeforeDot()
                if (sub.isEmpty()) {
                    false
                } else {
                    "$sub.".startsWith(it.subStringBeforeDot() + '.')
                }
            }
            else -> packageName == it
        }
    }
}


