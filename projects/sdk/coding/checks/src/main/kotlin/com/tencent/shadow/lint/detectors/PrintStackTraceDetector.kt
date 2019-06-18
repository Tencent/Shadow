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

package com.tencent.shadow.lint.detectors

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.JavaContext
import com.intellij.psi.impl.source.PsiClassReferenceType
import com.tencent.shadow.lint.issues.PrintStackTraceIssue
import org.jetbrains.uast.UCallExpression

class PrintStackTraceDetector : ShadowCodeIssueDetector() {
    override fun getApplicableUastTypes() = listOf(UCallExpression::class.java)

    override fun createUastHandler(context: JavaContext) =
            object : UElementHandler() {
                override fun visitCallExpression(node: UCallExpression) {
                    if (node.methodName == "printStackTrace") {
                        val receiverType = node.receiverType
                        if (receiverType is PsiClassReferenceType) {
                            val clazz = receiverType.resolve()
                            if (clazz != null)
                                if (context.evaluator.inheritsFrom(
                                                clazz,
                                                "java.lang.Throwable",
                                                true
                                        )) {
                                    context.report(
                                            PrintStackTraceIssue.mISSUE,
                                            node,
                                            context.getLocation(node),
                                            "本项目禁止使用该方法"
                                    )
                                }
                        }
                    }
                }
            }
}