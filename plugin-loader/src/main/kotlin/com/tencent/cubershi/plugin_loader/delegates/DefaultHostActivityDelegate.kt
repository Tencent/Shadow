package com.tencent.cubershi.plugin_loader.delegates

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Context.LAYOUT_INFLATER_SERVICE
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.util.AttributeSet
import android.view.*
import com.tencent.cubershi.mock_interface.MockActivity
import com.tencent.cubershi.mock_interface.MockApplication
import com.tencent.cubershi.plugin_loader.FixedContextLayoutInflater
import com.tencent.cubershi.plugin_loader.infos.PluginActivityInfo
import com.tencent.cubershi.plugin_loader.managers.PluginActivitiesManager
import com.tencent.cubershi.plugin_loader.managers.PluginActivitiesManager.Companion.PLUGIN_ACTIVITY_CLASS_NAME_KEY
import com.tencent.hydevteam.pluginframework.plugincontainer.HostActivityDelegate
import com.tencent.hydevteam.pluginframework.plugincontainer.HostActivityDelegator
import dalvik.system.DexClassLoader

class DefaultHostActivityDelegate(
        private val mPluginApplication: MockApplication,
        private val mPluginClassLoader: DexClassLoader,
        private val mPluginResources: Resources,
        private val mPluginActivitiesManager: PluginActivitiesManager
) : HostActivityDelegate {
    private lateinit var mHostActivityDelegator: HostActivityDelegator
    private lateinit var mMockActivity: MockActivity

    override fun setDelegator(hostActivityDelegator: HostActivityDelegator) {
        mHostActivityDelegator = hostActivityDelegator
    }

    override fun onCreate(bundle: Bundle?) {
        mHostActivityDelegator.superOnCreate(bundle)

        mHostActivityDelegator.intent.setExtrasClassLoader(mPluginClassLoader)

        val bundleForPluginLoader = mHostActivityDelegator.intent.getBundleExtra(PluginActivitiesManager.PLUGIN_LOADER_BUNDLE_KEY)
        mHostActivityDelegator.intent.removeExtra(PluginActivitiesManager.PLUGIN_LOADER_BUNDLE_KEY)
        bundleForPluginLoader.classLoader = this.javaClass.classLoader

        val pluginActivityClassName = bundleForPluginLoader.getString(PLUGIN_ACTIVITY_CLASS_NAME_KEY)
        val pluginActivityInfo: PluginActivityInfo = bundleForPluginLoader.getParcelable(PluginActivitiesManager.PLUGIN_ACTIVITY_INFO_KEY)

        mHostActivityDelegator.setTheme(pluginActivityInfo.themeResource)
        try {
            val aClass = mPluginClassLoader.loadClass(pluginActivityClassName)
            val mockActivity = MockActivity::class.java.cast(aClass.newInstance())
            mockActivity.setContainerActivity(mHostActivityDelegator)
            mockActivity.setPluginResources(mPluginResources)
            mockActivity.setHostContextAsBase(mHostActivityDelegator.hostActivity as Context)
            mockActivity.setPluginClassLoader(mPluginClassLoader)
            mockActivity.setPluginActivityLauncher(mPluginActivitiesManager)
            mockActivity.setPluginApplication(mPluginApplication)
//            mockActivity.setTheme(android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen)
            mMockActivity = mockActivity
            mockActivity.performOnCreate(bundle)
        } catch (e: Exception) {
            throw RuntimeException(e)
        }

    }

    override fun onResume() {
        mHostActivityDelegator.superOnResume()
    }

    override fun onNewIntent(intent: Intent) {

    }

    override fun onSaveInstanceState(bundle: Bundle) {

    }

    override fun onPause() {
        mHostActivityDelegator.superOnPause()
    }

    override fun onStop() {
        mHostActivityDelegator.superOnStop()
    }

    override fun onDestroy() {
        mHostActivityDelegator.superOnDestroy()
    }

    override fun onConfigurationChanged(configuration: Configuration) {
        mHostActivityDelegator.superOnConfigurationChanged(configuration)
    }

    override fun dispatchKeyEvent(keyEvent: KeyEvent): Boolean {
        return mHostActivityDelegator.superDispatchKeyEvent(keyEvent)
    }

    override fun finish() {
        mHostActivityDelegator.superFinish()
    }

    override fun onActivityResult(i: Int, i1: Int, intent: Intent) {
        mHostActivityDelegator.superOnActivityResult(i, i1, intent)
    }

    override fun onChildTitleChanged(activity: Activity, charSequence: CharSequence) {

    }

    override fun onRestoreInstanceState(bundle: Bundle?) {
        mHostActivityDelegator.superOnRestoreInstanceState(bundle)
    }

    override fun onPostCreate(bundle: Bundle?) {
        mHostActivityDelegator.superOnPostCreate(bundle)
    }

    override fun onRestart() {
        mHostActivityDelegator.superOnRestart()
    }

    override fun onUserLeaveHint() {
        mHostActivityDelegator.superOnUserLeaveHint()
    }

    override fun onCreateThumbnail(bitmap: Bitmap, canvas: Canvas): Boolean {
        return mHostActivityDelegator.superOnCreateThumbnail(bitmap, canvas)
    }

    override fun onCreateDescription(): CharSequence? {
        return mHostActivityDelegator.superOnCreateDescription()
    }

    override fun onRetainNonConfigurationInstance(): Any {
        return mHostActivityDelegator.superOnRetainNonConfigurationInstance()
    }

    override fun onLowMemory() {
        mHostActivityDelegator.superOnLowMemory()
    }

    override fun onTrackballEvent(motionEvent: MotionEvent): Boolean {
        return mHostActivityDelegator.superOnTrackballEvent(motionEvent)
    }

    override fun onUserInteraction() {
        mHostActivityDelegator.superOnUserInteraction()
    }

    override fun onWindowAttributesChanged(layoutParams: WindowManager.LayoutParams) {
        mHostActivityDelegator.superOnWindowAttributesChanged(layoutParams)
    }

    override fun onContentChanged() {
        mHostActivityDelegator.superOnContentChanged()
    }

    override fun onWindowFocusChanged(b: Boolean) {
        mHostActivityDelegator.superOnWindowFocusChanged(b)
    }

    override fun onCreatePanelView(i: Int): View? {
        return mHostActivityDelegator.superOnCreatePanelView(i)
    }

    override fun onCreatePanelMenu(i: Int, menu: Menu): Boolean {
        return mHostActivityDelegator.superOnCreatePanelMenu(i, menu)
    }

    override fun onPreparePanel(i: Int, view: View?, menu: Menu): Boolean {
        return mHostActivityDelegator.superOnPreparePanel(i, view, menu)
    }

    override fun onPanelClosed(i: Int, menu: Menu) {
        mHostActivityDelegator.superOnPanelClosed(i, menu)
    }

    override fun onCreateDialog(i: Int): Dialog {
        return mHostActivityDelegator.superOnCreateDialog(i)
    }

    override fun onPrepareDialog(i: Int, dialog: Dialog) {
        mHostActivityDelegator.superOnPrepareDialog(i, dialog)
    }

    override fun onApplyThemeResource(theme: Resources.Theme, i: Int, b: Boolean) {
        mHostActivityDelegator.superOnApplyThemeResource(theme, i, b)
    }

    override fun onCreateView(name: String, context: Context, attrs: AttributeSet): View? {
        return mHostActivityDelegator.superOnCreateView(name, context, attrs)
    }

    override fun startActivityFromChild(activity: Activity, intent: Intent, i: Int) {
        mHostActivityDelegator.superStartActivityFromChild(activity, intent, i)
    }

    override fun getClassLoader(): ClassLoader {
        return mHostActivityDelegator.superGetClassLoader()
    }

    override fun getLayoutInflater(): LayoutInflater {
        val inflater = mHostActivityDelegator.applicationContext.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.cloneInContext(mMockActivity)
        return FixedContextLayoutInflater(inflater, mMockActivity)
    }

    override fun getResources(): Resources {
        return mPluginResources
    }
}
