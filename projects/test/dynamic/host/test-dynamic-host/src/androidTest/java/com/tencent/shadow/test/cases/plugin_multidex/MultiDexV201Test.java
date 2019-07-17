package com.tencent.shadow.test.cases.plugin_multidex;

import com.tencent.shadow.test.lib.constant.Constant;

public class MultiDexV201Test extends PluginMultiDexAppTest {
    @Override
    protected String getPartKey() {
        return Constant.PART_KEY_MULTIDEX_V2_0_1;
    }

    @Override
    protected String getActivityName() {
        return "com.tencent.shadow.test.plugin.particular_cases.multidex.v2_0_1.PluginMultidexV2_0_1Activity";
    }
}
