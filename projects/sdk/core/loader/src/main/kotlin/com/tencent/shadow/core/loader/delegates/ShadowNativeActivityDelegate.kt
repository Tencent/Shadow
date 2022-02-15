package com.tencent.shadow.core.loader.delegates

import android.content.ComponentName
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.view.InputQueue
import android.view.SurfaceHolder
import com.tencent.shadow.core.runtime.PackageManagerInvokeRedirect
import com.tencent.shadow.core.runtime.ShadowNativeActivity
import com.tencent.shadow.core.runtime.container.HostNativeActivityDelegate

class ShadowNativeActivityDelegate(mDI: DI) : ShadowActivityDelegate(mDI),
    HostNativeActivityDelegate {

    private val mPluginActivity: ShadowNativeActivity
        get()
        = super.pluginActivity as ShadowNativeActivity

    override fun surfaceCreated(holder: SurfaceHolder) {
        return mPluginActivity.surfaceCreated(holder)
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        return mPluginActivity.surfaceChanged(holder, format, width, height)
    }

    override fun surfaceRedrawNeeded(holder: SurfaceHolder) {
        return mPluginActivity.surfaceRedrawNeeded(holder)
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        return mPluginActivity.surfaceDestroyed(holder)
    }

    override fun onInputQueueCreated(queue: InputQueue) {
        return mPluginActivity.onInputQueueCreated(queue)
    }

    override fun onInputQueueDestroyed(queue: InputQueue) {
        return mPluginActivity.onInputQueueCreated(queue)
    }

    override fun onGlobalLayout() {
        return mPluginActivity.onGlobalLayout()
    }

    //预期只有NativeActivity会调用这个方法
    override fun getPackageManager(): PackageManager {
        val pluginPackageManager =
            PackageManagerInvokeRedirect.getPluginPackageManager(mPluginActivity.classLoader)
        return object : PackageManagerWrapper(mHostActivityDelegator.superGetPackageManager()) {
            override fun getActivityInfo(component: ComponentName, flags: Int): ActivityInfo {
                return pluginPackageManager.getActivityInfo(component, flags)
            }
        }
    }
}
