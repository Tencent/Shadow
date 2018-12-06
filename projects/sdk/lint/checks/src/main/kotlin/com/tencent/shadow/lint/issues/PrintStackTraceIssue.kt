package com.tencent.shadow.lint.issues

import com.android.tools.lint.detector.api.*
import com.tencent.shadow.lint.detectors.PrintStackTraceDetector

object PrintStackTraceIssue : ShadowCodeIssue {
    override val mISSUE = Issue.create(
            javaClass.simpleName,
            "禁止调用printStackTrace()",
            "禁止调用java.lang.Throwable.printStackTrace()方法。",
            Category.USABILITY,
            6,
            Severity.ERROR,
            Implementation(PrintStackTraceDetector::class.java, Scope.JAVA_FILE_SCOPE)
    )
}