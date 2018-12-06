package com.tencent.shadow.lint.detectors

import com.android.tools.lint.detector.api.Detector

/**
 * 统一继承关系
 */
abstract class ShadowCodeIssueDetector : Detector(), Detector.UastScanner