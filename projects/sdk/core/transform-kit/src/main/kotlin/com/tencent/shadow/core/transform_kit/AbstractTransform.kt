package com.tencent.shadow.core.transform_kit

import com.android.build.api.transform.TransformInvocation
import org.gradle.api.Project

abstract class AbstractTransform(
        project: Project,
        classPoolBuilder: ClassPoolBuilder
) : JavassistTransform(project, classPoolBuilder) {

    protected abstract val mTransformManager: AbstractTransformManager

    override fun beforeTransform(invocation: TransformInvocation) {
        super.beforeTransform(invocation)
        ReplaceClassName.resetErrorCount()
    }

    override fun onTransform() {
        mTransformManager.setupAll()
        mTransformManager.fireAll()
    }

    override fun afterTransform(invocation: TransformInvocation) {
        super.afterTransform(invocation)

        val errorCount = ReplaceClassName.getErrorCount()
        if (errorCount > 0) {
            throw IllegalStateException("存在${errorCount}处方法未实现的问题，需要Shadow补充实现。详见前面输出的Log")
        }
    }

}