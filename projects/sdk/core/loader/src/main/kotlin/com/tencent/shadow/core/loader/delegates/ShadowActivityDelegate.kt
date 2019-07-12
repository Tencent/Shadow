/*
 * Tencent is pleased to support the open source community by making Tencent Shadow available.
 * Copyright (C) 2019 THL A29 Limited, a Tencent company.  All rights reserved.
 *
 * Licensed under the BSD 3-Clause License (the "License"); you may not use
 * this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 *     https://opensource.org/licenses/BSD-3-Clause
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.tencent.shadow.core.loader.delegates

import android.app.Activity
import android.app.Dialog
import android.app.Fragment
import android.content.ComponentName
import android.content.Context
import android.content.Context.LAYOUT_INFLATER_SERVICE
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.util.AttributeSet
import android.view.*
import com.tencent.shadow.core.common.LoggerFactory
import com.tencent.shadow.core.loader.infos.PluginActivityInfo
import com.tencent.shadow.core.loader.managers.ComponentManager.Companion.CM_ACTIVITY_INFO_KEY
import com.tencent.shadow.core.loader.managers.ComponentManager.Companion.CM_BUSINESS_NAME_KEY
import com.tencent.shadow.core.loader.managers.ComponentManager.Companion.CM_CALLING_ACTIVITY_KEY
import com.tencent.shadow.core.loader.managers.ComponentManager.Companion.CM_CLASS_NAME_KEY
import com.tencent.shadow.core.loader.managers.ComponentManager.Companion.CM_EXTRAS_BUNDLE_KEY
import com.tencent.shadow.core.loader.managers.ComponentManager.Companion.CM_LOADER_BUNDLE_KEY
import com.tencent.shadow.core.loader.managers.ComponentManager.Companion.CM_PART_KEY
import com.tencent.shadow.core.runtime.MixResources
import com.tencent.shadow.core.runtime.PluginActivity
import com.tencent.shadow.core.runtime.ShadowLayoutInflater
import com.tencent.shadow.core.runtime.container.HostActivityDelegate
import com.tencent.shadow.core.runtime.container.HostActivityDelegator

/**
 * 壳子Activity与插件Activity转调关系的实现类
 * 它是抽象的是因为它缺少必要的业务信息.业务必须继承这个类提供业务信息.
 *
 * @author cubershi
 */
class ShadowActivityDelegate(private val mDI: DI) : HostActivityDelegate, ShadowDelegate() {
    companion object {
        const val PLUGIN_OUT_STATE_KEY = "PLUGIN_OUT_STATE_KEY"
        val mLogger = LoggerFactory.getLogger(ShadowActivityDelegate::class.java)
    }

    private lateinit var mHostActivityDelegator: HostActivityDelegator
    private lateinit var mPluginActivity: PluginActivity
    private lateinit var mBusinessName: String
    private lateinit var mPartKey: String
    private lateinit var mBundleForPluginLoader: Bundle
    private var mRawIntentExtraBundle: Bundle? = null
    private var mPluginActivityCreated = false
    private var mDependenciesInjected = false
    private var mRecreateCalled = false
    /**
     * 判断是否调用过OnWindowAttributesChanged，如果调用过就说明需要在onCreate之前调用
     */
    private var mCallOnWindowAttributesChanged = false
    private var mBeforeOnCreateOnWindowAttributesChangedCalledParams: WindowManager.LayoutParams? = null
    private lateinit var mMixResources: MixResources

    override fun setDelegator(hostActivityDelegator: HostActivityDelegator) {
        mHostActivityDelegator = hostActivityDelegator
    }

    override fun getPluginActivity(): Any = mPluginActivity

    private lateinit var mCurrentConfiguration: Configuration
    private var mPluginHandleConfigurationChange: Int = 0
    private var mCallingActivity: ComponentName? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        val pluginInitBundle = if (savedInstanceState == null) mHostActivityDelegator.intent.extras else savedInstanceState

        mCallingActivity = pluginInitBundle.getParcelable(CM_CALLING_ACTIVITY_KEY)
        mBusinessName = pluginInitBundle.getString(CM_BUSINESS_NAME_KEY, "")
        val partKey = pluginInitBundle.getString(CM_PART_KEY)!!
        mPartKey = partKey
        mDI.inject(this, partKey)
        mDependenciesInjected = true

        mMixResources = MixResources(mHostActivityDelegator.superGetResources(), mPluginResources)

        val bundleForPluginLoader = pluginInitBundle.getBundle(CM_LOADER_BUNDLE_KEY)!!
        mBundleForPluginLoader = bundleForPluginLoader
        bundleForPluginLoader.classLoader = this.javaClass.classLoader
        val pluginActivityClassName = bundleForPluginLoader.getString(CM_CLASS_NAME_KEY)
        val pluginActivityInfo: PluginActivityInfo = bundleForPluginLoader.getParcelable(CM_ACTIVITY_INFO_KEY)

        mCurrentConfiguration = Configuration(resources.configuration)
        mPluginHandleConfigurationChange =
                (pluginActivityInfo.activityInfo.configChanges
                        or ActivityInfo.CONFIG_SCREEN_SIZE//系统本身就会单独对待这个属性，不声明也不会重启Activity。
                        or ActivityInfo.CONFIG_SMALLEST_SCREEN_SIZE//系统本身就会单独对待这个属性，不声明也不会重启Activity。
                        or 0x20000000 //见ActivityInfo.CONFIG_WINDOW_CONFIGURATION 系统处理属性
                        )
        mRawIntentExtraBundle = pluginInitBundle.getBundle(CM_EXTRAS_BUNDLE_KEY)
        mHostActivityDelegator.intent.replaceExtras(mRawIntentExtraBundle)
        mHostActivityDelegator.intent.setExtrasClassLoader(mPluginClassLoader)

        mHostActivityDelegator.setTheme(pluginActivityInfo.themeResource)
        try {
            val aClass = mPluginClassLoader.loadClass(pluginActivityClassName)
            val pluginActivity = PluginActivity::class.java.cast(aClass.newInstance())
            initPluginActivity(pluginActivity)
            mPluginActivity = pluginActivity

            if (mLogger.isDebugEnabled) {
                mLogger.debug("{} mPluginHandleConfigurationChange=={}", mPluginActivity.javaClass.canonicalName, mPluginHandleConfigurationChange)
            }

            //使PluginActivity替代ContainerActivity接收Window的Callback
            mHostActivityDelegator.window.callback = pluginActivity

            //设置插件AndroidManifest.xml 中注册的WindowSoftInputMode
            mHostActivityDelegator.window.setSoftInputMode(pluginActivityInfo.activityInfo.softInputMode)

            //Activity.onCreate调用之前应该先收到onWindowAttributesChanged。
            if (mCallOnWindowAttributesChanged) {
                pluginActivity.onWindowAttributesChanged(mBeforeOnCreateOnWindowAttributesChangedCalledParams)
                mBeforeOnCreateOnWindowAttributesChangedCalledParams = null
            }

            val pluginSavedInstanceState: Bundle? = savedInstanceState?.getBundle(PLUGIN_OUT_STATE_KEY)
            pluginSavedInstanceState?.classLoader = mPluginClassLoader
            pluginActivity.onCreate(pluginSavedInstanceState)
            mPluginActivityCreated = true
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    private fun initPluginActivity(pluginActivity: PluginActivity) {
        pluginActivity.setHostActivityDelegator(mHostActivityDelegator)
        pluginActivity.setPluginResources(mPluginResources)
        pluginActivity.setHostContextAsBase(mHostActivityDelegator.hostActivity as Context)
        pluginActivity.setPluginClassLoader(mPluginClassLoader)
        pluginActivity.setPluginComponentLauncher(mComponentManager)
        pluginActivity.setPluginApplication(mPluginApplication)
        pluginActivity.setShadowApplication(mPluginApplication)
        pluginActivity.applicationInfo = mPluginApplication.applicationInfo
        pluginActivity.setBusinessName(mBusinessName)
        pluginActivity.setPluginPartKey(mPartKey)
        pluginActivity.remoteViewCreatorProvider = mRemoteViewCreatorProvider
    }

    override fun onResume() {
        mPluginActivity.onResume()
    }

    override fun onNewIntent(intent: Intent) {
        val pluginExtras: Bundle? = intent.getBundleExtra(CM_EXTRAS_BUNDLE_KEY)
        intent.replaceExtras(pluginExtras)
        mPluginActivity.onNewIntent(intent)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        val pluginOutState = Bundle(mPluginClassLoader)
        mPluginActivity.onSaveInstanceState(pluginOutState)
        outState.putBundle(PLUGIN_OUT_STATE_KEY, pluginOutState)
        outState.putString(CM_PART_KEY, mPartKey)
        outState.putBundle(CM_LOADER_BUNDLE_KEY, mBundleForPluginLoader)
        if (mRecreateCalled) {
            outState.putBundle(CM_EXTRAS_BUNDLE_KEY, mHostActivityDelegator.intent.extras)
        } else {
            outState.putBundle(CM_EXTRAS_BUNDLE_KEY, mRawIntentExtraBundle)
        }
    }

    override fun onPause() {
        mPluginActivity.onPause()
    }

    override fun onStart() {
        mPluginActivity.onStart()
    }

    override fun onStop() {
        mPluginActivity.onStop()
    }

    override fun onDestroy() {
        mPluginActivity.onDestroy()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        val diff = newConfig.diff(mCurrentConfiguration)
        if (mLogger.isDebugEnabled) {
            mLogger.debug("{} onConfigurationChanged diff=={}", mPluginActivity.javaClass.canonicalName, diff)
        }
        if (diff == (diff and mPluginHandleConfigurationChange)) {
            mPluginActivity.onConfigurationChanged(newConfig)
            mCurrentConfiguration = Configuration(newConfig)
        } else {
            mHostActivityDelegator.superOnConfigurationChanged(newConfig)
            mHostActivityDelegator.recreate()
        }
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        return mPluginActivity.dispatchKeyEvent(event)
    }

    override fun finish() {
        mPluginActivity.finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        mPluginActivity.onActivityResult(requestCode, resultCode, data)
    }

    override fun onChildTitleChanged(childActivity: Activity, title: CharSequence) {
        mPluginActivity.onChildTitleChanged(childActivity, title)
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
        mPluginActivity.onRestart()
    }

    override fun isChangingConfigurations(): Boolean {
       return mPluginActivity.isChangingConfigurations()
    }


    override fun onUserLeaveHint() {
        mPluginActivity.onUserLeaveHint()
    }

    override fun onCreateThumbnail(outBitmap: Bitmap, canvas: Canvas): Boolean {
        return mPluginActivity.onCreateThumbnail(outBitmap, canvas)
    }

    override fun onCreateDescription(): CharSequence? {
        return mPluginActivity.onCreateDescription()
    }

    override fun onRetainNonConfigurationInstance(): Any? {
        return mPluginActivity.onRetainNonConfigurationInstance()
    }

    override fun onLowMemory() {
        mPluginActivity.onLowMemory()
    }

    override fun onTrackballEvent(event: MotionEvent): Boolean {
        return mPluginActivity.onTrackballEvent(event)
    }

    override fun onUserInteraction() {
        mPluginActivity.onUserInteraction()
    }

    override fun onWindowAttributesChanged(params: WindowManager.LayoutParams) {
        if (mPluginActivityCreated) {
            mPluginActivity.onWindowAttributesChanged(params)
        } else {
            mBeforeOnCreateOnWindowAttributesChangedCalledParams = params
        }
        mCallOnWindowAttributesChanged = true
    }

    override fun onContentChanged() {
        mPluginActivity.onContentChanged()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        mPluginActivity.onWindowFocusChanged(hasFocus)
    }

    override fun onCreatePanelView(featureId: Int): View? {
        return mPluginActivity.onCreatePanelView(featureId)
    }

    override fun onCreatePanelMenu(featureId: Int, menu: Menu): Boolean {
        return mPluginActivity.onCreatePanelMenu(featureId, menu)
    }

    override fun onPreparePanel(featureId: Int, view: View?, menu: Menu): Boolean {
        return mPluginActivity.onPreparePanel(featureId, view, menu)
    }

    override fun onPanelClosed(featureId: Int, menu: Menu) {
        mPluginActivity.onPanelClosed(featureId, menu)
    }

    override fun onCreateDialog(id: Int): Dialog {
        return mPluginActivity.onCreateDialog(id)
    }

    override fun onPrepareDialog(id: Int, dialog: Dialog) {
        mPluginActivity.onPrepareDialog(id, dialog)
    }

    override fun onApplyThemeResource(theme: Resources.Theme, resid: Int, first: Boolean) {
        mHostActivityDelegator.superOnApplyThemeResource(theme, resid, first)
        if (mPluginActivityCreated) {
            mPluginActivity.onApplyThemeResource(theme, resid, first)
        }
    }

    override fun onCreateView(name: String, context: Context, attrs: AttributeSet): View? {
        return mPluginActivity.onCreateView(name, context, attrs)
    }

    override fun onCreateView(parent: View?, name: String, context: Context, attrs: AttributeSet): View? {
        return mPluginActivity.onCreateView(parent, name, context, attrs)
    }

    override fun startActivityFromChild(child: Activity, intent: Intent, requestCode: Int) {
        mPluginActivity.startActivityFromChild(child, intent, requestCode)
    }

    override fun getClassLoader(): ClassLoader {
        return mPluginClassLoader
    }

    override fun getLayoutInflater(): LayoutInflater {
        val inflater = mHostActivityDelegator.applicationContext.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        return ShadowLayoutInflater.build(inflater, mPluginActivity, mPartKey)
    }

    override fun getResources(): Resources {
        if (mDependenciesInjected) {
            return mMixResources;
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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>?, grantResults: IntArray?) {
        mPluginActivity.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun recreate() {
        mRecreateCalled = true
        mHostActivityDelegator.superRecreate()
    }

    override fun getCallingActivity(): ComponentName? {
        return mCallingActivity
    }

    override fun onMultiWindowModeChanged(isInMultiWindowMode: Boolean) {
        mPluginActivity.onMultiWindowModeChanged(isInMultiWindowMode)
    }

    override fun onMultiWindowModeChanged(isInMultiWindowMode: Boolean, newConfig: Configuration?) {
        mPluginActivity.onMultiWindowModeChanged(isInMultiWindowMode, newConfig)
    }
}
