package com.tencent.shadow.core.transform_kit

import org.junit.Assert
import org.junit.Test

class FilterRefClassesTest : AbstractTransformTest() {


    /**
     * 测试Transform时如果想修改一个对象方法调用时，
     * 如果这个方法来自于其父类，是否可以通过过来父类来找出需要修改的类，
     * 以便优化Transform速度。
     * 结论是不行，直接调用父类方法时并不需要引用父类。
     */
    @Test
    fun testCallSuperMethodWithoutSuperClass() {
        val targetClass = sLoader["test.override.UseFooAsSuperSuper"]

        val allAppClass = setOf(targetClass)
        val filterRefClasses =
            SpecificTransform.filterRefClasses(allAppClass, listOf("test.override.SuperSuper"))

        Assert.assertFalse(
            "直接调用父类方法时并不需要引用父类",
            filterRefClasses.contains(targetClass)
        )
    }
}