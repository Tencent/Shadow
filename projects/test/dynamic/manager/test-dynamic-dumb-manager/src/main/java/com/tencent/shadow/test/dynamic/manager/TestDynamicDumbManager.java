package com.tencent.shadow.test.dynamic.manager;

import android.content.Context;
import android.os.Bundle;

import com.tencent.shadow.dynamic.host.EnterCallback;
import com.tencent.shadow.dynamic.host.PluginManagerImpl;

final public class TestDynamicDumbManager implements PluginManagerImpl {
    public TestDynamicDumbManager(Context context) {
    }

    @Override
    public void enter(Context context, long formId, Bundle bundle, EnterCallback callback) {

    }

    @Override
    public void onCreate(Bundle bundle) {

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

    }

    @Override
    public void onDestroy() {

    }
}
