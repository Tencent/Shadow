package com.tencent.shadow.test.cases.plugin_multidex;

import com.tencent.shadow.test.lib.constant.Constant;

public class MultiDexV102Test extends PluginMultiDexAppTest {
    @Override
    protected String getPartKey() {
        return Constant.PART_KEY_MULTIDEX_V1_0_2;
    }

    @Override
    protected String getActivityName() {
        return "com.tencent.shadow.test.plugin.particular_cases.multidex.v1_0_2.PluginMultidexV1_0_2Activity";
    }
}
