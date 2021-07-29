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
import javassist.NotFoundException
import org.junit.Assert
import org.junit.Test

/**
 * ./gradlew -p projects/sdk/core :transform:test --tests com.tencent.shadow.core.transform.specific.FragmentSupportTransformTest
 */
class FragmentSupportTransformTest : AbstractTransformTest() {

    companion object {
        const val ShadowFragmentSupportClassName = "com.tencent.shadow.core.runtime.ShadowFragmentSupport"
        const val ShadowActivitySig = "Lcom/tencent/shadow/core/runtime/ShadowActivity;"
        const val TestFragmentSig = "Ltest/fragment/TestFragment;"
        const val FragmentSig = "Landroid/app/Fragment;"
        const val IntentSig = "Landroid/content/Intent;"
        const val BundleSig = "Landroid/os/Bundle;"
    }

    val shadowFragmentSupportClazz = sLoader[ShadowFragmentSupportClassName]
    val fragmentGetActivity = shadowFragmentSupportClazz.getMethod("fragmentGetActivity", "($FragmentSig)$ShadowActivitySig")
    val fragmentStartActivity1 = shadowFragmentSupportClazz.getMethod("fragmentStartActivity", "($FragmentSig$IntentSig)V")
    val fragmentStartActivity2 = shadowFragmentSupportClazz.getMethod("fragmentStartActivity", "($FragmentSig$IntentSig$BundleSig)V")
    val fragmentStartActivityForResult1 = shadowFragmentSupportClazz.getMethod("fragmentStartActivityForResult", "($FragmentSig${IntentSig}I)V")
    val fragmentStartActivityForResult2 = shadowFragmentSupportClazz.getMethod("fragmentStartActivityForResult", "($FragmentSig${IntentSig}I$BundleSig)V")

    private fun transform(clazz: CtClass) {
        val transform = FragmentSupportTransform()
        transform.mClassPool = sLoader

        val allInputClass = setOf(
                clazz
        )
        transform.setup(allInputClass)

        transform.list.forEach { step ->
            step.filter(allInputClass).forEach {
                step.transform(it)
                it.writeFile(WRITE_FILE_DIR)
            }
        }
    }


    @Test
    fun fragmentGetActivity() {
        val name = "test.fragment.UseGetActivityFragment"
        transform(sLoader[name])

        val transformedClass = dLoader.get(name)
        try {
            transformedClass.getMethod("test", "($TestFragmentSig)$ShadowActivitySig")
        } catch (e: Exception) {
            Assert.fail("找不到正确的test方法")
        }

        Assert.assertTrue("${fragmentGetActivity}调用应该可以找到",
                matchMethodCallInClass(fragmentGetActivity, transformedClass)
        )
    }

    @Test
    fun fragmentStartActivity() {
        val name = "test.fragment.UseStartActivityFragment"
        transform(sLoader[name])

        val transformedClass = dLoader.get(name)
        try {
            transformedClass.getMethod("test", "($TestFragmentSig)$ShadowActivitySig")
        } catch (e: Exception) {
            Assert.fail("找不到正确的test方法")
        }

        Assert.assertTrue("${fragmentStartActivity1}调用应该可以找到",
                matchMethodCallInClass(fragmentStartActivity1, transformedClass)
        )
        Assert.assertTrue("${fragmentStartActivity2}调用应该可以找到",
                matchMethodCallInClass(fragmentStartActivity2, transformedClass)
        )
    }

    @Test
    fun fragmentStartActivityForResult() {
        val name = "test.fragment.UseStartActivityForResultFragment"
        transform(sLoader[name])

        val transformedClass = dLoader.get(name)
        try {
            transformedClass.getMethod("test", "($TestFragmentSig)$ShadowActivitySig")
        } catch (e: Exception) {
            Assert.fail("找不到正确的test方法")
        }

        Assert.assertTrue("${fragmentStartActivityForResult1}调用应该可以找到",
            matchMethodCallInClass(fragmentStartActivityForResult1, transformedClass)
        )
        Assert.assertTrue("${fragmentStartActivityForResult2}调用应该可以找到",
            matchMethodCallInClass(fragmentStartActivityForResult2, transformedClass)
        )
    }

    @Test
    fun attachContext() {
        val name = "test.fragment.TestFragment"
        val transform = FragmentSupportTransform()
        transform.mClassPool = sLoader

        val allInputClass = setOf(
                sLoader[name]
        )
        transform.setup(allInputClass)

        transform.list.forEach { step ->
            step.filter(allInputClass).forEach {
                step.transform(it)
                it.writeFile(WRITE_FILE_DIR)
            }
        }

        val transformedClass = dLoader.get(name)
        try {
            transformedClass.getDeclaredMethod("onAttach")
        } catch (e: NotFoundException) {
            Assert.fail("找不到onAttach")
        }

        try {
            transformedClass.getDeclaredMethod("onAttachShadowContext")
        } catch (e: NotFoundException) {
            Assert.fail("找不到onAttachShadowContext")
        }

        try {
            transformedClass.getDeclaredMethod("superOnAttach")
        } catch (e: NotFoundException) {
            Assert.fail("找不到superOnAttach")
        }
    }
}
