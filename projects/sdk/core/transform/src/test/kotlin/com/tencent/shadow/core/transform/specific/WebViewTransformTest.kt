package com.tencent.shadow.core.transform.specific

import com.tencent.shadow.core.transform_kit.AbstractTransformTest
import javassist.CtClass
import org.junit.Assert
import org.junit.Test

/**
 * ./gradlew -p projects/sdk/core :transform:test --tests com.tencent.shadow.core.transform.specific.WebViewTransformTest
 */
class WebViewTransformTest : AbstractTransformTest() {

    val webViewClazz = sLoader["android.webkit.WebView"]
    val shadowWebViewClazz = sLoader["com.tencent.shadow.runtime.ShadowWebView"]

    @Test
    fun testWebViewTransform() {
        val allInputClass = setOf(sLoader["test.TestWebView"], sLoader["com.tencent.shadow.runtime.ShadowWebView"])

        val webViewTransform = WebViewTransform()
        webViewTransform.mClassPool = sLoader
        webViewTransform.setup(allInputClass)

        allInputClass.forEach {
            beforeTransformCheck(it)
        }

        webViewTransform.list.forEach { transform ->
            transform.filter(allInputClass).forEach {
                transform.transform(it)
            }
        }

        allInputClass.forEach {
            afterTransformCheck(it)
        }
    }

    private fun beforeTransformCheck(clazz: CtClass){
        if (clazz.classFile.name == "test.TestWebView") {
            Assert.assertTrue(webViewClazz.name+" 构造器方法调用应该可以找到",
                    matchConstructorCallInClass(webViewClazz.name,clazz)
            )
        }
    }

    private fun afterTransformCheck(clazz: CtClass){
        if (clazz.classFile.name == "test.TestWebView") {
            Assert.assertEquals(
                    "WebView父类应该都变为了ShadowWebView",
                    "com.tencent.shadow.runtime.ShadowWebView",
                    clazz.classFile.superclass
            )

            Assert.assertTrue(webViewClazz.name+"构造器方法调用应该没有了",
                    !matchConstructorCallInClass(webViewClazz.name,clazz)
            )

            Assert.assertTrue(shadowWebViewClazz.name+" 构造器方法调用应该可以找到",
                    matchConstructorCallInClass(shadowWebViewClazz.name,clazz)
            )
        }
    }
}