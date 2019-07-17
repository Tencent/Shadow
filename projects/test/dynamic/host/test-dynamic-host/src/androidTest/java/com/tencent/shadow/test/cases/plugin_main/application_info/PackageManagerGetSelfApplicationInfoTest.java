package com.tencent.shadow.test.cases.plugin_main.application_info;

/**
 * 在插件中通过PackageManager.getApplicationInfo()获取插件自己的ApplicationInfo测试。
 *
 * @author shifujun
 */
public class PackageManagerGetSelfApplicationInfoTest extends CommonApplicationInfoTest {

    @Override
    protected String getTag() {
        return "PackageManagerGetSelf";
    }

    @Override
    public void testMetaData() {
        matchSubstringWithViewTag("TAG_metaData_" + getTag(), "test_value");
    }
}
