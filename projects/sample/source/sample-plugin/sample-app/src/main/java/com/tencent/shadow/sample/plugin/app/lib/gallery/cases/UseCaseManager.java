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

package com.tencent.shadow.sample.plugin.app.lib.gallery.cases;

import com.tencent.shadow.sample.plugin.app.lib.gallery.cases.entity.UseCase;
import com.tencent.shadow.sample.plugin.app.lib.gallery.cases.entity.UseCaseCategory;
import com.tencent.shadow.sample.plugin.app.lib.usecases.activity.TestActivityOnCreate;
import com.tencent.shadow.sample.plugin.app.lib.usecases.activity.TestActivityOptionMenu;
import com.tencent.shadow.sample.plugin.app.lib.usecases.activity.TestActivityOrientation;
import com.tencent.shadow.sample.plugin.app.lib.usecases.activity.TestActivityReCreate;
import com.tencent.shadow.sample.plugin.app.lib.usecases.activity.TestActivityReCreateBySystem;
import com.tencent.shadow.sample.plugin.app.lib.usecases.activity.TestActivitySetTheme;
import com.tencent.shadow.sample.plugin.app.lib.usecases.activity.TestActivityWindowSoftMode;
import com.tencent.shadow.sample.plugin.app.lib.usecases.activity.TestAppCompatActivityOnCreate;
import com.tencent.shadow.sample.plugin.app.lib.usecases.context.ActivityContextSubDirTestActivity;
import com.tencent.shadow.sample.plugin.app.lib.usecases.context.ApplicationContextSubDirTestActivity;
import com.tencent.shadow.sample.plugin.app.lib.usecases.dialog.TestDialogActivity;
import com.tencent.shadow.sample.plugin.app.lib.usecases.fragment.TestDialogFragmentActivity;
import com.tencent.shadow.sample.plugin.app.lib.usecases.fragment.TestDynamicFragmentActivity;
import com.tencent.shadow.sample.plugin.app.lib.usecases.fragment.TestXmlFragmentActivity;
import com.tencent.shadow.sample.plugin.app.lib.usecases.host_communication.PluginUseHostClassActivity;
import com.tencent.shadow.sample.plugin.app.lib.usecases.packagemanager.TestPackageManagerActivity;
import com.tencent.shadow.sample.plugin.app.lib.usecases.provider.TestDBContentProviderActivity;
import com.tencent.shadow.sample.plugin.app.lib.usecases.provider.TestFileProviderActivity;
import com.tencent.shadow.sample.plugin.app.lib.usecases.receiver.TestDynamicReceiverActivity;
import com.tencent.shadow.sample.plugin.app.lib.usecases.receiver.TestReceiverActivity;
import com.tencent.shadow.sample.plugin.app.lib.usecases.webview.WebViewActivity;

import java.util.ArrayList;
import java.util.List;

public class UseCaseManager {

    public static List<UseCaseCategory> useCases = new ArrayList<>();

    private static boolean sInit;

    public static void initCase() {

        if (sInit) {
            throw new RuntimeException("不能重复调用init");
        }

        sInit = true;

        UseCaseCategory activityCategory = new UseCaseCategory("Activity测试用例",new UseCase[]{
                new TestActivityOnCreate.Case(),
                new TestActivityReCreate.Case(),
                new TestActivityReCreateBySystem.Case(),
                new TestActivityOrientation.Case(),
                new TestActivityWindowSoftMode.Case(),
                new TestActivitySetTheme.Case(),
                new TestActivityOptionMenu.Case(),
                new TestAppCompatActivityOnCreate.Case(),
                new WebViewActivity.Case()
        });
        useCases.add(activityCategory);

        UseCaseCategory broadcastReceiverCategory = new UseCaseCategory("广播测试用例",new UseCase[]{
                new TestReceiverActivity.Case(),
                new TestDynamicReceiverActivity.Case()
        });
        useCases.add(broadcastReceiverCategory);


        UseCaseCategory providerCategory = new UseCaseCategory( "ContentProvider测试用例",new UseCase[]{
                new TestDBContentProviderActivity.Case(),
                new TestFileProviderActivity.Case()
        });
        useCases.add(providerCategory);


        UseCaseCategory fragmentCategory = new UseCaseCategory("fragment测试用例",new UseCase[]{
                new TestDynamicFragmentActivity.Case(),
                new TestXmlFragmentActivity.Case(),
                new TestDialogFragmentActivity.Case()
        });
        useCases.add(fragmentCategory);

        UseCaseCategory dialogCategory = new UseCaseCategory("Dialog测试用例",new UseCase[]{
                new TestDialogActivity.Case(),
        });
        useCases.add(dialogCategory);

        UseCaseCategory packageManagerCategory = new UseCaseCategory("PackageManager测试用例",new UseCase[]{
                new TestPackageManagerActivity.Case(),
        });
        useCases.add(packageManagerCategory);


        UseCaseCategory contextCategory = new UseCaseCategory("Context相关测试用例", new UseCase[]{
                new ActivityContextSubDirTestActivity.Case(),
                new ApplicationContextSubDirTestActivity.Case(),
        });
        useCases.add(contextCategory);

        UseCaseCategory communicationCategory = new UseCaseCategory("插件和宿主通信相关测试用例", new UseCase[]{
                new PluginUseHostClassActivity.Case(),
        });
        useCases.add(communicationCategory);
    }


}
