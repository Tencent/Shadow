package com.tencent.shadow.core.transform.specific

import org.junit.Test

class ApplicationTransformTest : SimpleRenameTransformTest(ApplicationTransform(), arrayOf("test.TestApplication"),
        "get", "com.tencent.shadow.core.runtime.ShadowApplication",
        mapOf("()Landroid/app/Application;" to "()Lcom/tencent/shadow/core/runtime/ShadowApplication;")) {

    @Test
    fun testApplicationTransform() {
        doTest()
    }
}