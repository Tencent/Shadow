package com.tencent.shadow.loader.delegates

import android.app.Activity
import android.app.Dialog
import android.app.Fragment
import android.content.Context
import android.content.Context.LAYOUT_INFLATER_SERVICE
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.util.AttributeSet
import android.util.Pair
import android.view.*
import com.tencent.hydevteam.pluginframework.plugincontainer.HostActivityDelegate
import com.tencent.hydevteam.pluginframework.plugincontainer.HostActivityDelegator
import com.tencent.shadow.loader.PluginPackageManager
import com.tencent.shadow.loader.classloaders.PluginClassLoader
import com.tencent.shadow.loader.infos.PluginActivityInfo
import com.tencent.shadow.loader.managers.PendingIntentManager
import com.tencent.shadow.loader.managers.PluginActivitiesManager
import com.tencent.shadow.loader.managers.PluginActivitiesManager.Companion.PLUGIN_ACTIVITY_CLASS_NAME_KEY
import com.tencent.shadow.loader.managers.PluginServicesManager
import com.tencent.shadow.runtime.FixedContextLayoutInflater
import com.tencent.shadow.runtime.PluginActivity
import com.tencent.shadow.runtime.ShadowApplication

/**
 * 壳子Activity与插件Activity转调关系的实现类
 * 它是抽象的是因为它缺少必要的业务信息.业务必须继承这个类提供业务信息.
 *
 * @author cubershi
 */
class HostActivityDelegateImpl(
        private val mPluginPackageManager: PluginPackageManager,
        private val mPluginApplication: ShadowApplication,
        private val mPluginClassLoader: PluginClassLoader,
        private val mPluginResources: Resources,
        private val mPluginActivitiesManager: PluginActivitiesManager,
        private val mPluginServicesManager: PluginServicesManager,
        private val mPendingIntentManager: PendingIntentManager
) : HostActivityDelegate {
    private lateinit var mHostActivityDelegator: HostActivityDelegator
    private lateinit var mPluginActivity: PluginActivity
    private var mPluginActivityCreated = false
    private lateinit var mBundleForPluginLoader: Bundle

    override fun setDelegator(hostActivityDelegator: HostActivityDelegator) {
        mHostActivityDelegator = hostActivityDelegator
    }

    override fun getPluginActivity(): Any = mPluginActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        mHostActivityDelegator.intent.setExtrasClassLoader(mPluginClassLoader)

        val bundleForPluginLoader: Bundle = if (savedInstanceState != null) {
            savedInstanceState.getBundle(PluginActivitiesManager.PLUGIN_LOADER_BUNDLE_KEY)
        } else {
            mHostActivityDelegator.intent.getBundleExtra(PluginActivitiesManager.PLUGIN_LOADER_BUNDLE_KEY)
        }
        mBundleForPluginLoader = bundleForPluginLoader
        mHostActivityDelegator.intent.removeExtra(PluginActivitiesManager.PLUGIN_LOADER_BUNDLE_KEY)
        bundleForPluginLoader.classLoader = this.javaClass.classLoader

        val pluginActivityClassName = bundleForPluginLoader.getString(PLUGIN_ACTIVITY_CLASS_NAME_KEY)
        val pluginActivityInfo: PluginActivityInfo = bundleForPluginLoader.getParcelable(PluginActivitiesManager.PLUGIN_ACTIVITY_INFO_KEY)

        mHostActivityDelegator.setTheme(pluginActivityInfo.themeResource)
        try {
            val aClass = mPluginClassLoader.loadClass(pluginActivityClassName)
            val pluginActivity = PluginActivity::class.java.cast(aClass.newInstance())
            pluginActivity.setContainerActivity(mHostActivityDelegator)
            pluginActivity.setPluginResources(mPluginResources)
            pluginActivity.setPluginLoaderBundle(Pair(PluginActivitiesManager.PLUGIN_LOADER_BUNDLE_KEY,bundleForPluginLoader))
            pluginActivity.setHostContextAsBase(mHostActivityDelegator.hostActivity as Context)
            pluginActivity.setPluginClassLoader(mPluginClassLoader)
            pluginActivity.setPluginActivityLauncher(mPluginActivitiesManager)
            pluginActivity.pendingIntentConverter = mPendingIntentManager
            pluginActivity.setPluginApplication(mPluginApplication)
            pluginActivity.setPluginPackageManager(mPluginPackageManager)
            pluginActivity.setServiceOperator(mPluginServicesManager)
            pluginActivity.setShadowApplication(mPluginApplication)
            pluginActivity.setLibrarySearchPath(mPluginClassLoader.getLibrarySearchPath())
            mPluginActivity = pluginActivity
            pluginActivity.onCreate(savedInstanceState)
            mPluginActivityCreated = true
        } catch (e: Exception) {
            throw RuntimeException(e)
        }

    }

    override fun onResume() {
        mHostActivityDelegator.superOnResume()
        mPluginActivity.onResume()
    }

    override fun onNewIntent(intent: Intent) {
        mPluginActivity.onNewIntent(intent)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        mHostActivityDelegator.superOnSaveInstanceState(outState)
        outState.putBundle(PluginActivitiesManager.PLUGIN_LOADER_BUNDLE_KEY, mBundleForPluginLoader)
        mPluginActivity.onSaveInstanceState(outState)
    }

    override fun onPause() {
        mHostActivityDelegator.superOnPause()
        mPluginActivity.onPause()
    }

    override fun onStart() {
        mHostActivityDelegator.superOnStart()
        mPluginActivity.onStart()
    }

    override fun onStop() {
        mHostActivityDelegator.superOnStop()
        mPluginActivity.onStop()
    }

    override fun onDestroy() {
        mHostActivityDelegator.superOnDestroy()
        mPluginActivity.onDestroy()
    }

    override fun onConfigurationChanged(configuration: Configuration) {
        mHostActivityDelegator.superOnConfigurationChanged(configuration)
        mPluginActivity.onConfigurationChanged(configuration)
    }

    override fun dispatchKeyEvent(keyEvent: KeyEvent): Boolean {
        val dispatchKeyEvent = mPluginActivity.dispatchKeyEvent(keyEvent)
        return when {
            dispatchKeyEvent -> true
            else -> mHostActivityDelegator.superDispatchKeyEvent(keyEvent)
        }
    }

    override fun finish() {
        mPluginActivity.finish()
        mHostActivityDelegator.superFinish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        mPluginActivity.onActivityResult(requestCode, resultCode, data)
    }

    override fun onChildTitleChanged(activity: Activity, charSequence: CharSequence) {
        mPluginActivity.onChildTitleChanged(activity, charSequence)
    }

    override fun onRestoreInstanceState(bundle: Bundle?) {
        mHostActivityDelegator.superOnRestoreInstanceState(bundle)
        mPluginActivity.onRestoreInstanceState(bundle)
    }

    override fun onPostCreate(bundle: Bundle?) {
        mHostActivityDelegator.superOnPostCreate(bundle)
        mPluginActivity.onPostCreate(bundle)
    }

    override fun onRestart() {
        mHostActivityDelegator.superOnRestart()
        mPluginActivity.onRestart()
    }

    override fun onUserLeaveHint() {
        mHostActivityDelegator.superOnUserLeaveHint()
        mPluginActivity.onUserLeaveHint()
    }

    override fun onCreateThumbnail(bitmap: Bitmap, canvas: Canvas): Boolean {
        return mPluginActivity.onCreateThumbnail(bitmap, canvas)
    }

    override fun onCreateDescription(): CharSequence? {
        return mPluginActivity.onCreateDescription()
    }

    override fun onRetainNonConfigurationInstance(): Any? {
        return mPluginActivity.onRetainNonConfigurationInstance()
    }

    override fun onLowMemory() {
        mHostActivityDelegator.superOnLowMemory()
        mPluginActivity.onLowMemory()
    }

    override fun onTrackballEvent(motionEvent: MotionEvent): Boolean {
        return mPluginActivity.onTrackballEvent(motionEvent)
    }

    override fun onUserInteraction() {
        mPluginActivity.onUserInteraction()
    }

    override fun onWindowAttributesChanged(layoutParams: WindowManager.LayoutParams) {
        mHostActivityDelegator.superOnWindowAttributesChanged(layoutParams)
        if (mPluginActivityCreated) {
            mPluginActivity.onWindowAttributesChanged(layoutParams)
        }
    }

    override fun onContentChanged() {
        mPluginActivity.onContentChanged()
    }

    override fun onWindowFocusChanged(b: Boolean) {
        mPluginActivity.onWindowFocusChanged(b)
    }

    override fun onCreatePanelView(featureId: Int): View? {
        return mPluginActivity.onCreatePanelView(featureId)
    }

    override fun onCreatePanelMenu(i: Int, menu: Menu): Boolean {
        val onCreatePanelMenu = mPluginActivity.onCreatePanelMenu(i, menu)
        return when {
            onCreatePanelMenu -> true
            else -> mHostActivityDelegator.superOnCreatePanelMenu(i, menu)
        }
    }

    override fun onPreparePanel(i: Int, view: View?, menu: Menu): Boolean {
        val onPreparePanel = mPluginActivity.onPreparePanel(i, view, menu)
        return when {
            onPreparePanel -> true
            else -> mHostActivityDelegator.superOnPreparePanel(i, view, menu)
        }
    }

    override fun onPanelClosed(i: Int, menu: Menu) {
        mHostActivityDelegator.superOnPanelClosed(i, menu)
        mPluginActivity.onPanelClosed(i, menu)
    }

    override fun onCreateDialog(i: Int): Dialog {
        return mPluginActivity.onCreateDialog(i)
    }

    override fun onPrepareDialog(i: Int, dialog: Dialog) {
        mHostActivityDelegator.superOnPrepareDialog(i, dialog)
        mPluginActivity.onPrepareDialog(i, dialog)
    }

    override fun onApplyThemeResource(theme: Resources.Theme, i: Int, b: Boolean) {
        mHostActivityDelegator.superOnApplyThemeResource(theme, i, b)
        if (mPluginActivityCreated) {
            mPluginActivity.onApplyThemeResource(theme, i, b)
        }
    }

    override fun onCreateView(name: String, context: Context, attrs: AttributeSet): View? {
        return mPluginActivity.onCreateView(name, context, attrs)
    }

    override fun startActivityFromChild(activity: Activity, intent: Intent, i: Int) {
        mHostActivityDelegator.superStartActivityFromChild(activity, intent, i)
        mPluginActivity.startActivityFromChild(activity, intent, i)
    }

    override fun getClassLoader(): ClassLoader {
        return mPluginClassLoader
    }

    override fun getLayoutInflater(): LayoutInflater {
        val inflater = mHostActivityDelegator.applicationContext.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.cloneInContext(mPluginActivity)
        return FixedContextLayoutInflater(inflater, mPluginActivity)
    }

    override fun getResources(): Resources {
        return mPluginResources
    }

    override fun onBackPressed() {
        mPluginActivity.onBackPressed()
    }

    override fun onAttachedToWindow() {
        mPluginActivity.onAttachedToWindow()
    }

    override fun onDetachedFromWindow() {
        mPluginActivity.onDetachedFromWindow()
    }

    override fun onAttachFragment(fragment: Fragment?) {
        mPluginActivity.onAttachFragment(fragment)
    }
}
