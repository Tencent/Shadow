package com.tencent.shadow.loader.delegates

interface DI {
    fun inject(delegate: ShadowDelegate, partKey: String)
}