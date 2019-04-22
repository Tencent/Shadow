package com.tencent.shadow.core.transform

import com.android.build.api.transform.TransformInvocation
import com.tencent.shadow.core.transform_kit.AbstractTransform
import com.tencent.shadow.core.transform_kit.AbstractTransformManager
import com.tencent.shadow.core.transform_kit.ClassPoolBuilder
import org.gradle.api.Project

class ShadowTransform(
        project: Project,
        classPoolBuilder: ClassPoolBuilder,
        private val useHostContext: () -> Array<String>
) : AbstractTransform(project, classPoolBuilder) {

    lateinit var _mTransformManager: TransformManager

    override val mTransformManager: AbstractTransformManager
        get() = _mTransformManager

    override fun beforeTransform(invocation: TransformInvocation) {
        super.beforeTransform(invocation)
        _mTransformManager = TransformManager(mCtClassInputMap, classPool, useHostContext)
    }

    override fun getName(): String = "ShadowTransform"
}