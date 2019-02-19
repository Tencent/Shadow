package com.tencent.shadow.core.transform.specific

class ActivityTransform : SimpleRenameTransform(
        mapOf(
                "android.app.Activity"
                        to "com.tencent.shadow.runtime.ShadowActivity"
        )
)
