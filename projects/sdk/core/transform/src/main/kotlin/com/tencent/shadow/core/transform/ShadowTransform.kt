package com.tencent.shadow.core.transform

import com.tencent.shadow.core.transform.common.ClassPoolBuilder
import com.tencent.shadow.core.transform.common.JavassistTransform
import org.gradle.api.Project

class ShadowTransform(project: Project, classPoolBuilder: ClassPoolBuilder, val useHostContext: () -> Array<String>) : JavassistTransform(project, classPoolBuilder) {
    override fun onTransform() {
        val transformManager = TransformManager(mCtClassInputMap, classPool, useHostContext)
        transformManager.fireAll()
    }

    override fun getName(): String = "ShadowTransform"
}