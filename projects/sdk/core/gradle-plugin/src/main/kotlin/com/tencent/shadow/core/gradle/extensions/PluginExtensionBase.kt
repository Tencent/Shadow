package com.tencent.shadow.core.gradle.extensions

open class PluginExtensionBase {
    open var loaderApkName = ""
    open var runtimeApkName = ""

    open var pluginApkNames: Array<String> = emptyArray()
}