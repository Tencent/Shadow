package com.tencent.shadow.loader

interface Reporter {
    fun reportException(exception: Exception)
    fun log(msg: String)
}