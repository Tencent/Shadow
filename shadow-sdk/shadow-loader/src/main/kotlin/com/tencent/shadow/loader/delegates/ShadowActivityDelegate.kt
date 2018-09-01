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
import android.view.*
import com.tencent.hydevteam.pluginframework.plugincontainer.HostActivityDelegate
import com.tencent.hydevteam.pluginframework.plugincontainer.HostActivityDelegator
import com.tencent.shadow.loader.infos.PluginActivityInfo
import com.tencent.shadow.loader.infos.PluginInfo.Companion.PART_KEY
import com.tencent.shadow.loader.managers.PluginActivitiesManager
import com.tencent.shadow.loader.managers.PluginActivitiesManager.Companion.PLUGIN_ACTIVITY_CLASS_NAME_KEY
import com.tencent.shadow.loader.managers.PluginActivitiesManager.Companion.PLUGIN_ACTIVITY_INFO_KEY
import com.tencent.shadow.loader.managers.PluginActivitiesManager.Companion.PLUGIN_EXTRAS_BUNDLE_KEY
import com.tencent.shadow.loader.managers.PluginActivitiesManager.Companion.PLUGIN_LOADER_BUNDLE_KEY
import com.tencent.shadow.runtime.FixedContextLayoutInflater
import com.tencent.shadow.runtime.PluginActivity
import com.tencent.shadow.runtime.ShadowActivity

/**
 * 壳子Activity与插件Activity转调关系的实现类
 * 它是抽象的是因为它缺少必要的业务信息.业务必须继承这个类提供业务信息.
 *
 * @author cubershi
 */
class ShadowActivityDelegate(private val mDI: DI) : HostActivityDelegate, ShadowDelegate() {
    companion object {
        const val PLUGIN_OUT_STATE_KEY = "PLUGIN_OUT_STATE_KEY"
    }

    private lateinit var mHostActivityDelegator: HostActivityDelegator
    private lateinit var mPluginActivity: PluginActivity
    private lateinit var mPartKey: String
    private lateinit var mBundleForPluginLoader: Bundle
    private var mPluginActivityCreated = false
    private var mDependenciesInjected = false

    override fun setDelegator(hostActivityDelegator: HostActivityDelegator) {
        mHostActivityDelegator = hostActivityDelegator
    }

    override fun getPluginActivity(): Any = mPluginActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        if (checkIllegalIntent()) return

        val pluginInitBundle = if (savedInstanceState == null) mHostActivityDelegator.intent.extras else savedInstanceState

        val partKey = pluginInitBundle.getString(PART_KEY)!!
        mPartKey = partKey
        mDI.inject(this, partKey)
        mDependenciesInjected = true

        val bundleForPluginLoader = pluginInitBundle.getBundle(PLUGIN_LOADER_BUNDLE_KEY)!!
        mBundleForPluginLoader = bundleForPluginLoader
        bundleForPluginLoader.classLoader = this.javaClass.classLoader
        val pluginActivityClassName = bundleForPluginLoader.getString(PLUGIN_ACTIVITY_CLASS_NAME_KEY)
        val pluginActivityInfo: PluginActivityInfo = bundleForPluginLoader.getParcelable(PLUGIN_ACTIVITY_INFO_KEY)

        if (savedInstanceState == null) {
            val pluginExtras: Bundle? = pluginInitBundle.getBundle(PLUGIN_EXTRAS_BUNDLE_KEY)
            mHostActivityDelegator.intent.replaceExtras(pluginExtras)
        }
        mHostActivityDelegator.intent.setExtrasClassLoader(mPluginClassLoader)

        mHostActivityDelegator.setTheme(pluginActivityInfo.themeResource)
        try {
            val aClass = mPluginClassLoader.loadClass(pluginActivityClassName)
            val pluginActivity = PluginActivity::class.java.cast(aClass.newInstance())
            initPluginActivity(pluginActivity)
            mPluginActivity = pluginActivity

            val pluginSavedInstanceState: Bundle? = savedInstanceState?.getBundle(PLUGIN_OUT_STATE_KEY)
            pluginSavedInstanceState?.classLoader = mPluginClassLoader
            pluginActivity.onCreate(pluginSavedInstanceState)
            mPluginActivityCreated = true
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    /**
     * @return true表示Intent不合法，应直接return onCreate方法。
     */
    private fun checkIllegalIntent(): Boolean {
        //todo cubershi: 这里由于没有DI注入依赖已经不能正常工作。而且这个初始化一个empty的PluginActivity的做法也比较重，应该改在Container中做这个事。
        if (mHostActivityDelegator.intent.hasExtra(PluginActivitiesManager.PLUGIN_LOADER_BUNDLE_KEY).not()) {
            mHostActivityDelegator.superFinish()
            val emptyPluginActivity: PluginActivity = object : ShadowActivity() {}
            initPluginActivity(emptyPluginActivity)
            mPluginActivity = emptyPluginActivity
            emptyPluginActivity.onCreate(null)
            mPluginActivityCreated = true
            return true
        } else {
            return false
        }
    }

    private fun initPluginActivity(pluginActivity: PluginActivity) {
        pluginActivity.setContainerActivity(mHostActivityDelegator)
        pluginActivity.setPluginResources(mPluginResources)
        pluginActivity.setHostContextAsBase(mHostActivityDelegator.hostActivity as Context)
        pluginActivity.setPluginClassLoader(mPluginClassLoader)
        pluginActivity.setPluginActivityLauncher(mPluginActivitiesManager)
        pluginActivity.pendingIntentConverter = mPendingIntentManager
        pluginActivity.setPluginApplication(mPluginApplication)
        pluginActivity.setPluginPackageManager(mPluginPackageManager)
        pluginActivity.setServiceOperator(mPluginServicesManager)
        pluginActivity.setShadowApplication(mPluginApplication)
        pluginActivity.setLibrarySearchPath(mPluginClassLoader.getLibrarySearchPath())
    }

    override fun onResume() {
        mHostActivityDelegator.superOnResume()
        mPluginActivity.onResume()
    }

    override fun onNewIntent(intent: Intent) {
        mPluginActivity.onNewIntent(intent)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        val pluginOutState = Bundle(mPluginClassLoader)
        mPluginActivity.onSaveInstanceState(pluginOutState)
        outState.putBundle(PLUGIN_OUT_STATE_KEY, pluginOutState)
        outState.putString(PART_KEY, mPartKey)
        outState.putBundle(PLUGIN_LOADER_BUNDLE_KEY, mBundleForPluginLoader)
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

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        val pluginSavedInstanceState: Bundle? = savedInstanceState?.getBundle(PLUGIN_OUT_STATE_KEY)
        mPluginActivity.onRestoreInstanceState(pluginSavedInstanceState)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        val pluginSavedInstanceState: Bundle? = savedInstanceState?.getBundle(PLUGIN_OUT_STATE_KEY)
        mPluginActivity.onPostCreate(pluginSavedInstanceState)
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
        if (mHostActivityDelegator.isFinishing) {
            return null
        }
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
        if (mDependenciesInjected) {
            return mPluginResources
        } else {
            //预期只有android.view.Window.getDefaultFeatures会调用到这个分支，此时我们还无法确定插件资源
            //而getDefaultFeatures只需要访问系统资源
            return Resources.getSystem()
        }
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
