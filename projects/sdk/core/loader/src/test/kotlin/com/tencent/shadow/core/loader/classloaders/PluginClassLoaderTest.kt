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

package com.tencent.shadow.core.loader.classloaders

import org.junit.Assert
import org.junit.Test

/**
 * @author zby
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2019-09-06
 * @description test String.inPackage(packageNames: Array<String>): Boolean
 * @usage click icon on the left of testString_inPackage()
 */
class PluginClassLoaderTest {

    @Test
    fun case11() {
        val packageNames = arrayOf("a.b.c")
        val className = "a.b.c.D"
        Assert.assertTrue(className.inPackage(packageNames))
    }

    @Test
    fun case12() {
        val packageNames = arrayOf("a.b.c")
        val className = "a.b.D"
        Assert.assertFalse(className.inPackage(packageNames))
    }

    @Test
    fun case13() {
        val packageNames = arrayOf("a.b.c")
        val className = "a.b.c"
        Assert.assertFalse(className.inPackage(packageNames))
    }

    @Test
    fun case14() {
        val packageNames = arrayOf("a.b.c")
        val className = "a.b.c.d.E"
        Assert.assertFalse(className.inPackage(packageNames))
    }

    @Test
    fun case15() {
        val packageNames = arrayOf("xxxx", "a.b.c")
        val className = "a.b.c.D"
        Assert.assertTrue(className.inPackage(packageNames))
    }

    @Test
    fun case16() {
        val packageNames = arrayOf("a.b.c", "xxxx")
        val className = "a.b.c.D"
        Assert.assertTrue(className.inPackage(packageNames))
    }

    @Test
    fun case21() {
        val packageNames = arrayOf("")
        val className = "A"
        Assert.assertFalse(className.inPackage(packageNames))
    }

    @Test
    fun case22() {
        val packageNames = arrayOf("")
        val className = "a.B"
        Assert.assertFalse(className.inPackage(packageNames))
    }

    @Test
    fun case31() {
        val packageNames = arrayOf("b.c")
        val className = "a.b.c.D"
        Assert.assertFalse(className.inPackage(packageNames))
    }

    @Test
    fun case41() {
        val packageNames = arrayOf("a.b.c.*")
        val className = "a.b.c.D"
        Assert.assertFalse(className.inPackage(packageNames))
    }

    @Test
    fun case42() {
        val packageNames = arrayOf("a.b.c.*")
        val className = "a.b.c"
        Assert.assertFalse(className.inPackage(packageNames))
    }

    @Test
    fun case43() {
        val packageNames = arrayOf("a.b.c.*")
        val className = "a.b.c.d.E"
        Assert.assertTrue(className.inPackage(packageNames))
    }

    @Test
    fun case44() {
        val packageNames = arrayOf("a.b.c.*")
        val className = "a.b.c.d.e.F"
        Assert.assertFalse(className.inPackage(packageNames))
    }

    @Test
    fun case51() {
        val packageNames = arrayOf(".*")
        val className = "a.b.c.d.E"
        Assert.assertFalse(className.inPackage(packageNames))
    }

    @Test
    fun case52() {
        val packageNames = arrayOf(".*")
        val className = "A"
        Assert.assertFalse(className.inPackage(packageNames))
    }

    @Test
    fun case61() {
        val packageNames = arrayOf("a.b.c.**")
        val className = "a.b.c.D"
        Assert.assertFalse(className.inPackage(packageNames))
    }

    @Test
    fun case62() {
        val packageNames = arrayOf("a.b.c.**")
        val className = "a.b.c.d.E"
        Assert.assertTrue(className.inPackage(packageNames))
    }

    @Test
    fun case63() {
        val packageNames = arrayOf("a.b.c.**")
        val className = "a.b.c.d.e.F"
        Assert.assertTrue(className.inPackage(packageNames))
    }

    @Test
    fun case64() {
        val packageNames = arrayOf("a.b.c.**")
        val className = "a.b.C"
        Assert.assertFalse(className.inPackage(packageNames))
    }

    @Test
    fun case65() {
        val packageNames = arrayOf(".**")
        val className = "a.b.c.D"
        Assert.assertFalse(className.inPackage(packageNames))
    }

    @Test
    fun case66() {
        val packageNames = arrayOf(".**")
        val className = "a.B"
        Assert.assertFalse(className.inPackage(packageNames))
    }

    @Test
    fun case67() {
        val packageNames = arrayOf(".**")
        val className = "A"
        Assert.assertFalse(className.inPackage(packageNames))
    }

    @Test
    fun case68() {
        val packageNames = arrayOf("a.b.c.d.**", "a.b.c2.d2.**")
        val className = "a.b.c2.d2.e2.F"
        Assert.assertTrue(className.inPackage(packageNames))
    }

    @Test
    fun case71() {
        val packageNames = arrayOf("com.tencent.**")
        val className = "com.tencentshadow.MyClass"
        Assert.assertFalse(className.inPackage(packageNames))
    }

    @Test
    fun case72() {
        val packageNames = arrayOf("com.tencent.**")
        val className = "com.tencentshadow.next.MyClass"
        Assert.assertFalse(className.inPackage(packageNames))
    }

    @Test
    fun case73() {
        val packageNames = arrayOf("com.tencent**")
        val className = "com.tencentshadow.MyClass"
        Assert.assertFalse(className.inPackage(packageNames))
    }

    @Test
    fun case74() {
        val packageNames = arrayOf("com.tencent**")
        val className = "com.tencentshadow.next.MyClass"
        Assert.assertFalse(className.inPackage(packageNames))
    }

    @Test
    fun case75() {
        //允许retrofit2包中的类和retrofit2包中所有子包中的类
        val packageNames = arrayOf("retrofit2", "retrofit2.**")
        val className1 = "retrofit2.Retrofit\$Builder"
        val className2 = "retrofit2.a.Retrofit\$Builder"
        val className3 = "retrofit2.a.b.Retrofit\$Builder"
        Assert.assertTrue(className1.inPackage(packageNames))
        Assert.assertTrue(className2.inPackage(packageNames))
        Assert.assertTrue(className3.inPackage(packageNames))
    }
}


