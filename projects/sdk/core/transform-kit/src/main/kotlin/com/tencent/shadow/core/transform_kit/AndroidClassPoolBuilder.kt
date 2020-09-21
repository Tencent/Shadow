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

package com.tencent.shadow.core.transform_kit

import javassist.ClassPool
import javassist.LoaderClassPath
import org.gradle.api.Project
import org.gradle.internal.classloader.VisitableURLClassLoader
import java.io.File

class AndroidClassPoolBuilder(
        project: Project,
        val contextClassLoader: ClassLoader,
        val androidJar: File
) : ClassPoolBuilder {
    private val logger = project.logger

    override fun build(): ClassPool {
        //这里使用useDefaultPath:false是因为这里取到的contextClassLoader不包含classpath指定进来的runtime
        //所以在外部先获取一个包含了runtime的contextClassLoader传进来
        val classPool = AutoMakeMissingClassPool(false)

        classPool.appendClassPath(LoaderClassPath(contextClassLoader))
        if (logger.isInfoEnabled && contextClassLoader is VisitableURLClassLoader) {
            val sb = StringBuilder()
            sb.appendln()
            for (urL in contextClassLoader.urLs) {
                sb.appendln(urL)
            }
            logger.info("AndroidClassPoolBuilder appendClassPath contextClassLoader URLs:$sb")
        }

        classPool.appendClassPath(androidJar.absolutePath)
        if (logger.isInfoEnabled) {
            logger.info("AndroidClassPoolBuilder appendClassPath androidJar:${androidJar.absolutePath}")
        }

        return classPool
    }
}