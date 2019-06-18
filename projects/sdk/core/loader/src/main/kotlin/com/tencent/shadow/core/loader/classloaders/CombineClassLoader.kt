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

class CombineClassLoader(private val classLoaders: Array<out ClassLoader>, parent: ClassLoader) : ClassLoader(parent) {

    @Throws(ClassNotFoundException::class)
    override fun loadClass(name: String, resolve: Boolean): Class<*> {
        var c: Class<*>? = findLoadedClass(name)
        val classNotFoundException = ClassNotFoundException(name)
        if (c == null) {
            try {
                c = super.loadClass(name, resolve)
            } catch (e: ClassNotFoundException) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    classNotFoundException.addSuppressed(e)
                }
            }

            if (c == null) {
                for (classLoader in classLoaders) {
                    try {
                        c = classLoader.loadClass(name)!!
                        break
                    } catch (e: ClassNotFoundException) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                            classNotFoundException.addSuppressed(e)
                        }
                    }
                }
                if (c == null) {
                    throw classNotFoundException
                }
            }
        }
        return c
    }
}