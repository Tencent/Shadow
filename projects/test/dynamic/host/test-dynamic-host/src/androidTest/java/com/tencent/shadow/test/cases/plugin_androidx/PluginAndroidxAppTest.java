package com.tencent.shadow.test.cases.plugin_androidx;

import com.tencent.shadow.test.PluginTest;
import com.tencent.shadow.test.lib.constant.Constant;

public abstract class PluginAndroidxAppTest extends PluginTest {

    @Override
    protected String getPartKey() {
        return Constant.PART_KEY_PLUGIN_ANDROIDX;
    }
}
