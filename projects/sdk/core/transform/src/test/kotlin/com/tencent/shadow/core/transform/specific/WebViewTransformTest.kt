package com.tencent.shadow.core.transform.specific

import com.tencent.shadow.core.transform.common.ShadowTransformTest
import org.junit.Assert
import org.junit.Test

class WebViewTransformTest : ShadowTransformTest() {


    @Test
    fun testWebViewTransform() {
        //TODO 需要添加由代码动态new WebView的单元测试

        val allInputClass = setOf(sLoader["test.TestWebView"], sLoader["com.tencent.shadow.runtime.ShadowWebView"])

        val webViewTransform = WebViewTransform()
        webViewTransform.mClassPool = sLoader
        webViewTransform.setup(allInputClass)

        webViewTransform.list.forEach { transform ->
            transform.filter(allInputClass).forEach {
                transform.transform(it)
            }
        }

        allInputClass.forEach {
            if (it.classFile.name == "test.TestWebView") {
                Assert.assertEquals(
                        "WebView父类应该都变为了ShadowWebView",
                        "com.tencent.shadow.runtime.ShadowWebView",
                        it.classFile.superclass
                )
            }
        }
    }
}