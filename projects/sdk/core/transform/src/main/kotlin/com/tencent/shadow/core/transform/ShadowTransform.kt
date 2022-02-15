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

package com.tencent.shadow.core.transform

import com.android.build.api.transform.TransformInvocation
import com.android.build.api.variant.VariantInfo
import com.tencent.shadow.core.transform_kit.AbstractTransform
import com.tencent.shadow.core.transform_kit.AbstractTransformManager
import com.tencent.shadow.core.transform_kit.ClassPoolBuilder
import org.gradle.api.Project

class ShadowTransform(
    project: Project,
    classPoolBuilder: ClassPoolBuilder,
    private val useHostContext: () -> Array<String>
) : AbstractTransform(project, classPoolBuilder) {
    companion object {
        const val SelfClassNamePlaceholder =
            "com.tencent.shadow.core.transform.SelfClassNamePlaceholder"
        const val DimensionName = "Shadow"
        const val NoShadowTransformFlavorName = "normal"
        const val ApplyShadowTransformFlavorName = "plugin"
    }

    lateinit var _mTransformManager: TransformManager

    override val mTransformManager: AbstractTransformManager
        get() = _mTransformManager

    override fun beforeTransform(invocation: TransformInvocation) {
        super.beforeTransform(invocation)
        _mTransformManager = TransformManager(mCtClassInputMap, classPool, useHostContext)
        classPool.makeInterface(SelfClassNamePlaceholder)
    }

    override fun isCacheable(): Boolean {
        return true
    }

    override fun getName(): String = "ShadowTransform"

    override fun applyToVariant(variant: VariantInfo): Boolean {
        return if (variant.isTest) false
        else variant.flavorNames.contains(ApplyShadowTransformFlavorName)
    }
}