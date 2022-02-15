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
import com.tencent.shadow.core.runtime.PluginManifest
import dalvik.system.BaseDexClassLoader
import org.jetbrains.annotations.TestOnly
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
    private val allHostWhiteTrie = PackageNameTrie()

    private val loaderClassLoader = PluginClassLoader::class.java.classLoader!!

    init {
        hostWhiteList?.forEach {
            allHostWhiteTrie.insert(it)
        }

        //org.apache.commons.logging是非常特殊的的包,由系统放到App的PathClassLoader中.
        allHostWhiteTrie.insert("org.apache.commons.logging")

        //Android 9.0以下的系统里面带有http包，走系统的不走本地的
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            allHostWhiteTrie.insert("org.apache.http")
            allHostWhiteTrie.insert("org.apache.http.**")
        }
    }

    @Throws(ClassNotFoundException::class)
    override fun loadClass(className: String, resolve: Boolean): Class<*> {
        var clazz: Class<*>? = findLoadedClass(className)

        if (clazz == null) {
            //specialClassLoader 为null 表示该classLoader依赖了其他的插件classLoader，需要遵循双亲委派
            if (specialClassLoader == null) {
                return super.loadClass(className, resolve)
            }

            //插件依赖跟loader一起打包的runtime类，如ShadowActivity，从loader的ClassLoader加载
            if (className.subStringBeforeDot() == "com.tencent.shadow.core.runtime") {
                return loaderClassLoader.loadClass(className)
            }

            //包名在白名单中的类按双亲委派逻辑，从宿主中加载
            if (className.inPackage(allHostWhiteTrie)) {
                return super.loadClass(className, resolve)
            }

            var suppressed: ClassNotFoundException? = null
            try {
                //正常的ClassLoader这里是parent.loadClass,插件用specialClassLoader以跳过parent
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

    internal fun loadPluginManifest(): PluginManifest {
        try {
            val clazz = findClass("com.tencent.shadow.core.manifest_parser.PluginManifest")
            return PluginManifest::class.java.cast(clazz.newInstance())
        } catch (e: ClassNotFoundException) {
            throw Error(
                "请注意每个插件apk构建时都需要" +
                        "apply plugin: 'com.tencent.shadow.plugin'", e
            )
        }
    }

}

private fun String.subStringBeforeDot() = substringBeforeLast('.', "")

@Deprecated("use PackageNameTrie instead.")
@TestOnly
internal fun String.inPackage(packageNames: Array<String>): Boolean {
    val trie = PackageNameTrie()
    packageNames.forEach {
        trie.insert(it)
    }
    return inPackage(trie)
}

private fun String.inPackage(packageNames: PackageNameTrie): Boolean {
    val packageName = subStringBeforeDot()
    return packageNames.isMatch(packageName)
}

/**
 * 基于Trie算法对包名进行前缀匹配
 */
private class PackageNameTrie {
    private class Node {
        val subNodes = mutableMapOf<String, Node>()
        var isLastPackageOfARule = false
    }

    private val root = Node()

    fun insert(packageNameRule: String) {
        var node = root
        packageNameRule.split('.').forEach {
            if (it.isEmpty()) return //"",".*",".**"这种无包名情况不允许设置

            var subNode = node.subNodes[it]
            if (subNode == null) {
                subNode = Node()
                node.subNodes[it] = subNode
            }
            node = subNode
        }
        node.isLastPackageOfARule = true
    }

    fun isMatch(packageName: String): Boolean {
        var node = root

        val split = packageName.split('.')
        val lastIndex = split.size - 1
        for ((index, name) in split.withIndex()) {
            // 只要下级包名规则中有**，就完成了匹配
            val twoStars = node.subNodes["**"]
            if (twoStars != null) {
                return true
            }

            // 剩最后一级包名时，如果规则是*则完成比配
            if (index == lastIndex) {
                val oneStar = node.subNodes["*"]
                if (oneStar != null) {
                    return true
                }
            }

            // 找不到下级包名时即匹配失败
            val subNode = node.subNodes[name]
            if (subNode == null) {
                return false
            } else {
                node = subNode
            }
        }
        return node.isLastPackageOfARule
    }
}
