package com.tencent.shadow.test.cases.plugin_httpclient;

import com.tencent.shadow.test.PluginTest;
import com.tencent.shadow.test.lib.constant.Constant;

abstract class PluginHttpClientTest extends PluginTest {

    /**
     * 要启动的插件的PartKey
     */
    @Override
    protected String getPartKey() {
        return Constant.PART_KEY_PLUGIN_HTTP_CLIENT;
    }

}