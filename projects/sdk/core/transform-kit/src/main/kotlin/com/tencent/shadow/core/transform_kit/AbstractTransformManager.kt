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
import javassist.CtClass

abstract class AbstractTransformManager(private val classPool: ClassPool) {
    abstract val mTransformList: List<SpecificTransform>

    fun setupAll(allInputCtClass: Set<CtClass>) {
        mTransformList.forEach {
            it.mClassPool = classPool
            it.setup(allInputCtClass)
        }
    }

    fun fireAll(allInputCtClass: Set<CtClass>) {
        mTransformList.flatMap { it.list }.forEach { transform ->
            transform.filter(allInputCtClass).forEach {
                transform.transform(it)
            }
        }
    }
}