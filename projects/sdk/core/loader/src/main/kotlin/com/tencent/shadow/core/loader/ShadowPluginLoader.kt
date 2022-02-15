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

package com.tencent.shadow.core.loader

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.Parcel
import com.tencent.shadow.core.common.InstalledApk
import com.tencent.shadow.core.common.LoggerFactory
import com.tencent.shadow.core.load_parameters.LoadParameters
import com.tencent.shadow.core.loader.blocs.LoadPluginBloc
import com.tencent.shadow.core.loader.delegates.*
import com.tencent.shadow.core.loader.exceptions.LoadPluginException
import com.tencent.shadow.core.loader.infos.PluginParts
import com.tencent.shadow.core.loader.managers.ComponentManager
import com.tencent.shadow.core.loader.managers.PluginContentProviderManager
import com.tencent.shadow.core.loader.managers.PluginServiceManager
import com.tencent.shadow.core.runtime.UriConverter
import com.tencent.shadow.core.runtime.container.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

abstract class ShadowPluginLoader(hostAppContext: Context) : DelegateProvider, DI,
    ContentProviderDelegateProvider {

    protected val mExecutorService = Executors.newCachedThreadPool()

    open val delegateProviderKey: String = DelegateProviderHolder.DEFAULT_KEY

    /**
     * loadPlugin方法是在子线程被调用的。而getHostActivityDelegate方法是在主线程被调用的。
     * 两个方法需要传递数据（主要是PluginParts），因此需要同步。
     */
    private val mLock = ReentrantLock()

    /**
     * 多插件Map
     * key: partKey
     * value: PluginParts
     * @GuardedBy("mLock")
     */
    private val mPluginPartsMap = hashMapOf<String, PluginParts>()


    lateinit var mComponentManager: ComponentManager

    /**
     * @GuardedBy("mLock")
     */
    abstract fun getComponentManager(): ComponentManager

    private lateinit var mPluginServiceManager: PluginServiceManager

    private val mPluginContentProviderManager: PluginContentProviderManager =
        PluginContentProviderManager()

    private val mPluginServiceManagerLock = ReentrantLock()

    private val mHostAppContext: Context = hostAppContext

    private val mUiHandler = Handler(Looper.getMainLooper())

    companion object {
        private val mLogger = LoggerFactory.getLogger(ShadowPluginLoader::class.java)
    }

    init {
        UriConverter.setUriParseDelegate(mPluginContentProviderManager)
    }

    fun getPluginServiceManager(): PluginServiceManager {
        mPluginServiceManagerLock.withLock {
            return mPluginServiceManager
        }

    }

    fun getPluginParts(partKey: String): PluginParts? {
        mLock.withLock {
            return mPluginPartsMap[partKey]
        }
    }

    fun getAllPluginPart(): HashMap<String, PluginParts> {
        mLock.withLock {
            return mPluginPartsMap
        }
    }

    fun onCreate() {
        mComponentManager = getComponentManager()
        mComponentManager.setPluginContentProviderManager(mPluginContentProviderManager)
    }

    fun callApplicationOnCreate(partKey: String) {
        fun realAction() {
            val pluginParts = getPluginParts(partKey)
            pluginParts?.let {
                val application = pluginParts.application
                application.attachBaseContext(mHostAppContext)
                mPluginContentProviderManager.createContentProviderAndCallOnCreate(
                    application, partKey, pluginParts
                )
                application.onCreate()
            }
        }
        if (isUiThread()) {
            realAction()
        } else {
            val waitUiLock = CountDownLatch(1)
            mUiHandler.post {
                realAction()
                waitUiLock.countDown()
            }
            waitUiLock.await();
        }
    }

    @Throws(LoadPluginException::class)
    open fun loadPlugin(
        installedApk: InstalledApk
    ): Future<*> {
        val loadParameters = installedApk.getLoadParameters()
        if (mLogger.isInfoEnabled) {
            mLogger.info("start loadPlugin")
        }
        // 在这里初始化PluginServiceManager
        mPluginServiceManagerLock.withLock {
            if (!::mPluginServiceManager.isInitialized) {
                mPluginServiceManager = PluginServiceManager(this, mHostAppContext)
            }

            mComponentManager.setPluginServiceManager(mPluginServiceManager)
        }

        return LoadPluginBloc.loadPlugin(
            mExecutorService,
            mComponentManager,
            mLock,
            mPluginPartsMap,
            mHostAppContext,
            installedApk,
            loadParameters
        )
    }

    override fun getHostActivityDelegate(aClass: Class<out HostActivityDelegator>): HostActivityDelegate {
        return if (HostNativeActivityDelegator::class.java.isAssignableFrom(aClass)) {
            ShadowNativeActivityDelegate(this)
        } else {
            ShadowActivityDelegate(this)
        }
    }


    override fun getHostContentProviderDelegate(): HostContentProviderDelegate {
        return ShadowContentProviderDelegate(mPluginContentProviderManager)
    }

    override fun inject(delegate: ShadowDelegate, partKey: String) {
        mLock.withLock {
            val pluginParts = mPluginPartsMap[partKey]
            if (pluginParts == null) {
                throw IllegalStateException("partKey==${partKey}在map中找不到。此时map：${mPluginPartsMap}")
            } else {
                delegate.inject(pluginParts.appComponentFactory)
                delegate.inject(pluginParts.application)
                delegate.inject(pluginParts.classLoader)
                delegate.inject(pluginParts.resources)
                delegate.inject(mComponentManager)
            }
        }
    }

    fun InstalledApk.getLoadParameters(): LoadParameters {
        val parcel = Parcel.obtain()
        parcel.unmarshall(parcelExtras, 0, parcelExtras.size)
        parcel.setDataPosition(0)
        val loadParameters = LoadParameters(parcel)
        parcel.recycle()
        return loadParameters
    }

    private fun isUiThread(): Boolean {
        return Thread.currentThread() === Looper.getMainLooper().thread
    }
}
