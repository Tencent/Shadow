package com.tencent.shadow.test.cases.plugin_main.application_info;

/**
 * 在插件中通过Context.getApplicationInfo()获得到的ApplicationInfo测试。
 *
 * @author shifujun
 */
public class ContextGetApplicationInfoTest extends CommonApplicationInfoTest {

    @Override
    protected String getTag() {
        return "Context";
    }

    @Override
    public void testMetaData() {
        matchTextWithViewTag("TAG_metaData_" + getTag(), "");
    }
}
