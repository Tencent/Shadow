/*
 * Tencent is pleased to support the open source community by making Tencent Shadow available.
 * Copyright (C) 2019 THL A29 Limited, a Tencent company.  All rights reserved.
 *
 * Licensed under the BSD 3-Clause License (the "License"); you may not use
 * this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 *     https://opensource.org/licenses/BSD-3-Clause
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.tencent.shadow.test.plugin.general_cases.lib.usecases.task;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tencent.shadow.test.plugin.general_cases.lib.usecases.WithIdlingResourceActivity;

/**
 * 自动化测试用：插件Manifest声明singleTask和非默认taskAffinity，
 * 应由宿主独立Task容器池承载，并成为新Task的根Activity。
 */
public class TestIndependentTaskActivity extends WithIdlingResourceActivity {

    private static final long IDLE_TIMEOUT_MS = 5000;

    private final Handler mHandler = new Handler();

    private int mCreateCount;
    private int mNewIntentCount;

    private TextView mTaskRootView;
    private TextView mLifecycleView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCreateCount++;

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);

        mTaskRootView = new TextView(this);
        mTaskRootView.setTag("task_root");
        root.addView(mTaskRootView);

        mLifecycleView = new TextView(this);
        mLifecycleView.setTag("lifecycle");
        root.addView(mLifecycleView);

        Button relaunch = new Button(this);
        relaunch.setText("relaunch");
        relaunch.setTag("relaunch");
        relaunch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setBusy();
                startActivity(new Intent(TestIndependentTaskActivity.this,
                        TestIndependentTaskActivity.class));
            }
        });
        root.addView(relaunch);

        Button finish = new Button(this);
        finish.setText("finish");
        finish.setTag("finish");
        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        root.addView(finish);

        setContentView(root);
        updateViews();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        mNewIntentCount++;
        updateViews();
        setIdle();
    }

    private void updateViews() {
        mTaskRootView.setText("isTaskRoot=" + isTaskRoot());
        mLifecycleView.setText("onCreate=" + mCreateCount + ",onNewIntent=" + mNewIntentCount);
    }

    private void setBusy() {
        mHandler.removeCallbacksAndMessages(null);
        mIdlingResource.setIdleState(false);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mIdlingResource.setIdleState(true);
            }
        }, IDLE_TIMEOUT_MS);
    }

    private void setIdle() {
        mHandler.removeCallbacksAndMessages(null);
        mIdlingResource.setIdleState(true);
    }

    @Override
    protected void onDestroy() {
        mHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }
}
