package com.tencent.shadow.core.loader.delegates

import android.content.ContentValues
import android.content.res.Configuration
import android.database.Cursor
import android.net.Uri

import com.tencent.shadow.core.loader.managers.PluginContentProviderManager
import com.tencent.shadow.runtime.container.HostContentProviderDelegate
import com.tencent.shadow.runtime.container.HostContentProviderDelegator

class ShadowContentProviderDelegate(private val mProviderManager: PluginContentProviderManager) : ShadowDelegate(), HostContentProviderDelegate {

    private var mDelegator: HostContentProviderDelegator? = null

    override fun onConfigurationChanged(newConfig: Configuration) {
        mProviderManager.getAllContentProvider().forEach {
            it.onConfigurationChanged(newConfig)
        }
    }

    override fun onLowMemory() {
        mProviderManager.getAllContentProvider().forEach {
            it.onLowMemory()
        }
    }

    override fun onTrimMemory(level: Int) {
        mProviderManager.getAllContentProvider().forEach {
            it.onTrimMemory(level)
        }
    }

    override fun onCreate(): Boolean {
        return true
    }


    override fun setDelegator(delegator: HostContentProviderDelegator) {
        mDelegator = delegator
    }

    override fun query(uri: Uri, projection: Array<String>?, selection: String?, selectionArgs: Array<String>?, sortOrder: String?): Cursor? {
        val pluginUri = mProviderManager.convert2PluginUri(uri)
        return mProviderManager.getPluginContentProvider(pluginUri.authority!!)!!.query(pluginUri, projection, selection, selectionArgs, sortOrder)
    }

    override fun getType(uri: Uri): String? {
        val pluginUri = mProviderManager.convert2PluginUri(uri)
        return mProviderManager.getPluginContentProvider(pluginUri.authority!!)!!.getType(pluginUri)
    }

    override fun insert(uri: Uri, values: ContentValues): Uri? {
        val pluginUri = mProviderManager.convert2PluginUri(uri)
        return mProviderManager.getPluginContentProvider(pluginUri.authority!!)!!.insert(pluginUri, values)
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        val pluginUri = mProviderManager.convert2PluginUri(uri)
        return mProviderManager.getPluginContentProvider(pluginUri.authority!!)!!.delete(pluginUri, selection, selectionArgs)
    }

    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<String>?): Int {
        val pluginUri = mProviderManager.convert2PluginUri(uri)
        return mProviderManager.getPluginContentProvider(pluginUri.authority!!)!!.update(pluginUri, values, selection, selectionArgs)
    }

    override fun bulkInsert(uri: Uri, values: Array<ContentValues>?): Int {
        val pluginUri = mProviderManager.convert2PluginUri(uri)
        return mProviderManager.getPluginContentProvider(pluginUri.authority!!)!!.bulkInsert(pluginUri, values)
    }
}
