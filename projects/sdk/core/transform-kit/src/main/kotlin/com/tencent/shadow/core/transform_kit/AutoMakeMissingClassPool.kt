package com.tencent.shadow.core.transform_kit

import javassist.ClassPool
import javassist.CtClass

class AutoMakeMissingClassPool(useDefaultPath: Boolean) : ClassPool(useDefaultPath) {

    companion object {
        fun isFromFixTypes2Called(newThrowable: Throwable): Boolean {
            for (stackTraceElement in newThrowable.stackTrace) {
                if (stackTraceElement.methodName == "fixTypes2") {
                    return true
                }
            }
            return false
        }
    }

    override fun get0(classname: String?, useCache: Boolean): CtClass? {
        var get0 = super.get0(classname, useCache)

        // 来自javassist.bytecode.stackmap.TypeData.TypeVar.fixTypes2的调用时，
        // 如果类不存在，就构造一个。
        // fixTypes2是重建StackMap的步骤，参考：https://stackoverflow.com/a/37310409/11616914
        // 我们的Transform不会去修改找不到的类型相关的代码，
        // 而fixTypes2处理的逻辑是在确定泛型的下界，
        // 由于我们没改任何跟找不到类型相关的逻辑，所以未知类型的父类重定义为Object，应该没有危险。
        //
        // 这里必须判断是来自的fixTypes2的调用，而不是所有调用都构造类型，是因为存在像
        // javassist.compiler.MemberResolver.lookupClass0
        // 依赖NotFoundException的逻辑存在。
        if (get0 == null && isFromFixTypes2Called(Throwable())) {
            get0 = makeClass(classname)
            if (useCache) cacheCtClass(get0.getName(), get0, false)
        }

        return get0
    }
}