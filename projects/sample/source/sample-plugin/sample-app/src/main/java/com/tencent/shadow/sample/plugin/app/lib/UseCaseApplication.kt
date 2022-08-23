package com.tencent.shadow.sample.plugin.app.lib

import android.app.Application
import com.tencent.shadow.sample.plugin.app.lib.UseCaseApplication
import com.tencent.shadow.sample.plugin.app.lib.gallery.cases.UseCaseManager
import com.tencent.shadow.sample.plugin.app.lib.gallery.cases.entity.UseCaseCategory
import com.tencent.shadow.sample.plugin.app.lib.gallery.cases.entity.UseCase
import com.tencent.shadow.sample.plugin.app.lib.usecases.activity.*
import com.tencent.shadow.sample.plugin.app.lib.usecases.context.ActivityContextSubDirTestActivity
import com.tencent.shadow.sample.plugin.app.lib.usecases.context.ApplicationContextSubDirTestActivity
import com.tencent.shadow.sample.plugin.app.lib.usecases.dialog.TestDialogActivity
import com.tencent.shadow.sample.plugin.app.lib.usecases.webview.WebViewActivity
import com.tencent.shadow.sample.plugin.app.lib.usecases.receiver.TestDynamicReceiverActivity
import com.tencent.shadow.sample.plugin.app.lib.usecases.fragment.TestDynamicFragmentActivity
import com.tencent.shadow.sample.plugin.app.lib.usecases.fragment.TestXmlFragmentActivity
import com.tencent.shadow.sample.plugin.app.lib.usecases.fragment.TestDialogFragmentActivity
import com.tencent.shadow.sample.plugin.app.lib.usecases.host_communication.PluginUseHostClassActivity
import com.tencent.shadow.sample.plugin.app.lib.usecases.packagemanager.TestPackageManagerActivity
import com.tencent.shadow.sample.plugin.app.lib.usecases.provider.TestDBContentProviderActivity
import com.tencent.shadow.sample.plugin.app.lib.usecases.provider.TestFileProviderActivity
import com.tencent.shadow.sample.plugin.app.lib.usecases.receiver.TestReceiverActivity
import java.lang.RuntimeException

class UseCaseApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        intence = this
        initCase()
    }

    companion object {
        @JvmField
        var intence: Application? = null
        private fun initCase() {
            if (UseCaseManager.sInit) {
                throw RuntimeException("不能重复调用init")
            }
            UseCaseManager.sInit = true
            val activityCategory = UseCaseCategory(
                "Activity测试用例", arrayOf(
                    TestActivityOnCreate.Case(),
                    TestActivityReCreate.Case(),
                    TestActivityReCreateBySystem.Case(),
                    TestActivityOrientation.Case(),
                    TestActivityWindowSoftMode.Case(),
                    TestActivitySetTheme.Case(),
                    TestActivityOptionMenu.Case(),
                    WebViewActivity.Case()
                )
            )
            UseCaseManager.useCases.add(activityCategory)
            val broadcastReceiverCategory = UseCaseCategory(
                "广播测试用例", arrayOf(
                    TestReceiverActivity.Case(),
                    TestDynamicReceiverActivity.Case()
                )
            )
            UseCaseManager.useCases.add(broadcastReceiverCategory)
            val providerCategory = UseCaseCategory(
                "ContentProvider测试用例", arrayOf(
                    TestDBContentProviderActivity.Case(),
                    TestFileProviderActivity.Case()
                )
            )
            UseCaseManager.useCases.add(providerCategory)
            val fragmentCategory = UseCaseCategory(
                "fragment测试用例", arrayOf(
                    TestDynamicFragmentActivity.Case(),
                    TestXmlFragmentActivity.Case(),
                    TestDialogFragmentActivity.Case()
                )
            )
            UseCaseManager.useCases.add(fragmentCategory)
            val dialogCategory = UseCaseCategory(
                "Dialog测试用例", arrayOf<UseCase>(
                    TestDialogActivity.Case()
                )
            )
            UseCaseManager.useCases.add(dialogCategory)
            val packageManagerCategory = UseCaseCategory(
                "PackageManager测试用例", arrayOf<UseCase>(
                    TestPackageManagerActivity.Case()
                )
            )
            UseCaseManager.useCases.add(packageManagerCategory)
            val contextCategory = UseCaseCategory(
                "Context相关测试用例", arrayOf(
                    ActivityContextSubDirTestActivity.Case(),
                    ApplicationContextSubDirTestActivity.Case()
                )
            )
            UseCaseManager.useCases.add(contextCategory)
            val communicationCategory = UseCaseCategory(
                "插件和宿主通信相关测试用例", arrayOf<UseCase>(
                    PluginUseHostClassActivity.Case()
                )
            )
            UseCaseManager.useCases.add(communicationCategory)
        }
    }
}