package com.tencent.shadow.pluginloader

import android.content.Intent
import com.tencent.cubershi.mock_interface.MockApplication
import com.tencent.hydevteam.common.progress.ProgressFuture
import com.tencent.hydevteam.pluginframework.installedplugin.InstalledPlugin
import com.tencent.hydevteam.pluginframework.pluginloader.RunningPlugin
import com.tencent.shadow.pluginloader.infos.PluginInfo
import com.tencent.shadow.pluginloader.managers.PluginActivitiesManager
import org.slf4j.LoggerFactory
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

internal class ShadowRunningPlugin(
        private val mockApplication: MockApplication
        , private val installedPlugin: InstalledPlugin
        , private val pluginInfo: PluginInfo
        , private val mPluginActivitiesManager: PluginActivitiesManager
) : RunningPlugin {

    override fun startLauncherActivity(intent: Intent): ProgressFuture<*> {
        val launcherActivity = mPluginActivitiesManager.launcherActivity
        val pluginIntent = Intent(intent)
        pluginIntent.component = launcherActivity
        val hostApplicationContext = mockApplication
        hostApplicationContext.startActivity(pluginIntent)

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
        return null
    }

    override fun unload() {

    }

    companion object {
        private val mLogger = LoggerFactory.getLogger(ShadowRunningPlugin::class.java)
    }
}
