package com.tencent.shadow.core.transform.specific

import org.junit.Test

class ServiceTransformTest : SimpleRenameTransformTest(ServiceTransform(), arrayOf("test.TestService"),
        "getService", "com.tencent.shadow.runtime.ShadowService",
        mapOf("()Landroid/app/Service;" to "()Lcom/tencent/shadow/runtime/ShadowService;")) {

    @Test
    fun testServiceTransform() {
        doTest()
    }
}