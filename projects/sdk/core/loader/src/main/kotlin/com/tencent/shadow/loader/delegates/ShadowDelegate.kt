package com.tencent.shadow.loader.delegates

import android.content.res.Resources
import com.tencent.shadow.loader.Reporter
import com.tencent.shadow.loader.classloaders.PluginClassLoader
import com.tencent.shadow.loader.managers.ComponentManager
import com.tencent.shadow.loader.managers.PluginPackageManager
import com.tencent.shadow.runtime.ShadowApplication
import com.tencent.shadow.runtime.remoteview.ShadowRemoteViewCreatorProvider

abstract class ShadowDelegate() {
    fun inject(pluginPackageManager: PluginPackageManager) {
        _pluginPackageManager = pluginPackageManager
    }

    fun inject(shadowApplication: ShadowApplication) {
        _pluginApplication = shadowApplication
    }

    fun inject(pluginClassLoader: PluginClassLoader) {
        _pluginClassLoader = pluginClassLoader
    }

    fun inject(resources: Resources) {
        _pluginResources = resources
    }

    fun inject(reporter: Reporter) {
        _exceptionReporter = reporter
    }

    fun inject(componentManager: ComponentManager) {
        _componentManager = componentManager
    }

    fun inject(remoteViewCreatorProvider: ShadowRemoteViewCreatorProvider) {
        _remoteViewCreatorProvider = remoteViewCreatorProvider
    }

    private lateinit var _pluginPackageManager: PluginPackageManager
    private lateinit var _pluginApplication: ShadowApplication
    private lateinit var _pluginClassLoader: PluginClassLoader
    private lateinit var _pluginResources: Resources
    private lateinit var _exceptionReporter: Reporter
    private lateinit var _componentManager: ComponentManager
    private lateinit var _remoteViewCreatorProvider: ShadowRemoteViewCreatorProvider

    protected val mPluginPackageManager: PluginPackageManager
        get() = _pluginPackageManager
    protected val mPluginApplication: ShadowApplication
        get() = _pluginApplication
    protected val mPluginClassLoader: PluginClassLoader
        get() = _pluginClassLoader
    protected val mPluginResources: Resources
        get() = _pluginResources
    protected val mExceptionReporter: Reporter
        get() = _exceptionReporter
    protected val mComponentManager: ComponentManager
        get() = _componentManager

    protected val mRemoteViewCreatorProvider: ShadowRemoteViewCreatorProvider
        get() = _remoteViewCreatorProvider

}