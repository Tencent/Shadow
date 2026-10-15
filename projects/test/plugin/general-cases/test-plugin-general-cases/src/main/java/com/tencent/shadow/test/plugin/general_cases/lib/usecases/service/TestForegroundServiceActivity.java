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

package com.tencent.shadow.test.plugin.general_cases.lib.usecases.service;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tencent.shadow.test.plugin.general_cases.lib.usecases.WithIdlingResourceActivity;

import java.util.List;

/**
 * 自动化测试用：验证多个插件前台Service共享同一个宿主容器Service，
 * 且只有全部插件Service停止后容器才会停止。
 */
public class TestForegroundServiceActivity extends WithIdlingResourceActivity {

    private static final String CONTAINER_SERVICE =
            "com.tencent.shadow.core.runtime.container.PluginContainerService";

    /**
     * 等待容器Service启动、前台提升或stopSelf在系统中生效的时间。
     */
    private static final long SETTLE_DELAY_MS = 1500;

    private final Handler mHandler = new Handler();

    private TextView mEventsView;
    private TextView mContainerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);

        mEventsView = new TextView(this);
        mEventsView.setTag("events");
        root.addView(mEventsView);

        mContainerView = new TextView(this);
        mContainerView.setTag("container");
        root.addView(mContainerView);

        addButton(root, "startA", new Runnable() {
            @Override
            public void run() {
                startForegroundServiceCompat(TestForegroundServiceA.class);
            }
        });
        addButton(root, "startB", new Runnable() {
            @Override
            public void run() {
                startForegroundServiceCompat(TestForegroundServiceB.class);
            }
        });
        addButton(root, "stopA", new Runnable() {
            @Override
            public void run() {
                stopService(new Intent(TestForegroundServiceActivity.this,
                        TestForegroundServiceA.class));
            }
        });
        addButton(root, "stopB", new Runnable() {
            @Override
            public void run() {
                stopService(new Intent(TestForegroundServiceActivity.this,
                        TestForegroundServiceB.class));
            }
        });

        // 容器onStartCommand回调前立即stop，验证pending stop不会导致前台Service超时
        addButton(root, "startThenStopA", new Runnable() {
            @Override
            public void run() {
                startForegroundServiceCompat(TestForegroundServiceA.class);
                stopService(new Intent(TestForegroundServiceActivity.this,
                        TestForegroundServiceA.class));
            }
        });

        setContentView(root);
        updateContainerState();

        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver,
                new IntentFilter(BaseTestForegroundService.ACTION_EVENT));
    }

    private void addButton(LinearLayout root, final String tag, final Runnable action) {
        Button button = new Button(this);
        button.setText(tag);
        button.setTag(tag);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runAndSettle(action);
            }
        });
        root.addView(button);
    }

    private void startForegroundServiceCompat(Class<?> serviceClass) {
        Intent intent = new Intent(this, serviceClass);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);
        } else {
            startService(intent);
        }
    }

    private void runAndSettle(Runnable action) {
        mHandler.removeCallbacksAndMessages(null);
        mIdlingResource.setIdleState(false);
        action.run();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                updateContainerState();
                mIdlingResource.setIdleState(true);
            }
        }, SETTLE_DELAY_MS);
    }

    private void updateContainerState() {
        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> services =
                am.getRunningServices(Integer.MAX_VALUE);
        String state = "running=false";
        if (services != null) {
            for (ActivityManager.RunningServiceInfo info : services) {
                if (CONTAINER_SERVICE.equals(info.service.getClassName())
                        && info.started) {
                    state = "running=true,foreground=" + info.foreground;
                    break;
                }
            }
        }
        mContainerView.setText(state);
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String event = intent.getStringExtra(BaseTestForegroundService.EXTRA_EVENT);
            String old = mEventsView.getText().toString();
            mEventsView.setText(TextUtils.isEmpty(old) ? event : old + "-" + event);
        }
    };

    @Override
    protected void onDestroy() {
        mHandler.removeCallbacksAndMessages(null);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
        super.onDestroy();
    }
}
