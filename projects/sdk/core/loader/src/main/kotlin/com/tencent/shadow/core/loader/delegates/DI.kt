package com.tencent.shadow.core.loader.delegates

interface DI {
    fun inject(delegate: ShadowDelegate, partKey: String)
}