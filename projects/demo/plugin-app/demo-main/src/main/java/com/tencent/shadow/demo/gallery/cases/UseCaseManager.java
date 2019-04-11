package com.tencent.shadow.demo.gallery.cases;

import android.os.Bundle;

import com.tencent.shadow.demo.gallery.cases.entity.UseCase;
import com.tencent.shadow.demo.gallery.cases.entity.UseCaseCategory;
import com.tencent.shadow.demo.usecases.activity.TestActivityOnCreate;
import com.tencent.shadow.demo.usecases.activity.TestActivityOrientation;
import com.tencent.shadow.demo.usecases.activity.TestActivityReCreate;
import com.tencent.shadow.demo.usecases.activity.TestActivityReCreateBySystem;
import com.tencent.shadow.demo.usecases.activity.TestActivityWindowSoftMode;
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

        UseCaseCategory activityCategory = new UseCaseCategory("Activity测试用例");
        useCases.add(activityCategory);
        activityCategory.caseList.add(new UseCase("生命周期测试", "测试Activity的生命周期方法是否正确回调", TestActivityOnCreate.class));
        activityCategory.caseList.add(new UseCase("ReCreate", "测试Activity的调用ReCreate是否工作正常", TestActivityReCreate.class));

        UseCase useCase = new UseCase("ReCreateBySystem", "不保留活动进行测试，需要手动到开发者模式中开启", TestActivityReCreateBySystem.class);
        useCase.bundle = new Bundle();
        useCase.bundle.putString("url", "https://www.baidu.com");

        activityCategory.caseList.add(useCase);
        activityCategory.caseList.add(new UseCase("横竖屏切换测试","测试横竖屏切换时，Activity的生命周期变化是否和AndroidManifest.xml中配置的config相关", TestActivityOrientation.class));

        activityCategory.caseList.add(new UseCase("windowSoftInputMode测试",
                "测试插件中设置windowSoftInputMode是否生效", TestActivityWindowSoftMode.class));




        UseCaseCategory serviceCategory = new UseCaseCategory("Service测试用例");
        useCases.add(serviceCategory);
        serviceCategory.caseList.add(new UseCase(
                "启动Service", "测试startService,bindService,stopService,unBindService等调用", TestStartServiceActivity.class));


        UseCaseCategory broadcastReceiverCategory = new UseCaseCategory("广播测试用例");
        useCases.add(broadcastReceiverCategory);
        broadcastReceiverCategory.caseList.add(new UseCase("静态广播测试", "测试静态广播的发送和接收是否工作正常", TestReceiverActivity.class));
        broadcastReceiverCategory.caseList.add(new UseCase("动态广播测试", "测试动态广播的发送和接收是否工作正常", TestDynamicReceiverActivity.class));



        UseCaseCategory providerCategory = new UseCaseCategory( "ContentProvider测试用例");
        useCases.add(providerCategory);
        providerCategory.caseList.add(new UseCase("ContentProvider DB相关测试", "测试通过ContentProvider来操作数据库", TestDBContentProviderActivity.class));
        providerCategory.caseList.add(new UseCase("FileProvider相关测试", "通过使用系统相机拍照来测试FileProvider", TestFileProviderActivity.class));


        UseCaseCategory fragmentCategory = new UseCaseCategory("fragment测试用例");
        useCases.add(fragmentCategory);
        fragmentCategory.caseList.add(new UseCase("代码添加fragment相关测试", "测试通过代码添加一个fragment", TestDynamicFragmentActivity.class));
        fragmentCategory.caseList.add(new UseCase("xml中使用fragment相关测试", "测试在Activity现实xml中定义的fragment", TestXmlFragmentActivity.class));

        UseCaseCategory dialogCategory = new UseCaseCategory("Dialog测试用例");
        useCases.add(dialogCategory);
        dialogCategory.caseList.add(new UseCase("Dialog 相关测试", "测试show Dialog", TestDialogActivity.class));

        UseCaseCategory viewCategory = new UseCaseCategory("View测试用例");
        useCases.add(viewCategory);
        viewCategory.caseList.add(new UseCase("同名View构造器缓存冲突测试", "宿主和插件具有同名View应该都能正常加载各自的版本", TestViewConstructorCache.class));


        UseCaseCategory packageManagerCategory = new UseCaseCategory("PackageManager测试用例");
        useCases.add(packageManagerCategory);
        packageManagerCategory.caseList.add(new UseCase("PackageManager调用测试", "测试PackageManager相关api的调用，确保插件调用相关api时可以正确获取到插件相关的信息", TestPackageManagerActivity.class));
    }


}
