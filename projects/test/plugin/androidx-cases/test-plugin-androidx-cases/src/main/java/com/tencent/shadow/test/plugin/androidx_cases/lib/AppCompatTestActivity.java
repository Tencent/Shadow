package com.tencent.shadow.test.plugin.androidx_cases.lib;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class AppCompatTestActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater.Factory2 factory2 = getLayoutInflater().getFactory2();
        String factory2Class;
        if (factory2 == null) {
            factory2Class = "null";
        } else {
            factory2Class = factory2.getClass().getName();
        }

        ViewGroup viewGroup = UiUtil.setActivityContentView(this);
        ViewGroup item = UiUtil.makeItem(
                this,
                "factory2Class",
                "factory2Class",
                factory2Class
        );

        viewGroup.addView(item);
    }
}
