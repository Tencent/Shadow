package com.tencent.shadow.test.cases.plugin_main.fragment_support;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        ProgrammaticallyAddNormalFragmentTest.class,
        ProgrammaticallyAddSubFragmentTest.class,
        ProgrammaticallyAddBaseFragmentTest.class,
        ProgrammaticallyAddDialogFragmentTest.class,
        ProgrammaticallyAddOnlyOverrideOnAttachActivityFragmentTest.class,
        ProgrammaticallyAddOnlyOverrideOnAttachContextFragmentTest.class,
        XmlAddNormalFragmentTest.class,
        XmlAddSubFragmentTest.class,
        XmlAddBaseFragmentTest.class
})
public class FragmentSupportTest {
}
