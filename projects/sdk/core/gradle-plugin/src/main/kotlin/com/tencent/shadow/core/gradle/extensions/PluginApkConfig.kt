package com.tencent.shadow.core.gradle.extensions

open class PluginApkConfig {

    var name = ""

    var partKey = ""

    /**
     * 业务名（空字符串表示同宿主相同业务）
     */
    var businessName = ""

    var apkName = ""
    var projectPath = ""
    var buildTask = ""
    var dependsOn: Array<String> = emptyArray()
    var hostWhiteList: Array<String> = emptyArray()

    constructor(name: String) {
        this.name = name
    }
}