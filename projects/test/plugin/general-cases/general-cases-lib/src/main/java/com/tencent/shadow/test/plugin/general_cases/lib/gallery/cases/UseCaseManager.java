package com.tencent.shadow.test.plugin.general_cases.lib.gallery.cases;

import com.tencent.shadow.test.plugin.general_cases.lib.gallery.cases.entity.UseCase;
import com.tencent.shadow.test.plugin.general_cases.lib.gallery.cases.entity.UseCaseCategory;
import com.tencent.shadow.test.plugin.general_cases.lib.gallery.util.PluginChecker;
import com.tencent.shadow.test.plugin.general_cases.lib.usecases.activity.TestActivityOnCreate;
import com.tencent.shadow.test.plugin.general_cases.lib.usecases.activity.TestActivityOrientation;
import com.tencent.shadow.test.plugin.general_cases.lib.usecases.activity.TestActivityReCreate;
import com.tencent.shadow.test.plugin.general_cases.lib.usecases.activity.TestActivityReCreateBySystem;
import com.tencent.shadow.test.plugin.general_cases.lib.usecases.activity.TestActivityWindowSoftMode;
import com.tencent.shadow.test.plugin.general_cases.lib.usecases.activity.TestCallingActivity;
import com.tencent.shadow.test.plugin.general_cases.lib.usecases.context.ActivityContextSubDirTestActivity;
import com.tencent.shadow.test.plugin.general_cases.lib.usecases.context.ApplicationContextSubDirTestActivity;
import com.tencent.shadow.test.plugin.general_cases.lib.usecases.context.ServiceContextSubDirTestActivity;
import com.tencent.shadow.test.plugin.general_cases.lib.usecases.dialog.TestDialogActivity;
import com.tencent.shadow.test.plugin.general_cases.lib.usecases.fragment.TestDynamicFragmentActivity;
import com.tencent.shadow.test.plugin.general_cases.lib.usecases.fragment.TestXmlFragmentActivity;
import com.tencent.shadow.test.plugin.general_cases.lib.usecases.interfaces.TestHostInterfaceActivity;
import com.tencent.shadow.test.plugin.general_cases.lib.usecases.packagemanager.TestPackageManagerActivity;
import com.tencent.shadow.test.plugin.general_cases.lib.usecases.provider.TestDBContentProviderActivity;
import com.tencent.shadow.test.plugin.general_cases.lib.usecases.provider.TestFileProviderActivity;
import com.tencent.shadow.test.plugin.general_cases.lib.usecases.receiver.TestDynamicReceiverActivity;
import com.tencent.shadow.test.plugin.general_cases.lib.usecases.receiver.TestReceiverActivity;
import com.tencent.shadow.test.plugin.general_cases.lib.usecases.service.TestStartServiceActivity;
import com.tencent.shadow.test.plugin.general_cases.lib.usecases.view.TestViewConstructorCache;

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
                new TestCallingActivity.Case()
        });
        useCases.add(activityCategory);


        UseCaseCategory serviceCategory = new UseCaseCategory("Service测试用例",new UseCase[]{
                new TestStartServiceActivity.Case(),
        });
        useCases.add(serviceCategory);


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
                new TestXmlFragmentActivity.Case()
        });
        useCases.add(fragmentCategory);

        UseCaseCategory dialogCategory = new UseCaseCategory("Dialog测试用例",new UseCase[]{
                new TestDialogActivity.Case(),
        });
        useCases.add(dialogCategory);

        UseCaseCategory viewCategory = new UseCaseCategory("View测试用例",new UseCase[]{
                new TestViewConstructorCache.Case(),
        });
        useCases.add(viewCategory);


        UseCaseCategory packageManagerCategory = new UseCaseCategory("PackageManager测试用例",new UseCase[]{
                new TestPackageManagerActivity.Case(),
        });
        useCases.add(packageManagerCategory);


        if (PluginChecker.isPluginMode()) {
            UseCaseCategory hostInterfaceCategory = new UseCaseCategory("宿主接口调用测试用例", new UseCase[]{
                    new TestHostInterfaceActivity.Case(),
            });
            useCases.add(hostInterfaceCategory);
        }


        UseCaseCategory contextCategory = new UseCaseCategory("Context相关测试用例", new UseCase[]{
                new ActivityContextSubDirTestActivity.Case(),
                new ServiceContextSubDirTestActivity.Case(),
                new ApplicationContextSubDirTestActivity.Case(),
        });
        useCases.add(contextCategory);
    }


}
