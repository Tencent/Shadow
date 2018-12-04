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