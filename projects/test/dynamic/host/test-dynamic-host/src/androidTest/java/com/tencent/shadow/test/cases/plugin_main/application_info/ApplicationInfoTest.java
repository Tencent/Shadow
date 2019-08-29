package com.tencent.shadow.test.cases.plugin_main.application_info;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({ContextGetApplicationInfoTest.class,
        PackageManagerGetSelfApplicationInfoTest.class,
        PackageManagerGetOtherInstalledApplicationInfoTest.class})
public class ApplicationInfoTest {
}
