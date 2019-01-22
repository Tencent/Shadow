package com.tencent.shadow.demo.gallery.cases;

import android.os.Bundle;

import com.tencent.shadow.demo.gallery.cases.entity.UseCase;
import com.tencent.shadow.demo.gallery.cases.entity.UseCaseCategory;
import com.tencent.shadow.demo.usecases.activity.TestActivityOnCreate;
import com.tencent.shadow.demo.usecases.activity.TestActivityReCreate;
import com.tencent.shadow.demo.usecases.activity.TestActivityReCreateBySystem;
import com.tencent.shadow.demo.usecases.provider.TestDBContentProviderActivity;
import com.tencent.shadow.demo.usecases.provider.TestFileProviderActivity;
import com.tencent.shadow.demo.usecases.receiver.MyReceiver;
import com.tencent.shadow.demo.usecases.service.MyLocalService;

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
                "startService", "测试startService的方式启动Service", MyLocalService.class));
        serviceCategory.caseList.add(new UseCase(Case_Service.CASE_BIND_SERVICE,
                "bindService", "测试bindService的方式启动Service", MyLocalService.class));


        UseCaseCategory broadcastReceiverCategory = new UseCaseCategory(Case_BroadcastReceiver.CATEGORY_ID, "广播测试用例");
        useCases.add(broadcastReceiverCategory);
        broadcastReceiverCategory.caseList.add(new UseCase(Case_BroadcastReceiver.CASE_RECEIVE,
                "动态广播测试", "测试动态广播的发送和接收是否工作正常", MyReceiver.class));



        UseCaseCategory providerCategory = new UseCaseCategory(Case_Provider.CATEGORY_ID, "ContentProvider测试用例");
        useCases.add(providerCategory);
        providerCategory.caseList.add(new UseCase(Case_Provider.CASE_DB,
                "ContentProvider DB相关测试", "测试通过ContentProvider来操作数据库", TestDBContentProviderActivity.class));
        providerCategory.caseList.add(new UseCase(Case_Provider.CASE_FILE,
                "FileProvider相关测试", "通过使用系统相机拍照来测试FileProvider", TestFileProviderActivity.class));
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
        public final static int CASE_BIND_SERVICE = 20001;
    }

    private static class Case_BroadcastReceiver {
        public final static int CATEGORY_ID = 3;

        public final static int CASE_RECEIVE = 30000;
    }

    private static class Case_Provider {
        public final static int CATEGORY_ID = 4;

        public final static int CASE_DB = 40000;
        public final static int CASE_FILE = 40001;
    }

}
