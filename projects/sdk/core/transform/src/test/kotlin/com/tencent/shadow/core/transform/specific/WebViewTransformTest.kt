/*
 * Tencent is pleased to support the open source community by making Tencent Shadow available.
 * Copyright (C) 2019 THL A29 Limited, a Tencent company.  All rights reserved.
 *
 * Licensed under the BSD 3-Clause License (the "License"); you may not use
 * this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 *     https://opensource.org/licenses/BSD-3-Clause
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

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
    val shadowWebViewClazz = sLoader["com.tencent.shadow.core.runtime.ShadowWebView"]

    @Test
    fun testWebViewTransform() {
        val allInputClass = setOf(sLoader["test.TestWebView"], sLoader["com.tencent.shadow.core.runtime.ShadowWebView"])

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
                    "com.tencent.shadow.core.runtime.ShadowWebView",
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