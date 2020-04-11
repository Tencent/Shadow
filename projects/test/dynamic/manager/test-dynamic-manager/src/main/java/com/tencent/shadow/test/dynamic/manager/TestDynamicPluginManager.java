package com.tencent.shadow.test.dynamic.manager;

import android.content.Context;
import android.os.Bundle;

import com.tencent.shadow.dynamic.host.EnterCallback;
import com.tencent.shadow.dynamic.host.PluginManagerImpl;
import com.tencent.shadow.test.lib.constant.Constant;

final public class TestDynamicPluginManager implements PluginManagerImpl {
    final private ActivityTestDynamicPluginManager activityPluginManager;
    final private ServiceTestDynamicPluginManager serviceTestDynamicPluginManager;
    public TestDynamicPluginManager(Context context) {
        this.activityPluginManager = new ActivityTestDynamicPluginManager(context);
        this.serviceTestDynamicPluginManager = new ServiceTestDynamicPluginManager(context);
    }

    @Override
    public void onCreate(Bundle bundle) {
        activityPluginManager.onCreate(bundle);
        serviceTestDynamicPluginManager.onCreate(bundle);
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        activityPluginManager.onSaveInstanceState(bundle);
        serviceTestDynamicPluginManager.onSaveInstanceState(bundle);
    }

    @Override
    public void onDestroy() {
        activityPluginManager.onDestroy();
        serviceTestDynamicPluginManager.onDestroy();
    }

    @Override
    public void enter(Context context, long fromId, Bundle bundle, EnterCallback callback) {
        if (fromId == Constant.FROM_ID_BIND_SERVICE) {
            serviceTestDynamicPluginManager.enter(context, fromId, bundle, callback);
        } else {
            activityPluginManager.enter(context, fromId, bundle, callback);
        }
    }
}
