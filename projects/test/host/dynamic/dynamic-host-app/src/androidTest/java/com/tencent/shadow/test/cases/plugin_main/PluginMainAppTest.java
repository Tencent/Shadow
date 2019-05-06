package com.tencent.shadow.test.cases.plugin_main;

import com.tencent.shadow.demo.testutil.Constant;
import com.tencent.shadow.test.PluginTest;

abstract class PluginMainAppTest extends PluginTest {

    @Override
    protected String getPartKey() {
        return Constant.PART_KEY_PLUGIN_MAIN_APP;
    }
}
