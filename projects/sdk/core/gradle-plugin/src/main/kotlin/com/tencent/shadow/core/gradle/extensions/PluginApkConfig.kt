package com.tencent.shadow.core.gradle.extensions

open class PluginApkConfig {

    var name = ""

    var partKey = ""
    var apkName = ""
    var projectPath = ""
    var buildTask = ""
    var dependsOn: Array<String> = emptyArray()

    constructor(name: String) {
        this.name = name
    }
}