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

package com.tencent.shadow.core.gradle

import com.tencent.shadow.core.gradle.extensions.PackagePluginExtension
import com.tencent.shadow.core.gradle.extensions.PluginApkConfig
import com.tencent.shadow.core.gradle.extensions.PluginBuildType
import org.gradle.api.Project
import java.io.File
import java.io.FileInputStream
import java.security.MessageDigest
import kotlin.experimental.and

open class ShadowPluginHelper {
    companion object {
        fun getFileMD5(file: File): String? {
            if (!file.isFile) {
                return null
            }

            val buffer = ByteArray(1024)
            var len: Int
            var inStream: FileInputStream? = null
            val digest = MessageDigest.getInstance("MD5")
            try {
                inStream = FileInputStream(file)
                do {
                    len = inStream.read(buffer, 0, 1024)
                    if (len != -1) {
                        digest.update(buffer, 0, len)
                    }
                } while (len != -1)
            } catch (e: Exception) {
                e.printStackTrace()
                return null
            } finally {
                inStream?.close()
            }
            return bytes2HexStr(digest.digest())
        }

        private fun bytes2HexStr(bytes: ByteArray?): String {
            val HEX_ARRAY = "0123456789ABCDEF".toCharArray()
            if (bytes == null || bytes.isEmpty()) {
                return ""
            }

            val buf = CharArray(2 * bytes.size)
            try {
                for (i in bytes.indices) {
                    var b = bytes[i]
                    buf[2 * i + 1] = HEX_ARRAY[(b and 0xF).toInt()]
                    b = b.toInt().ushr(4).toByte()
                    buf[2 * i + 0] = HEX_ARRAY[(b and 0xF).toInt()]
                }
            } catch (e: Exception) {
                return ""
            }

            return String(buf)
        }

        fun getRuntimeApkFile(
            project: Project,
            buildType: PluginBuildType,
            checkExist: Boolean
        ): File {
            val packagePlugin = project.extensions.findByName("packagePlugin")
            val extension = packagePlugin as PackagePluginExtension

            val splitList = buildType.runtimeApkConfig.second.split(":")
            val runtimeFileParent =
                splitList[splitList.lastIndex].replace("assemble", "").toLowerCase()
            val runtimeApkName: String = buildType.runtimeApkConfig.first
            val runtimeFile = File(
                "${project.rootDir}" +
                        "/${extension.runtimeApkProjectPath}/build/outputs/apk/$runtimeFileParent/$runtimeApkName"
            )
            if (checkExist && !runtimeFile.exists()) {
                throw IllegalArgumentException(runtimeFile.absolutePath + " , runtime file not exist...")
            }
            project.logger.info("runtimeFile = $runtimeFile")
            return runtimeFile
        }

        fun getLoaderApkFile(
            project: Project,
            buildType: PluginBuildType,
            checkExist: Boolean
        ): File {
            val packagePlugin = project.extensions.findByName("packagePlugin")
            val extension = packagePlugin as PackagePluginExtension

            val loaderApkName: String = buildType.loaderApkConfig.first
            val splitList = buildType.loaderApkConfig.second.split(":")
            val loaderFileParent =
                splitList[splitList.lastIndex].replace("assemble", "").toLowerCase()
            val loaderFile = File(
                "${project.rootDir}" +
                        "/${extension.loaderApkProjectPath}/build/outputs/apk/$loaderFileParent/$loaderApkName"
            )
            if (checkExist && !loaderFile.exists()) {
                throw IllegalArgumentException(loaderFile.absolutePath + " , loader file not exist...")
            }
            project.logger.info("loaderFile = $loaderFile")
            return loaderFile

        }

        fun getPluginFile(
            project: Project,
            pluginConfig: PluginApkConfig,
            checkExist: Boolean
        ): File {
            val pluginFile = File(project.rootDir, pluginConfig.apkPath)
            if (checkExist && !pluginFile.exists()) {
                throw IllegalArgumentException(pluginFile.absolutePath + " , plugin file not exist...")
            }
            project.logger.info("pluginFile = $pluginFile")
            return pluginFile
        }
    }
}