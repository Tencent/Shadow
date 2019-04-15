package com.tencent.shadow.demo.gallery.cases;

import com.tencent.shadow.demo.gallery.cases.entity.UseCase;
import com.tencent.shadow.demo.gallery.cases.entity.UseCaseCategory;
import com.tencent.shadow.demo.usecases.activity.TestActivityOnCreate;
import com.tencent.shadow.demo.usecases.activity.TestActivityOrientation;
import com.tencent.shadow.demo.usecases.activity.TestActivityReCreate;
import com.tencent.shadow.demo.usecases.activity.TestActivityReCreateBySystem;
import com.tencent.shadow.demo.usecases.activity.TestActivityWindowSoftMode;
import com.tencent.shadow.demo.usecases.activity.TestCallingActivity;
import com.tencent.shadow.demo.usecases.dialog.TestDialogActivity;
import com.tencent.shadow.demo.usecases.fragment.TestDynamicFragmentActivity;
import com.tencent.shadow.demo.usecases.fragment.TestXmlFragmentActivity;
import com.tencent.shadow.demo.usecases.packagemanager.TestPackageManagerActivity;
import com.tencent.shadow.demo.usecases.provider.TestDBContentProviderActivity;
import com.tencent.shadow.demo.usecases.provider.TestFileProviderActivity;
import com.tencent.shadow.demo.usecases.receiver.TestDynamicReceiverActivity;
import com.tencent.shadow.demo.usecases.receiver.TestReceiverActivity;
import com.tencent.shadow.demo.usecases.service.TestStartServiceActivity;
import com.tencent.shadow.demo.usecases.view.TestViewConstructorCache;

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
    }


}
