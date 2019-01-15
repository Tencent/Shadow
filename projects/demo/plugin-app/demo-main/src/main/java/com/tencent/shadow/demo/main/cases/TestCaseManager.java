package com.tencent.shadow.demo.main.cases;

import com.tencent.shadow.demo.activity.TestActivityOnCreate;
import com.tencent.shadow.demo.activity.TestActivityReCreate;
import com.tencent.shadow.demo.main.cases.entity.TestCase;
import com.tencent.shadow.demo.main.cases.entity.TestCategory;
import com.tencent.shadow.demo.receiver.MyReceiver;
import com.tencent.shadow.demo.service.MyLocalService;

import java.util.ArrayList;
import java.util.List;

public class TestCaseManager {

    public static List<TestCategory> testCases = new ArrayList<>();

    private static boolean sInit;

    public static void initCase() {

        if (sInit) {
            throw new RuntimeException("不能重复调用init");
        }

        sInit = true;

        TestCategory activityCategory = new TestCategory(Case_Activity.CATEGORY_ID, "Activity测试用例");
        testCases.add(activityCategory);
        activityCategory.caseList.add(new TestCase(Case_Activity.CASE_ONCREATE,
                "生命周期测试", "测试Activity的生命周期方法是否正确回调", TestActivityOnCreate.class));
        activityCategory.caseList.add(new TestCase(Case_Activity.CASE_RECREATE,
                "ReCreate", "测试Activity的调用ReCreate是否工作正常", TestActivityReCreate.class));


        TestCategory serviceCategory = new TestCategory(Case_Service.CATEGORY_ID, "Service测试用例");
        testCases.add(serviceCategory);
        serviceCategory.caseList.add(new TestCase(Case_Service.CASE_START_SERVICE,
                "startService", "测试startService的方式启动Service", MyLocalService.class));
        serviceCategory.caseList.add(new TestCase(Case_Service.CASE_BIND_SERVICE,
                "bindService", "测试bindService的方式启动Service", MyLocalService.class));


        TestCategory broadcastReceiverCategory = new TestCategory(Case_BroadcastReceiver.CATEGORY_ID, "广播测试用例");
        testCases.add(broadcastReceiverCategory);
        broadcastReceiverCategory.caseList.add(new TestCase(Case_BroadcastReceiver.CASE_RECEIVE,
                "动态广播测试", "测试动态广播的发送和接收是否工作正常", MyReceiver.class));


        serviceCategory.caseList.add(new TestCase(Case_BroadcastReceiver.CASE_RECEIVE,
                "动态广播测试", "测试动态广播的发送和接收是否工作正常", MyReceiver.class));
        serviceCategory.caseList.add(new TestCase(Case_BroadcastReceiver.CASE_RECEIVE,
                "动态广播测试", "测试动态广播的发送和接收是否工作正常", MyReceiver.class));
        serviceCategory.caseList.add(new TestCase(Case_BroadcastReceiver.CASE_RECEIVE,
                "动态广播测试", "测试动态广播的发送和接收是否工作正常", MyReceiver.class));
        serviceCategory.caseList.add(new TestCase(Case_BroadcastReceiver.CASE_RECEIVE,
                "动态广播测试", "测试动态广播的发送和接收是否工作正常", MyReceiver.class));
        serviceCategory.caseList.add(new TestCase(Case_BroadcastReceiver.CASE_RECEIVE,
                "动态广播测试", "测试动态广播的发送和接收是否工作正常", MyReceiver.class));
        serviceCategory.caseList.add(new TestCase(Case_BroadcastReceiver.CASE_RECEIVE,
                "动态广播测试", "测试动态广播的发送和接收是否工作正常", MyReceiver.class));
        serviceCategory.caseList.add(new TestCase(Case_BroadcastReceiver.CASE_RECEIVE,
                "动态广播测试", "测试动态广播的发送和接收是否工作正常", MyReceiver.class));
        serviceCategory.caseList.add(new TestCase(Case_BroadcastReceiver.CASE_RECEIVE,
                "动态广播测试", "测试动态广播的发送和接收是否工作正常", MyReceiver.class));
        serviceCategory.caseList.add(new TestCase(Case_BroadcastReceiver.CASE_RECEIVE,
                "动态广播测试", "测试动态广播的发送和接收是否工作正常", MyReceiver.class));
        serviceCategory.caseList.add(new TestCase(Case_BroadcastReceiver.CASE_RECEIVE,
                "动态广播测试", "测试动态广播的发送和接收是否工作正常", MyReceiver.class));
        serviceCategory.caseList.add(new TestCase(Case_BroadcastReceiver.CASE_RECEIVE,
                "动态广播测试", "测试动态广播的发送和接收是否工作正常", MyReceiver.class));
        serviceCategory.caseList.add(new TestCase(Case_BroadcastReceiver.CASE_RECEIVE,
                "动态广播测试", "测试动态广播的发送和接收是否工作正常", MyReceiver.class));
        serviceCategory.caseList.add(new TestCase(Case_BroadcastReceiver.CASE_RECEIVE,
                "动态广播测试", "测试动态广播的发送和接收是否工作正常", MyReceiver.class));
        serviceCategory.caseList.add(new TestCase(Case_BroadcastReceiver.CASE_RECEIVE,
                "动态广播测试", "测试动态广播的发送和接收是否工作正常", MyReceiver.class));
        serviceCategory.caseList.add(new TestCase(Case_BroadcastReceiver.CASE_RECEIVE,
                "动态广播测试", "测试动态广播的发送和接收是否工作正常", MyReceiver.class));
        serviceCategory.caseList.add(new TestCase(Case_BroadcastReceiver.CASE_RECEIVE,
                "动态广播测试", "测试动态广播的发送和接收是否工作正常", MyReceiver.class));
        serviceCategory.caseList.add(new TestCase(Case_BroadcastReceiver.CASE_RECEIVE,
                "动态广播测试", "测试动态广播的发送和接收是否工作正常", MyReceiver.class));
        serviceCategory.caseList.add(new TestCase(Case_BroadcastReceiver.CASE_RECEIVE,
                "动态广播测试", "测试动态广播的发送和接收是否工作正常", MyReceiver.class));
        serviceCategory.caseList.add(new TestCase(Case_BroadcastReceiver.CASE_RECEIVE,
                "动态广播测试", "测试动态广播的发送和接收是否工作正常", MyReceiver.class));
        serviceCategory.caseList.add(new TestCase(Case_BroadcastReceiver.CASE_RECEIVE,
                "动态广播测试", "测试动态广播的发送和接收是否工作正常", MyReceiver.class));
        serviceCategory.caseList.add(new TestCase(Case_BroadcastReceiver.CASE_RECEIVE,
                "动态广播测试", "测试动态广播的发送和接收是否工作正常", MyReceiver.class));
        serviceCategory.caseList.add(new TestCase(Case_BroadcastReceiver.CASE_RECEIVE,
                "动态广播测试", "测试动态广播的发送和接收是否工作正常", MyReceiver.class));


    }

    public static TestCase findTestCaseById(int caseId) {
        for (TestCategory testCategory : testCases) {
            for (TestCase testCase : testCategory.caseList) {
                if (testCase.id == caseId) {
                    return testCase;
                }
            }
        }
        return null;
    }


    private static class Case_Activity {
        public final static int CATEGORY_ID = 1;

        public final static int CASE_ONCREATE = 10000;
        public final static int CASE_RECREATE = 10001;
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


}
