package com.tencent.shadow.core.loader

import android.content.Intent
import com.tencent.hydevteam.common.progress.ProgressFuture
import com.tencent.hydevteam.pluginframework.installedplugin.InstalledPlugin
import com.tencent.hydevteam.pluginframework.pluginloader.RunningPlugin
import com.tencent.shadow.core.loader.infos.PluginInfo
import com.tencent.shadow.core.loader.managers.ComponentManager
import com.tencent.shadow.runtime.ShadowApplication
import org.slf4j.LoggerFactory
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

internal class ShadowRunningPlugin(
        private val shadowApplication: ShadowApplication
        , private val installedPlugin: InstalledPlugin
        , private val pluginInfo: PluginInfo
        , private val mComponentManager: ComponentManager
) : RunningPlugin {

    override fun startLauncherActivity(intent: Intent): ProgressFuture<*> {
        val launcherActivity = mComponentManager.getLauncherActivity(pluginInfo.partKey)
        val pluginIntent = Intent(intent)
        pluginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        pluginIntent.component = launcherActivity
        val pluginApplicationContext = shadowApplication
        pluginApplicationContext.startActivity(pluginIntent)

        return object : ProgressFuture<Any> {
            override fun getProgress(): Double {
                return 1.0
            }

            override fun cancel(mayInterruptIfRunning: Boolean): Boolean {
                return false
            }

            override fun isCancelled(): Boolean {
                return false
            }

            override fun isDone(): Boolean {
                return true
            }

            @Throws(InterruptedException::class, ExecutionException::class)
            override fun get(): Any? {
                if (mLogger.isInfoEnabled) {
                    mLogger.info("startLauncherActivity path=={}", installedPlugin.pluginFile.absolutePath)
                }
                return null
            }

            @Throws(InterruptedException::class, ExecutionException::class, TimeoutException::class)
            override operator fun get(timeout: Long, unit: TimeUnit): Any? {
                return null
            }
        }
    }

    override fun startInitActivity(intent: Intent): ProgressFuture<*>? {
        val initActivity = mComponentManager.getInitActivity(pluginInfo.partKey)
        val pluginIntent = Intent(intent)
        pluginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        pluginIntent.component = initActivity
        val pluginApplicationContext = shadowApplication
        pluginApplicationContext.startActivity(pluginIntent)

        return object : ProgressFuture<Any> {
            override fun getProgress(): Double {
                return 1.0
            }

            override fun cancel(mayInterruptIfRunning: Boolean): Boolean {
                return false
            }

            override fun isCancelled(): Boolean {
                return false
            }

            override fun isDone(): Boolean {
                return true
            }

            @Throws(InterruptedException::class, ExecutionException::class)
            override fun get(): Any? {
                if (mLogger.isInfoEnabled) {
                    mLogger.info("startInitActivity path=={}", installedPlugin.pluginFile.absolutePath)
                }
                return null
            }

            @Throws(InterruptedException::class, ExecutionException::class, TimeoutException::class)
            override operator fun get(timeout: Long, unit: TimeUnit): Any? {
                return null
            }
        }
    }

    override fun unload() {

    }

    companion object {
        private val mLogger = LoggerFactory.getLogger(ShadowRunningPlugin::class.java)
    }
}
