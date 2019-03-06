package com.tencent.shadow.core.gradle.extensions

import groovy.lang.Closure
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project

open class PackagePluginExtension  {

    var loaderApkProjectPath = ""
    var runtimeApkProjectPath = ""

    var uuid = ""
    var version : Int = 0
    var uuidNickName = ""
    var compactVersion : Array<Int> = emptyArray()

    var buildTypes: NamedDomainObjectContainer<PluginBuildType>

    constructor(project: Project) {
        buildTypes = project.container(PluginBuildType::class.java)
        buildTypes.all {
            it.pluginApks = project.container(PluginApkConfig::class.java)
        }
    }

    fun pluginTypes(closure: Closure<PluginBuildType>) {
        buildTypes.configure(closure)
    }
}