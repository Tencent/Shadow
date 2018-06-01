package com.tencent.cubershi

import com.android.build.gradle.AppPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project

class CuberPluginLoaderTransformPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        System.err.println("CuberPluginLoaderTransformPlugin project.name==" + project.name)
        val plugin = project.plugins.getPlugin(AppPlugin::class.java)
        plugin.extension.registerTransform(CuberPluginLoaderTransform())
    }
}