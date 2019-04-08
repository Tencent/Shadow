package com.tencent.shadow.core.transform.specific

import org.junit.Test

class ApplicationTransformTest : SimpleRenameTransformTest(ApplicationTransform(), arrayOf("test.TestApplication"),
        "get", "com.tencent.shadow.runtime.ShadowApplication",
        mapOf("()Landroid/app/Application;" to "()Lcom/tencent/shadow/runtime/ShadowApplication;")) {

    @Test
    fun testApplicationTransform() {
        doTest()
    }
}