package com.tencent.shadow.demo.host;

import com.tencent.shadow.demo.testutil.Constant;

abstract class DemoBaseTest extends BaseTest {
    @Override
    String getPartKey() {
        return Constant.PART_KEY_DEMO_MAIN;
    }
}
