package com.tencent.shadow.core.transform.specific

import com.tencent.shadow.core.transform.common.SpecificTransform
import com.tencent.shadow.core.transform.common.TransformStep
import javassist.CtClass

/**
 * 替换跨插件apk创建view相关的类
 */
class RemoteViewTransform : SpecificTransform() {

    companion object {
        const val RemoteLocalSdkPackageName = "com.tencent.shadow.remoteview.localsdk"
        val RemoteViewRenameMap = mapOf(
                "com.tencent.shadow.remoteview.localsdk.RemoteViewCreator"
                        to "com.tencent.shadow.runtime.remoteview.ShadowRemoteViewCreator",
                "com.tencent.shadow.remoteview.localsdk.RemoteViewCreatorFactory"
                        to "com.tencent.shadow.runtime.remoteview.ShadowRemoteViewCreatorFactory",
                "com.tencent.shadow.remoteview.localsdk.RemoteViewCreateCallback"
                        to "com.tencent.shadow.runtime.remoteview.ShadowRemoteViewCreateCallback",
                "com.tencent.shadow.remoteview.localsdk.RemoteViewCreateException"
                        to "com.tencent.shadow.runtime.remoteview.ShadowRemoteViewCreateException"
        )
    }

    override fun setup(allInputClass: Set<CtClass>) {
        newStep(object : TransformStep {
            override fun filter(allInputClass: Set<CtClass>) = allInputClass

            override fun transform(ctClass: CtClass) {
                // 除RemoteLocalSdk包外的所有类，都需要替换
                if (RemoteLocalSdkPackageName != ctClass.packageName) {
                    RemoteViewRenameMap.forEach {
                        ctClass.replaceClassName(it.key, it.value)
                    }
                }
            }

        })
    }
}