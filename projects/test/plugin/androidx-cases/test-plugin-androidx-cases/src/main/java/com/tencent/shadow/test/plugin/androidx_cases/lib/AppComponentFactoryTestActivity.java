package com.tencent.shadow.test.plugin.androidx_cases.lib;

import android.app.Activity;
import android.os.Bundle;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

public class AppComponentFactoryTestActivity extends Activity {
    boolean flag = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ViewGroup viewGroup = UiUtil.setActivityContentView(this);
        ViewGroup item = UiUtil.makeItem(
                this,
                "flag",
                "flag",
                Boolean.toString(flag)
        );

        viewGroup.addView(item);
    }
}
