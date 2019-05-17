package com.tencent.shadow.test;

import android.os.Bundle;

import androidx.test.runner.AndroidJUnitRunner;

public class CustomAndroidJUnitRunner extends AndroidJUnitRunner {
    @Override
    public void onCreate(Bundle arguments) {
        //禁止Google收集数据，避免因访问不到在测试结束后等待40秒超时
        arguments.putString("disableAnalytics", Boolean.toString(true));
        super.onCreate(arguments);
    }
}
