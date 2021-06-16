package com.tencent.shadow.coding.code_generator

import org.junit.Test


internal class ActivityCodeGeneratorTest {
    @Test
    fun testLoadAndroidClass() {
        ActivityCodeGenerator.classPool.get("android.app.Activity")
    }
}