package com.tencent.shadow.core.transform.specific

class AppComponentFactoryTransform : SimpleRenameTransform(
    mapOf(
        "android.app.AppComponentFactory"
                to "com.tencent.shadow.core.runtime.ShadowAppComponentFactory"
    )
)
