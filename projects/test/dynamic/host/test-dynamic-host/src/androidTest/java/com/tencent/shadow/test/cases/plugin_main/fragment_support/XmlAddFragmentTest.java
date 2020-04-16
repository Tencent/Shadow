package com.tencent.shadow.test.cases.plugin_main.fragment_support;

import android.os.Build;

import org.junit.Assume;
import org.junit.Test;

abstract class XmlAddFragmentTest extends CommonFragmentSupportTest {
    @Override
    protected String getActivityName() {
        return "XmlAddFragmentActivity";
    }

    @Override
    protected String expectMsg() {
        return "addFragmentWithXml";
    }

    @Test
    public void inflateContext() {
        Assume.assumeTrue(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M);
        matchTextWithViewTag("InflateContextView",
                "com.tencent.shadow.test.plugin.general_cases.lib.usecases.fragment." + getActivityName());
    }

    @Test
    public void inflateActivity() {
        matchTextWithViewTag("InflateActivityView",
                "com.tencent.shadow.test.plugin.general_cases.lib.usecases.fragment." + getActivityName());
    }
}
