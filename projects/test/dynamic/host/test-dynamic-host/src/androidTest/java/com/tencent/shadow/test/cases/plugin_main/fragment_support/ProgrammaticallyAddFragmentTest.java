package com.tencent.shadow.test.cases.plugin_main.fragment_support;

abstract class ProgrammaticallyAddFragmentTest extends CommonFragmentSupportTest {
    @Override
    protected String getActivityName() {
        return "ProgrammaticallyAddFragmentActivity";
    }

    @Override
    protected String expectMsg() {
        return "addFragmentProgrammatically";
    }
}
