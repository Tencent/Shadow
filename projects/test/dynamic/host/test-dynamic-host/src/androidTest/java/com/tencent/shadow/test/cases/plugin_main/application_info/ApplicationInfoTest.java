package com.tencent.shadow.test.cases.plugin_main.application_info;

import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Ignore("避免自动化全量测试时重复这些测试")
@Suite.SuiteClasses({ContextGetApplicationInfoTest.class,
        PackageManagerGetSelfApplicationInfoTest.class,
        PackageManagerGetOtherInstalledApplicationInfoTest.class})
public class ApplicationInfoTest {
}
