package com.tencent.shadow.core.transform.specific

class ServiceTransform : SimpleRenameTransform(
        mapOf(
                "android.app.Service"
                        to "com.tencent.shadow.runtime.ShadowService"
        )
)
