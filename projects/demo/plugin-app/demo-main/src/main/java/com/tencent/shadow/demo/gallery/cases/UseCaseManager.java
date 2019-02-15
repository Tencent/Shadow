package com.tencent.shadow.demo.gallery.cases;

import android.os.Bundle;

import com.tencent.shadow.demo.gallery.cases.entity.UseCase;
import com.tencent.shadow.demo.gallery.cases.entity.UseCaseCategory;
import com.tencent.shadow.demo.usecases.activity.TestActivityOnCreate;
import com.tencent.shadow.demo.usecases.activity.TestActivityReCreate;
import com.tencent.shadow.demo.usecases.activity.TestActivityReCreateBySystem;
import com.tencent.shadow.demo.usecases.dialog.TestDialogActivity;
import com.tencent.shadow.demo.usecases.fragment.TestDynamicFragmentActivity;
import com.tencent.shadow.demo.usecases.fragment.TestXmlFragmentActivity;
import com.tencent.shadow.demo.usecases.packagemanager.TestPackageManagerActivity;
import com.tencent.shadow.demo.usecases.provider.TestDBContentProviderActivity;
import com.tencent.shadow.demo.usecases.receiver.TestDynamicReceiverActivity;
import com.tencent.shadow.demo.usecases.receiver.TestReceiverActivity;
import com.tencent.shadow.demo.usecases.service.TestStartServiceActivity;

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

        UseCaseCategory activityCategory = new UseCaseCategory(Case_Activity.CATEGORY_ID, "Activity测试用例");
        useCases.add(activityCategory);
        activityCategory.caseList.add(new UseCase(Case_Activity.CASE_ONCREATE,
                "生命周期测试", "测试Activity的生命周期方法是否正确回调", TestActivityOnCreate.class));
        activityCategory.caseList.add(new UseCase(Case_Activity.CASE_RECREATE,
                "ReCreate", "测试Activity的调用ReCreate是否工作正常", TestActivityReCreate.class));

        UseCase useCase = new UseCase(Case_Activity.CASE_RECREATE_BY_SYTEM, "ReCreateBySystem",
                "不保留活动进行测试，需要手动到开发者模式中开启", TestActivityReCreateBySystem.class);
        useCase.bundle = new Bundle();
        useCase.bundle.putString("url", "https://www.baidu.com");

        activityCategory.caseList.add(useCase);



        UseCaseCategory serviceCategory = new UseCaseCategory(Case_Service.CATEGORY_ID, "Service测试用例");
        useCases.add(serviceCategory);
        serviceCategory.caseList.add(new UseCase(Case_Service.CASE_START_SERVICE,
                "启动Service", "测试startService,bindService,stopService,unBindService等调用", TestStartServiceActivity.class));


        UseCaseCategory broadcastReceiverCategory = new UseCaseCategory(Case_BroadcastReceiver.CATEGORY_ID, "广播测试用例");
        useCases.add(broadcastReceiverCategory);
        broadcastReceiverCategory.caseList.add(new UseCase(Case_BroadcastReceiver.CASE_RECEIVE,
                "静态广播测试", "测试静态广播的发送和接收是否工作正常", TestReceiverActivity.class));
        broadcastReceiverCategory.caseList.add(new UseCase(Case_BroadcastReceiver.CASE_RECEIVE_DYNAMIC,
                "动态广播测试", "测试动态广播的发送和接收是否工作正常", TestDynamicReceiverActivity.class));



        UseCaseCategory providerCategory = new UseCaseCategory(Case_Provider.CATEGORY_ID, "ContentProvider测试用例");
        useCases.add(providerCategory);
        providerCategory.caseList.add(new UseCase(Case_Provider.CASE_DB,
                "ContentProvider DB相关测试", "测试通过ContentProvider来操作数据库", TestDBContentProviderActivity.class));


        UseCaseCategory fragmentCategory = new UseCaseCategory(Case_Fragment.CATEGORY_ID, "fragment测试用例");
        useCases.add(fragmentCategory);
        fragmentCategory.caseList.add(new UseCase(Case_Fragment.CASE_FRAGMENT_DYNAMIC,
                "代码添加fragment相关测试", "测试通过代码添加一个fragment", TestDynamicFragmentActivity.class));
        fragmentCategory.caseList.add(new UseCase(Case_Fragment.CASE_FRAGMENT_XML,
                "xml中使用fragment相关测试", "测试在Activity现实xml中定义的fragment", TestXmlFragmentActivity.class));

        UseCaseCategory dialogCategory = new UseCaseCategory(Case_Dialog.CATEGORY_ID, "Dialog测试用例");
        useCases.add(dialogCategory);
        dialogCategory.caseList.add(new UseCase(Case_Dialog.CASE_SHOW_DIALOG,
                "Dialog 相关测试", "测试show Dialog", TestDialogActivity.class));


        UseCaseCategory packageManagerCategory = new UseCaseCategory(Case_PackageManager.CATEGORY_ID, "PackageManager测试用例");
        useCases.add(packageManagerCategory);
        packageManagerCategory.caseList.add(new UseCase(Case_PackageManager.CASE_PACKAGEMANAGER,
                "PackageManager调用测试", "测试PackageManager相关api的调用，确保插件调用相关api时可以正确获取到插件相关的信息", TestPackageManagerActivity.class));
    }

    public static UseCase findTestCaseById(int caseId) {
        for (UseCaseCategory testCategory : useCases) {
            for (UseCase useCase : testCategory.caseList) {
                if (useCase.id == caseId) {
                    return useCase;
                }
            }
        }
        return null;
    }


    private static class Case_Activity {
        public final static int CATEGORY_ID = 1;

        public final static int CASE_ONCREATE = 10000;
        public final static int CASE_RECREATE = 10001;
        public final static int CASE_RECREATE_BY_SYTEM = 10002;
    }

    private static class Case_Service {
        public final static int CATEGORY_ID = 2;

        public final static int CASE_START_SERVICE = 20000;
    }

    private static class Case_BroadcastReceiver {
        public final static int CATEGORY_ID = 3;

        public final static int CASE_RECEIVE = 30000;
        public final static int CASE_RECEIVE_DYNAMIC = 30001;
    }

    private static class Case_Provider {
        public final static int CATEGORY_ID = 4;

        public final static int CASE_DB = 40000;
    }

    private static class Case_Fragment {
        public final static int CATEGORY_ID = 5;

        public final static int CASE_FRAGMENT_DYNAMIC = 50000;
        public final static int CASE_FRAGMENT_XML = 50001;
    }

    private static class Case_Dialog {
        public final static int CATEGORY_ID = 6;

        public final static int CASE_SHOW_DIALOG = 60000;
    }

    private static class Case_PackageManager {
        public final static int CATEGORY_ID = 7;

        public final static int CASE_PACKAGEMANAGER = 60000;
    }

}
