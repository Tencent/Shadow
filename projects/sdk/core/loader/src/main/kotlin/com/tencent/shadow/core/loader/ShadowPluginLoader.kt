package com.tencent.shadow.core.loader

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.Parcel
import com.tencent.shadow.core.common.InstalledApk
import com.tencent.shadow.core.common.LoggerFactory
import com.tencent.shadow.core.loader.blocs.LoadPluginBloc
import com.tencent.shadow.core.loader.classloaders.InterfaceClassLoader
import com.tencent.shadow.core.loader.delegates.*
import com.tencent.shadow.core.loader.exceptions.LoadPluginException
import com.tencent.shadow.core.loader.infos.PluginParts
import com.tencent.shadow.core.loader.managers.CommonPluginPackageManager
import com.tencent.shadow.core.loader.managers.ComponentManager
import com.tencent.shadow.core.loader.managers.PluginBroadcastManager
import com.tencent.shadow.core.loader.managers.PluginContentProviderManager
import com.tencent.shadow.core.loader.remoteview.ShadowRemoteViewCreatorImp
import com.tencent.shadow.runtime.UriParseDelegate
import com.tencent.shadow.runtime.container.*
import com.tencent.shadow.runtime.remoteview.ShadowRemoteViewCreator
import com.tencent.shadow.runtime.remoteview.ShadowRemoteViewCreatorProvider
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

abstract class ShadowPluginLoader(hostAppContext: Context) : DelegateProvider, DI ,ContentProviderDelegateProvider {

    private val mExecutorService = Executors.newCachedThreadPool()

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
    abstract fun getComponentManager():ComponentManager

    /**
     * @GuardedBy("mLock")
     */
    abstract fun getBusinessPluginReceiverManager(hostAppContext: Context): PluginBroadcastManager

    abstract val mExceptionReporter: Reporter

    private val mCommonPluginPackageManager = CommonPluginPackageManager()

    private lateinit var mPluginServiceManager: PluginServiceManager

    private val mPluginContentProviderManager: PluginContentProviderManager = PluginContentProviderManager()

    private val mPluginServiceManagerLock = ReentrantLock()

    private val mInterfaceClassLoader = InterfaceClassLoader(ShadowPluginLoader::class.java.classLoader.parent)

    /**
     * 插件将要使用的so的ABI，Loader会将其从apk中解压出来。
     * 如果插件不需要so，则返回""空字符串。
     */
    abstract val mAbi: String

    private val  mShadowRemoteViewCreatorProvider: ShadowRemoteViewCreatorProvider = ShadowRemoteViewCreatorProviderImpl()

    private val mHostAppContext: Context = hostAppContext

    private val mUiHandler = Handler(Looper.getMainLooper())

    companion object {
        private val mLogger = LoggerFactory.getLogger(ShadowPluginLoader::class.java)
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

    fun getAllPluginPart() :HashMap<String,PluginParts> {
        mLock.withLock {
            return mPluginPartsMap
        }
    }

    fun onCreate(){
        mComponentManager = getComponentManager()
        mComponentManager.setPluginContentProviderManager(mPluginContentProviderManager)
    }

    fun callApplicationOnCreate(partKey: String) {
        fun realAction() {
            val pluginParts = getPluginParts(partKey)
            mPluginContentProviderManager.createContentProviderAndCallOnCreate(mHostAppContext, partKey, pluginParts)
            pluginParts?.let {
                pluginParts.application.onCreate()
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
    fun loadPlugin(
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

        if (loadParameters.pluginFileType == 1) { // 是接口apk

            return LoadPluginBloc.loadInterface(
                    mExecutorService,
                    mAbi,
                    mHostAppContext,
                    mInterfaceClassLoader,
                    installedApk
                    )
        } else {
            return LoadPluginBloc.loadPlugin(
                    mExecutorService,
                    mAbi,
                    mCommonPluginPackageManager,
                    mComponentManager,
                    getBusinessPluginReceiverManager(mHostAppContext),
                    mLock,
                    mPluginPartsMap,
                    mHostAppContext,
                    installedApk,
                    loadParameters,
                    mInterfaceClassLoader,
                    mShadowRemoteViewCreatorProvider
            )
        }
    }

    override fun getHostActivityDelegate(aClass: Class<out HostActivityDelegator>): HostActivityDelegate {
        return ShadowActivityDelegate(this)
    }

    override fun getHostServiceDelegate(aClass: Class<out HostServiceDelegator>): HostServiceDelegate {
        return ServiceContainerReuseDelegate(this)
    }

    override fun getHostContentProviderDelegate(): HostContentProviderDelegate {
        return ShadowContentProviderDelegate(mPluginContentProviderManager)
    }

    override fun getUriParseDelegate(): UriParseDelegate {
        return mPluginContentProviderManager
    }

    override fun inject(delegate: ShadowDelegate, partKey: String) {
        mLock.withLock {
            val pluginParts = mPluginPartsMap[partKey]
            if (pluginParts == null) {
                throw IllegalStateException("partKey==${partKey}在map中找不到。此时map：${mPluginPartsMap}")
            } else {
                delegate.inject(pluginParts.packageManager)
                delegate.inject(pluginParts.application)
                delegate.inject(pluginParts.classLoader)
                delegate.inject(pluginParts.resources)
                delegate.inject(mExceptionReporter)
                delegate.inject(mComponentManager)
                delegate.inject(mShadowRemoteViewCreatorProvider)
            }
        }
    }

    private inner class ShadowRemoteViewCreatorProviderImpl: ShadowRemoteViewCreatorProvider {
        override fun createRemoteViewCreator(context: Context): ShadowRemoteViewCreator {
            return ShadowRemoteViewCreatorImp(context, this@ShadowPluginLoader)
        }

    }

    private fun InstalledApk.getLoadParameters(): LoadParameters {
        val parcel = Parcel.obtain()
        parcel.unmarshall(parcelExtras, 0, parcelExtras.size)
        parcel.setDataPosition(0)
        val loadParameters = LoadParameters(parcel)
        parcel.recycle()
        return loadParameters
    }

    private fun isUiThread(): Boolean {
        return Looper.myLooper() == Looper.getMainLooper()
    }
}
