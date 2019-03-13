package com.tencent.shadow.core.gradle.extensions

import groovy.lang.Closure
import groovy.lang.Tuple2
import org.gradle.api.NamedDomainObjectContainer

open class PluginBuildType {

    var name = ""

    var loaderApkConfig: Tuple2<String, String> = Tuple2("", "")
    var runtimeApkConfig: Tuple2<String, String> = Tuple2("", "")
    lateinit var pluginApks: NamedDomainObjectContainer<PluginApkConfig>

    constructor(name: String) {
        this.name = name
    }

    fun pluginApks(closure: Closure<PluginApkConfig>) {
        pluginApks.configure(closure)
    }
}