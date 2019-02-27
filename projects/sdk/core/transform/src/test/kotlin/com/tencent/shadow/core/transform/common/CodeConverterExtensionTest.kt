package com.tencent.shadow.core.transform.common

import org.junit.Assert.assertEquals
import org.junit.Test


class CodeConverterExtensionTest : ShadowTransformTest() {

    @Test
    fun redirectMethodCallToStaticMethodCall() {
        val targetClass = sLoader["test.MethodRedirectToStatic"]
        val staticClass = sLoader["test.MethodRedirectToStatic2"]

        val targetMethod = targetClass.getDeclaredMethod("add")
        val staticMethod = staticClass.getDeclaredMethod("add2")
        val conv = CodeConverterExtension()

        conv.redirectMethodCallToStaticMethodCall(targetMethod, staticMethod)
        targetClass.instrument(conv)
        targetClass.writeFile(WRITE_FILE_DIR)

        val obj = make(targetClass.name)
        assertEquals(30, invoke(obj, "test"))
    }
}