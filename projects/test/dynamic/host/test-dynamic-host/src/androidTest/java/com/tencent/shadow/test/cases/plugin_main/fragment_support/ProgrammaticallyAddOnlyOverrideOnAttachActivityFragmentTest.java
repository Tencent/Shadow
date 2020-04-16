package com.tencent.shadow.test.cases.plugin_main.fragment_support;

public class ProgrammaticallyAddOnlyOverrideOnAttachActivityFragmentTest extends ProgrammaticallyAddFragmentTest {
    @Override
    protected String fragmentType() {
        return "OnlyOverrideActivityMethodBaseFragment";
    }
}
