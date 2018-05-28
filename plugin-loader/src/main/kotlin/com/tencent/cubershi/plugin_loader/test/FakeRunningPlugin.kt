package com.tencent.cubershi.plugin_loader.test

import android.content.Intent
import com.tencent.cubershi.mock_interface.MockApplication
import com.tencent.hydevteam.common.progress.ProgressFuture
import com.tencent.hydevteam.pluginframework.installedplugin.InstalledPlugin
import com.tencent.hydevteam.pluginframework.pluginloader.RunningPlugin
import org.slf4j.LoggerFactory
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

class FakeRunningPlugin(internal var mockApplication: MockApplication, internal var installedPlugin: InstalledPlugin) : RunningPlugin {

    override fun startLauncherActivity(intent: Intent): ProgressFuture<*> {
        val startContainerActivity = Intent()
        startContainerActivity.setClassName("com.tencent.libexample", "com.tencent.hydevteam.pluginframework.plugincontainer.PluginContainerActivity")
        startContainerActivity.putExtra(ARG, "com.example.android.basicglsurfaceview.TestSoLoadActivity")
        val hostApplicationContext = mockApplication.hostApplicationContext
        hostApplicationContext.startActivity(startContainerActivity)

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
        private val mLogger = LoggerFactory.getLogger(FakeRunningPlugin::class.java)
        val ARG = "TEST_ARG_LAUNCHER_ACTIVITY"
    }
}
