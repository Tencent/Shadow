package com.tencent.shadow.loader.remoteview

import android.content.Context
import com.tencent.shadow.loader.ShadowPluginLoader
import com.tencent.shadow.runtime.remoteview.ShadowRemoteViewCreator
import com.tencent.shadow.runtime.remoteview.ShadowRemoteViewCreatorProvider

class ShadowRemoteViewCreatorProviderImpl(private val shadowPluginLoader: ShadowPluginLoader): ShadowRemoteViewCreatorProvider {

    override fun createRemoteViewCreator(context: Context): ShadowRemoteViewCreator {
        return ShadowRemoteViewCreatorImp(context, shadowPluginLoader)
    }
}