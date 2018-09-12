package com.tencent.shadow.lint.issues

import com.android.tools.lint.detector.api.Issue

/**
 * 统一构造方法
 */
interface ShadowCodeIssue {
    val mISSUE: Issue
}