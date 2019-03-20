package com.tencent.shadow.core.transform.specific

class ApplicationTransform : SimpleRenameTransform(
        mapOf(
                "android.app.Application"
                        to "com.tencent.shadow.runtime.ShadowApplication"
                ,
                "android.app.Application\$ActivityLifecycleCallbacks"
                        to "com.tencent.shadow.runtime.ShadowActivityLifecycleCallbacks"
        )
)
