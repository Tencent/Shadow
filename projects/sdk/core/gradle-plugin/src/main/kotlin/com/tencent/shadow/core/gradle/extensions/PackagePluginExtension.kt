package com.tencent.shadow.core.gradle.extensions

import org.gradle.api.Project

open class PackagePluginExtension  {

    var loaderApkProjectPath = ""
    var runtimeApkProjectPath = ""

    var version : Int = 0
    var uuidNickName = ""
    var compactVersion : Array<Int> = emptyArray()

    var debug : PluginExtensionBase = PluginExtensionBase()
    var release : PluginExtensionBase = PluginExtensionBase()

    var pluginApkProjectPaths : Array<String> = emptyArray()
    var pluginApkPartKeys : Array<String> = emptyArray()

    constructor(project: Project) {
        project.extensions.add("debug", debug)
        project.extensions.add("release", release)
    }
}