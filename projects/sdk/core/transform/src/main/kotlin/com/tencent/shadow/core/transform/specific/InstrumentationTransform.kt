package com.tencent.shadow.core.transform.specific

class InstrumentationTransform : SimpleRenameTransform(
        mapOf(
                "android.app.Instrumentation"
                        to "com.tencent.shadow.core.runtime.ShadowInstrumentation"
        )
)
