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

import android.annotation.TargetApi
import android.content.ContentValues
import android.content.res.Configuration
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.CancellationSignal
import android.os.ParcelFileDescriptor

import com.tencent.shadow.core.loader.managers.PluginContentProviderManager
import com.tencent.shadow.core.runtime.container.HostContentProviderDelegate

class ShadowContentProviderDelegate(private val mProviderManager: PluginContentProviderManager) :
    ShadowDelegate(), HostContentProviderDelegate {

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


    override fun query(
        uri: Uri,
        projection: Array<String>?,
        selection: String?,
        selectionArgs: Array<String>?,
        sortOrder: String?
    ): Cursor? {
        val pluginUri = mProviderManager.convert2PluginUri(uri)
        return mProviderManager.getPluginContentProvider(pluginUri.authority!!)!!
            .query(pluginUri, projection, selection, selectionArgs, sortOrder)
    }

    override fun getType(uri: Uri): String? {
        val pluginUri = mProviderManager.convert2PluginUri(uri)
        return mProviderManager.getPluginContentProvider(pluginUri.authority!!)!!.getType(pluginUri)
    }

    override fun insert(uri: Uri, values: ContentValues): Uri? {
        val pluginUri = mProviderManager.convert2PluginUri(uri)
        return mProviderManager.getPluginContentProvider(pluginUri.authority!!)!!
            .insert(pluginUri, values)
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        val pluginUri = mProviderManager.convert2PluginUri(uri)
        return mProviderManager.getPluginContentProvider(pluginUri.authority!!)!!
            .delete(pluginUri, selection, selectionArgs)
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<String>?
    ): Int {
        val pluginUri = mProviderManager.convert2PluginUri(uri)
        return mProviderManager.getPluginContentProvider(pluginUri.authority!!)!!
            .update(pluginUri, values, selection, selectionArgs)
    }

    override fun bulkInsert(uri: Uri, values: Array<out ContentValues>): Int {
        val pluginUri = mProviderManager.convert2PluginUri(uri)
        return mProviderManager.getPluginContentProvider(pluginUri.authority!!)!!
            .bulkInsert(pluginUri, values)
    }

    override fun call(method: String, arg: String?, extras: Bundle): Bundle? {
        val pluginUri = mProviderManager.convert2PluginUri(extras)
        return mProviderManager.getPluginContentProvider(pluginAuthority = pluginUri.authority!!)!!
            .call(method, arg, extras)
    }

    override fun openFile(uri: Uri, mode: String): ParcelFileDescriptor? {
        val pluginUri = mProviderManager.convert2PluginUri(uri)
        return mProviderManager.getPluginContentProvider(pluginUri.authority!!)!!
            .openFile(pluginUri, mode)
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    override fun openFile(
        uri: Uri,
        mode: String,
        signal: CancellationSignal?
    ): ParcelFileDescriptor? {
        val pluginUri = mProviderManager.convert2PluginUri(uri)
        return mProviderManager.getPluginContentProvider(pluginUri.authority!!)!!
            .openFile(pluginUri, mode, signal)
    }
}
