package com.tencent.shadow.lint

import com.android.tools.lint.client.api.IssueRegistry
import com.android.tools.lint.detector.api.Issue
import com.tencent.shadow.lint.issues.PrintStackTraceIssue

class ShadowCodeIssueRegistry : IssueRegistry() {

    override val issues: List<Issue>
        get() = listOf(
                PrintStackTraceIssue.mISSUE
        )

    override val api: Int = com.android.tools.lint.detector.api.CURRENT_API
}