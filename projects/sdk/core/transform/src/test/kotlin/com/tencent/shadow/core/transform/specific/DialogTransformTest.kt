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
 * ./gradlew -p projects/sdk/core :transform:test --tests com.tencent.shadow.core.transform.specific.DialogTransformTest
 */
class DialogTransformTest : AbstractTransformTest() {

    companion object {
        const val DIALOG_CLASS_NAME = "android.app.Dialog"
        const val SHADOW_DIALOG_CLASS_NAME = "com.tencent.shadow.core.runtime.ShadowDialog"
        const val TEST_CLASS_FOO_NAME = "test.dialog.Foo"
        const val TEST_CLASS_BAR_NAME = "test.dialog.BarDialog"

        const val DIALOG_SIG = "Landroid/app/Dialog;"
        const val SHADOW_DIALOG_SIG = "Lcom/tencent/shadow/core/runtime/ShadowDialog;"

        const val SHADOW_ACT_SIG = "Lcom/tencent/shadow/core/runtime/ShadowActivity;"
    }

    val dialogClazz = sLoader[DIALOG_CLASS_NAME]
    val dialogGetOwnerActivity = dialogClazz.getMethod("getOwnerActivity", "()$SHADOW_ACT_SIG")
    val dialogSetOwnerActivity = dialogClazz.getMethod("setOwnerActivity", "($SHADOW_ACT_SIG)V")

    val shadowDialogClazz = sLoader[SHADOW_DIALOG_CLASS_NAME]
    val shadowDialogGetOwnerActivity = shadowDialogClazz.getMethod("getOwnerActivity", "()$SHADOW_ACT_SIG")
    val shadowDialogSetOwnerActivity = shadowDialogClazz.getMethod("setOwnerActivity", "($SHADOW_ACT_SIG)V")

    val getOwnerPluginActivity = shadowDialogClazz.getMethod("getOwnerPluginActivity", "()$SHADOW_ACT_SIG")
    val setOwnerPluginActivity = shadowDialogClazz.getMethod("setOwnerPluginActivity", "($SHADOW_ACT_SIG)V")

    val testFooClazz = sLoader[TEST_CLASS_FOO_NAME]
    val testbarClazz = sLoader[TEST_CLASS_BAR_NAME]

    @Test
    fun testDialogTransform() {
        val allInputClass = setOf(testFooClazz, testbarClazz)

        allInputClass.forEach {
            beforeTransformCheck(it)
        }

        val dialogTransform = DialogTransform()
        dialogTransform.mClassPool = sLoader
        dialogTransform.setup(allInputClass)

        dialogTransform.list.forEach { transform ->
            transform.filter(allInputClass).forEach {
                transform.transform(it)
                it.writeFile(WRITE_FILE_DIR)
            }
        }

        allInputClass.forEach {
            afterTransformCheck(dLoader.get(it.name))
        }
    }

    private fun beforeTransformCheck(clazz: CtClass) {
        if (clazz.classFile.name == TEST_CLASS_FOO_NAME) {
            try {
                clazz.getMethod("foo", "($DIALOG_SIG)$DIALOG_SIG")
            } catch (e: Exception) {
                Assert.fail("找不到正确的foo方法")
            }

            Assert.assertTrue("${dialogGetOwnerActivity}调用应该可以找到",
                    matchMethodCallInClass(dialogGetOwnerActivity, clazz)
            )

            Assert.assertTrue("${dialogSetOwnerActivity}调用应该可以找到",
                    matchMethodCallInClass(dialogSetOwnerActivity, clazz)
            )

        }

        if (clazz.classFile.name == TEST_CLASS_BAR_NAME) {
            Assert.assertEquals(
                    "父类不正确",
                    DIALOG_CLASS_NAME, clazz.classFile.superclass
            )

            Assert.assertTrue("${dialogGetOwnerActivity}调用应该可以找到",
                    matchMethodCallInClass(dialogGetOwnerActivity, clazz)
            )

            Assert.assertTrue("${dialogSetOwnerActivity}调用应该可以找到",
                    matchMethodCallInClass(dialogSetOwnerActivity, clazz)
            )
        }
    }

    private fun afterTransformCheck(clazz: CtClass) {
        if (clazz.name == TEST_CLASS_FOO_NAME) {
            //先检查原来的判断都应该失效了
            try {
                clazz.getMethod("foo", "($DIALOG_SIG)$DIALOG_SIG")
                Assert.fail("应该找不到原来的foo方法才对")
            } catch (ignored: Exception) {
            }

            Assert.assertFalse("不应该有调用：${dialogGetOwnerActivity}",
                    matchMethodCallInClass(dialogGetOwnerActivity, clazz)
            )

            Assert.assertFalse("不应该有调用：${dialogSetOwnerActivity}",
                    matchMethodCallInClass(dialogSetOwnerActivity, clazz)
            )

            Assert.assertFalse("不应该有调用：${shadowDialogGetOwnerActivity}",
                    matchMethodCallInClass(shadowDialogGetOwnerActivity, clazz)
            )

            Assert.assertFalse("不应该有调用：${shadowDialogSetOwnerActivity}",
                    matchMethodCallInClass(shadowDialogSetOwnerActivity, clazz)
            )

            //再检查新的变化是否都存在
            try {
                clazz.getMethod("foo", "($SHADOW_DIALOG_SIG)$SHADOW_DIALOG_SIG")
            } catch (e: Exception) {
                Assert.fail("找不到正确的foo方法")
            }

            Assert.assertTrue("${getOwnerPluginActivity}调用应该可以找到",
                    matchMethodCallInClass(getOwnerPluginActivity, clazz)
            )

            Assert.assertTrue("${setOwnerPluginActivity}调用应该可以找到",
                    matchMethodCallInClass(setOwnerPluginActivity, clazz)
            )
        }

        if (clazz.name == TEST_CLASS_BAR_NAME) {
            Assert.assertEquals(
                    "父类不正确",
                    SHADOW_DIALOG_CLASS_NAME, clazz.superclass.name
            )

            Assert.assertTrue("${getOwnerPluginActivity}调用应该可以找到",
                    matchMethodCallInClass(getOwnerPluginActivity, clazz)
            )

            Assert.assertTrue("${setOwnerPluginActivity}调用应该可以找到",
                    matchMethodCallInClass(setOwnerPluginActivity, clazz)
            )
        }
    }
}