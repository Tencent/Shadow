package com.tencent.shadow.test.cases.plugin_main;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.tencent.shadow.test.lib.constant.Constant;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class ReinstallPluginTestOnly extends BasicTest {

    @Override
    protected int getFromId() {

        return Constant.FROM_ID_REINSTALL_PLUGIN_ONLY;

    }


    // 单独测试 reinstall 的zip 是否正常
    @Test
    public void testReinstallPluginOnly() {
        matchTextWithViewTag("tv", "reinstall");
    }

}
