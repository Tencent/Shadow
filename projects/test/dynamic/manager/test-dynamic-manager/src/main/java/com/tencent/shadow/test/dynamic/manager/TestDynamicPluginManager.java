package com.tencent.shadow.test.dynamic.manager;

import android.content.Context;
import android.os.Bundle;

import com.tencent.shadow.dynamic.host.EnterCallback;
import com.tencent.shadow.dynamic.host.PluginManagerImpl;
import com.tencent.shadow.test.lib.constant.Constant;

final public class TestDynamicPluginManager implements PluginManagerImpl {
    final private ActivityTestDynamicPluginManager activityPluginManager;
    final private ServiceTestDynamicPluginManager serviceTestDynamicPluginManager;
    final private ReinstallPluginTestDynamicPluginManager reinstallPluginTestDynamicPluginManager;

    public TestDynamicPluginManager(Context context) {
        this.activityPluginManager = new ActivityTestDynamicPluginManager(context);
        this.serviceTestDynamicPluginManager = new ServiceTestDynamicPluginManager(context);
        this.reinstallPluginTestDynamicPluginManager = new ReinstallPluginTestDynamicPluginManager(context);
    }

    @Override
    public void onCreate(Bundle bundle) {
        activityPluginManager.onCreate(bundle);
        serviceTestDynamicPluginManager.onCreate(bundle);
        reinstallPluginTestDynamicPluginManager.onCreate(bundle);
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        activityPluginManager.onSaveInstanceState(bundle);
        serviceTestDynamicPluginManager.onSaveInstanceState(bundle);
        reinstallPluginTestDynamicPluginManager.onSaveInstanceState(bundle);
    }

    @Override
    public void onDestroy() {
        activityPluginManager.onDestroy();
        serviceTestDynamicPluginManager.onDestroy();
        reinstallPluginTestDynamicPluginManager.onDestroy();
    }

    @Override
    public void enter(Context context, long fromId, Bundle bundle, EnterCallback callback) {
        if (fromId == Constant.FROM_ID_BIND_SERVICE) {
            serviceTestDynamicPluginManager.enter(context, fromId, bundle, callback);
        } else if (fromId == Constant.FROM_ID_START_ACTIVITY) {
            activityPluginManager.enter(context, fromId, bundle, callback);
        } else if (fromId == Constant.FROM_ID_REINSTALL_PLUGIN) {
            reinstallPluginTestDynamicPluginManager.enter(context, fromId, bundle, callback);
        } else {
            throw new RuntimeException("不认识的fromId==" + fromId);
        }
    }
}
