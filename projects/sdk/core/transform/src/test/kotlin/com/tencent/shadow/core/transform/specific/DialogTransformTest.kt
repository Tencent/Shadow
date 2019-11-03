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
        const val SHADOW_DIALOG_CLASS_NAME = "com.tencent.shadow.core.runtime.ShadowDialog"
        const val SHADOW_ACT_SIG = "Lcom/tencent/shadow/core/runtime/ShadowActivity;"
    }

    val shadowDialogClazz = sLoader[SHADOW_DIALOG_CLASS_NAME]
    val getOwnerPluginActivity = shadowDialogClazz.getMethod("getOwnerPluginActivity", "()$SHADOW_ACT_SIG")
    val setOwnerPluginActivity = shadowDialogClazz.getMethod("setOwnerPluginActivity", "($SHADOW_ACT_SIG)V")

    private fun transform(clazz: CtClass) {
        val dialogTransform = DialogTransform()
        dialogTransform.mClassPool = sLoader

        val allInputClass = setOf(
                clazz,
                sLoader["test.dialog.SubDialog"],
                sLoader["test.dialog.SubSubDialog"]
        )
        dialogTransform.setup(allInputClass)

        dialogTransform.list.forEach { transform ->
            transform.filter(allInputClass).forEach {
                transform.transform(it)
                it.writeFile(WRITE_FILE_DIR)
            }
        }
    }

    private fun assertResult(sig: String, clazz: CtClass) {
        try {
            clazz.getMethod("test", "($sig)$sig")
        } catch (e: Exception) {
            Assert.fail("找不到正确的test方法")
        }

        Assert.assertTrue("${getOwnerPluginActivity}调用应该可以找到",
                matchMethodCallInClass(getOwnerPluginActivity, clazz)
        )

        Assert.assertTrue("${setOwnerPluginActivity}调用应该可以找到",
                matchMethodCallInClass(setOwnerPluginActivity, clazz)
        )
    }

    @Test
    fun subDialog() {
        val name = "test.dialog.SubDialog"
        transform(sLoader[name])
        Assert.assertEquals(SHADOW_DIALOG_CLASS_NAME, dLoader.get(name).superclass.name)
    }

    @Test
    fun useDialog() {
        val name = "test.dialog.UseDialog"
        transform(sLoader[name])
        val sig = "Lcom/tencent/shadow/core/runtime/ShadowDialog;"
        assertResult(sig, dLoader.get(name))
    }

    @Test
    fun useSubDialog() {
        val name = "test.dialog.UseSubDialog"
        transform(sLoader[name])
        val sig = "Ltest/dialog/SubDialog;"
        assertResult(sig, dLoader.get(name))
    }

    @Test
    fun useSubSubDialog() {
        val name = "test.dialog.UseSubSubDialog"
        transform(sLoader[name])
        val sig = "Ltest/dialog/SubSubDialog;"
        assertResult(sig, dLoader.get(name))
    }
}
