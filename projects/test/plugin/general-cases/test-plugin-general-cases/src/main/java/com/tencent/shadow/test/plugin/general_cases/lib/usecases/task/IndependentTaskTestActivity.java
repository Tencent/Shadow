/*
 * Tencent is pleased to support the open source community by making Tencent Shadow available.
 * Copyright (C) 2019 THL A29 Limited, a Tencent company. All rights reserved.
 *
 * Licensed under the BSD 3-Clause License (the "License"); you may not use
 * this file except in compliance with the License.
 */
package com.tencent.shadow.test.plugin.general_cases.lib.usecases.task;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 验证插件 Manifest 中的 singleTask 和 taskAffinity 能映射到独立宿主容器。
 */
public class IndependentTaskTestActivity extends Activity {

    private TextView mInfoView;
    private int mCreateCount;
    private int mNewIntentCount;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCreateCount = savedInstanceState == null
                ? 1 : savedInstanceState.getInt("create_count", 1);
        setTitle("Shadow 独立 Task 测试");
        setContentView(buildContentView());
        refreshInfo();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        mNewIntentCount++;
        refreshInfo();
        Toast.makeText(this, "收到 onNewIntent，复用了原独立 Task", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("create_count", mCreateCount);
        super.onSaveInstanceState(outState);
    }

    private View buildContentView() {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(16), dp(16), dp(16), dp(16));

        TextView description = new TextView(this);
        description.setText("插件声明：launchMode=singleTask，且使用非默认 taskAffinity。\n"
                + "最小化后从测试入口再次打开，应恢复同一个任务并触发 onNewIntent。");
        description.setTextSize(15);
        description.setTextColor(Color.DKGRAY);
        root.addView(description);

        mInfoView = new TextView(this);
        mInfoView.setTextSize(14);
        mInfoView.setTextColor(Color.BLACK);
        mInfoView.setPadding(0, dp(18), 0, dp(18));
        root.addView(mInfoView);

        addButton(root, "最小化独立 Task", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(IndependentTaskTestActivity.this,
                        "已最小化 taskId=" + getTaskId(), Toast.LENGTH_LONG).show();
                moveTaskToBack(true);
            }
        });

        addButton(root, "结束并移除独立 Task", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (android.os.Build.VERSION.SDK_INT >= 21) {
                    finishAndRemoveTask();
                } else {
                    finish();
                }
            }
        });

        return root;
    }

    private void refreshInfo() {
        ComponentName componentName = getComponentName();
        mInfoView.setText("插件 Activity：" + getClass().getName()
                + "\n系统容器 Activity："
                + (componentName == null ? "null" : componentName.getClassName())
                + "\ntaskId：" + getTaskId()
                + "\nonCreate 次数：" + mCreateCount
                + "\nonNewIntent 次数：" + mNewIntentCount);
    }

    private void addButton(LinearLayout root, String text, View.OnClickListener listener) {
        Button button = new Button(this);
        button.setText(text);
        button.setOnClickListener(listener);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.topMargin = dp(8);
        root.addView(button, params);
    }

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density + 0.5f);
    }
}
