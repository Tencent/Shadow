package com.tencent.shadow.core.loader

interface Reporter {
    fun reportException(exception: Exception)
    fun log(msg: String)
}