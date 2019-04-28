package com.tencent.shadow.core.pluginmanager;

import android.os.Bundle;
import android.support.test.runner.AndroidJUnitRunner;

public class CustomAndroidJUnitRunner extends AndroidJUnitRunner {
    @Override
    public void onCreate(Bundle arguments) {
        //禁止Google收集数据，避免因访问不到在测试结束后等待40秒超时
        arguments.putString("disableAnalytics", Boolean.toString(true));
        super.onCreate(arguments);
    }
}
