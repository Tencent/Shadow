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

package com.tencent.shadow.dynamic.loader.impl

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import com.tencent.shadow.core.loader.ShadowPluginLoader
import com.tencent.shadow.core.runtime.container.ContentProviderDelegateProviderHolder
import com.tencent.shadow.core.runtime.container.DelegateProviderHolder
import com.tencent.shadow.dynamic.host.UuidManager
import java.util.concurrent.CountDownLatch

internal class DynamicPluginLoader(hostContext: Context, uuid: String) {
    companion object {
        private const val CORE_LOADER_FACTORY_IMPL_NAME =
            "com.tencent.shadow.dynamic.loader.impl.CoreLoaderFactoryImpl"
    }

    fun setUuidManager(p0: UuidManager?) {
        if (p0 != null)
            mUuidManager = p0
        //todo #30 兼容mUuidManager为null时的逻辑
    }

    private val mPluginLoader: ShadowPluginLoader

    private val mDynamicLoaderClassLoader: ClassLoader =
        DynamicPluginLoader::class.java.classLoader!!

    private var mContext: Context

    private lateinit var mUuidManager: UuidManager

    private var mUuid: String

    private val mUiHandler = Handler(Looper.getMainLooper())

    /**
     * 同一个IServiceConnection只会对应一个ServiceConnection对象，此Map就是保存这种对应关系
     */
    private val mConnectionMap = HashMap<IBinder, ServiceConnection>()

    init {
        try {
            val coreLoaderFactory = mDynamicLoaderClassLoader.getInterface(
                CoreLoaderFactory::class.java,
                CORE_LOADER_FACTORY_IMPL_NAME
            )
            mPluginLoader = coreLoaderFactory.build(hostContext)
            DelegateProviderHolder.setDelegateProvider(
                mPluginLoader.delegateProviderKey,
                mPluginLoader
            )
            ContentProviderDelegateProviderHolder.setContentProviderDelegateProvider(mPluginLoader)
            mPluginLoader.onCreate()
        } catch (e: Exception) {
            throw RuntimeException("当前的classLoader找不到PluginLoader的实现", e)
        }
        mContext = hostContext
        mUuid = uuid
    }

    fun loadPlugin(partKey: String) {
        val installedApk = mUuidManager.getPlugin(mUuid, partKey)
        val future = mPluginLoader.loadPlugin(installedApk)
        future.get()
    }

    fun getLoadedPlugin(): MutableMap<String, Boolean> {
        val plugins = mPluginLoader.getAllPluginPart()
        val loadPlugins = hashMapOf<String, Boolean>()
        for (part in plugins) {
            loadPlugins[part.key] = part.value.application.isCallOnCreate
        }
        return loadPlugins
    }

    @Synchronized
    fun callApplicationOnCreate(partKey: String) {
        mPluginLoader.callApplicationOnCreate(partKey)
    }

    fun convertActivityIntent(pluginActivityIntent: Intent): Intent? {
        return mPluginLoader.mComponentManager.convertPluginActivityIntent(pluginActivityIntent)
    }

    @Synchronized
    fun startPluginService(pluginServiceIntent: Intent): ComponentName? {

        fun realAction(): ComponentName? {
            return mPluginLoader.getPluginServiceManager().startPluginService(pluginServiceIntent)
        }


        // 确保在ui线程调用
        var componentName: ComponentName? = null
        if (isUiThread()) {
            componentName = realAction()
        } else {
            val waitUiLock = CountDownLatch(1)
            mUiHandler.post {
                componentName = realAction()
                waitUiLock.countDown()
            }
            waitUiLock.await()
        }

        return componentName
    }

    @Synchronized
    fun stopPluginService(pluginServiceIntent: Intent): Boolean {

        fun realAction(): Boolean {
            return mPluginLoader.getPluginServiceManager().stopPluginService(pluginServiceIntent)
        }

        // 确保在ui线程调用
        var stopped: Boolean = false
        if (isUiThread()) {
            stopped = realAction()
        } else {
            val waitUiLock = CountDownLatch(1)
            mUiHandler.post {
                stopped = realAction()
                waitUiLock.countDown()
            }
            waitUiLock.await()
        }
        return stopped
    }

    @Synchronized
    fun bindPluginService(
        pluginServiceIntent: Intent,
        binderPsc: BinderPluginServiceConnection,
        flags: Int
    ): Boolean {

        fun realAction(): Boolean {
            if (mConnectionMap[binderPsc.mRemote] == null) {
                mConnectionMap[binderPsc.mRemote] = ServiceConnectionWrapper(binderPsc)
            }

            val connWrapper = mConnectionMap[binderPsc.mRemote]!!
            return mPluginLoader.getPluginServiceManager()
                .bindPluginService(pluginServiceIntent, connWrapper, flags)
        }
        // 确保在ui线程调用
        var stop: Boolean = false
        if (isUiThread()) {
            stop = realAction()
        } else {
            val waitUiLock = CountDownLatch(1)
            mUiHandler.post {
                stop = realAction()
                waitUiLock.countDown()
            }
            waitUiLock.await();
        }

        return stop

    }

    @Synchronized
    fun unbindService(connBinder: IBinder) {
        mUiHandler.post {
            mConnectionMap[connBinder]?.let {
                mConnectionMap.remove(connBinder)
                mPluginLoader.getPluginServiceManager().unbindPluginService(it)
            }
        }
    }

    @Synchronized
    fun startActivityInPluginProcess(intent: Intent) {
        mUiHandler.post {
            mContext.startActivity(intent)
        }
    }

    private class ServiceConnectionWrapper(private val mConnection: BinderPluginServiceConnection) :
        ServiceConnection {

        override fun onServiceDisconnected(name: ComponentName) {
            mConnection.onServiceDisconnected(name)
        }

        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            mConnection.onServiceConnected(name, service)
        }

    }

    private fun isUiThread(): Boolean {

        return Thread.currentThread() === Looper.getMainLooper().thread
    }

    /**
     * 从apk中读取接口的实现
     *
     * @param clazz     接口类
     * @param className 实现类的类名
     * @param <T>       接口类型
     * @return 所需接口
     * @throws Exception
    </T> */
    @Throws(Exception::class)
    fun <T> ClassLoader.getInterface(clazz: Class<T>, className: String): T {
        try {
            val interfaceImplementClass = loadClass(className)
            val interfaceImplement = interfaceImplementClass.newInstance()
            return clazz.cast(interfaceImplement) as T
        } catch (e: ClassNotFoundException) {
            throw Exception(e)
        } catch (e: InstantiationException) {
            throw Exception(e)
        } catch (e: ClassCastException) {
            throw Exception(e)
        } catch (e: IllegalAccessException) {
            throw Exception(e)
        }
    }

}
