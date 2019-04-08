package com.tencent.shadow.core.transform

import com.android.build.api.transform.TransformInvocation
import com.tencent.shadow.core.transform.common.ClassPoolBuilder
import com.tencent.shadow.core.transform.common.JavassistTransform
import com.tencent.shadow.core.transform.common.ReplaceClassName
import org.gradle.api.Project

class ShadowTransform(project: Project, classPoolBuilder: ClassPoolBuilder, val useHostContext: () -> Array<String>) : JavassistTransform(project, classPoolBuilder) {
    override fun beforeTransform(invocation: TransformInvocation) {
        super.beforeTransform(invocation)
        ReplaceClassName.resetErrorCount()
    }

    override fun onTransform() {
        val transformManager = TransformManager(mCtClassInputMap, classPool, useHostContext)
        transformManager.fireAll()
    }

    override fun afterTransform(invocation: TransformInvocation) {
        super.afterTransform(invocation)

        val errorCount = ReplaceClassName.getErrorCount()
        if (errorCount > 0) {
            throw IllegalStateException("存在${errorCount}处方法未实现的问题，需要Shadow补充实现。详见前面输出的Log")
        }
    }

    override fun getName(): String = "ShadowTransform"
}