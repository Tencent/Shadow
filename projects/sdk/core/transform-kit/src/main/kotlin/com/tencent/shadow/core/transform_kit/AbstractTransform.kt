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

import com.android.build.api.transform.TransformInvocation
import org.gradle.api.Project

abstract class AbstractTransform(
        project: Project,
        classPoolBuilder: ClassPoolBuilder
) : JavassistTransform(project, classPoolBuilder) {

    protected abstract val mTransformManager: AbstractTransformManager

    override fun beforeTransform(invocation: TransformInvocation) {
        super.beforeTransform(invocation)
        ReplaceClassName.resetErrorCount()
    }

    override fun onTransform() {
        mTransformManager.setupAll()
        mTransformManager.fireAll()
    }

    override fun afterTransform(invocation: TransformInvocation) {
        super.afterTransform(invocation)

        val errorCount = ReplaceClassName.getErrorCount()
        if (errorCount > 0) {
            throw IllegalStateException("存在${errorCount}处方法未实现的问题，需要Shadow补充实现。详见前面输出的Log")
        }
    }

}