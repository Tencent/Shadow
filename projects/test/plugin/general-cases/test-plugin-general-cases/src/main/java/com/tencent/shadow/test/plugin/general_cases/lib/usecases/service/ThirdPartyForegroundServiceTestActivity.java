/*
 * Tencent is pleased to support the open source community by making Tencent Shadow available.
 * Copyright (C) 2019 THL A29 Limited, a Tencent company. All rights reserved.
 *
 * Licensed under the BSD 3-Clause License (the "License"); you may not use
 * this file except in compliance with the License.
 */
package com.tencent.shadow.test.plugin.general_cases.lib.usecases.service;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

/**
 * Simulates the foreground-Service call pattern used by third-party screen sharing SDKs.
 */
public class ThirdPartyForegroundServiceTestActivity extends Activity {

    private static final int REQUEST_MEDIA_PROJECTION = 2001;
    private static final int REQUEST_RECORD_AUDIO = 2002;

    private TextView mLogView;
    /**
     * 1 = microphone only; 2..4 = screen share plus the requested number of Services.
     */
    private int mPendingServiceCount;

    private final BroadcastReceiver mStatusReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            appendLog(intent.getStringExtra(MockScreenShareService.EXTRA_MESSAGE));
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("第三方SDK前台Service模拟");

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(16), dp(12), dp(16), dp(16));

        TextView description = new TextView(this);
        description.setText("模拟第三方SDK：MediaProjection屏幕共享 + 麦克风Service");
        description.setTextSize(14);
        root.addView(description);

        addButton(root, "启动屏幕共享Service（含系统录屏授权）", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestScreenCapture();
            }
        });
        addButton(root, "启动麦克风Service", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startServicesWithPermission(1);
            }
        });
        addButton(root, "同时启动屏幕共享 + 麦克风", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startServicesWithPermission(2);
            }
        });
        addButton(root, "同时启动3个插件Service", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startServicesWithPermission(3);
            }
        });
        addButton(root, "同时启动4个插件Service", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startServicesWithPermission(4);
            }
        });
        addButton(root, "仅停止屏幕共享Service", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopService(new Intent(ThirdPartyForegroundServiceTestActivity.this,
                        MockScreenShareService.class));
                appendLog("已调用 stopService(screen share)，麦克风Service应继续运行");
            }
        });
        addButton(root, "仅停止麦克风Service", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopService(new Intent(ThirdPartyForegroundServiceTestActivity.this,
                        MockMicrophoneService.class));
                appendLog("已调用 stopService(microphone)，屏幕共享Service应继续运行");
            }
        });
        addButton(root, "停止第3、4个Service", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopService(new Intent(ThirdPartyForegroundServiceTestActivity.this,
                        MockThirdForegroundService.class));
                stopService(new Intent(ThirdPartyForegroundServiceTestActivity.this,
                        MockFourthForegroundService.class));
                appendLog("已调用 stopService(third/fourth)");
            }
        });
        addButton(root, "停止全部Service", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopService(new Intent(ThirdPartyForegroundServiceTestActivity.this,
                        MockScreenShareService.class));
                stopService(new Intent(ThirdPartyForegroundServiceTestActivity.this,
                        MockMicrophoneService.class));
                stopService(new Intent(ThirdPartyForegroundServiceTestActivity.this,
                        MockThirdForegroundService.class));
                stopService(new Intent(ThirdPartyForegroundServiceTestActivity.this,
                        MockFourthForegroundService.class));
                appendLog("已停止全部模拟Service");
            }
        });

        mLogView = new TextView(this);
        mLogView.setTextSize(12);
        mLogView.setPadding(dp(8), dp(8), dp(8), dp(8));
        ScrollView scrollView = new ScrollView(this);
        scrollView.addView(mLogView);
        LinearLayout.LayoutParams scrollParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f);
        scrollParams.topMargin = dp(12);
        root.addView(scrollView, scrollParams);

        setContentView(root);
        registerStatusReceiver();
        requestNotificationPermissionIfNeeded();
        appendLog("页面就绪；启动后请检查通知栏和PluginContainerService日志");
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mStatusReceiver);
        super.onDestroy();
    }

    private void requestScreenCapture() {
        MediaProjectionManager manager = (MediaProjectionManager)
                getSystemService(MEDIA_PROJECTION_SERVICE);
        if (manager == null) {
            appendLog("ERROR: MediaProjectionManager不可用");
            return;
        }
        appendLog("请求系统录屏授权...");
        startActivityForResult(manager.createScreenCaptureIntent(), REQUEST_MEDIA_PROJECTION);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != REQUEST_MEDIA_PROJECTION) {
            return;
        }
        if (resultCode != RESULT_OK || data == null) {
            appendLog("用户取消系统录屏授权");
            mPendingServiceCount = 0;
            return;
        }

        Intent serviceIntent = new Intent(this, MockScreenShareService.class);
        serviceIntent.putExtra(MockScreenShareService.EXTRA_RESULT_CODE, resultCode);
        serviceIntent.putExtra(MockScreenShareService.EXTRA_RESULT_DATA, data);
        startForegroundServiceCompat(serviceIntent);
        appendLog("已请求启动屏幕共享Service");

        int serviceCount = mPendingServiceCount;
        mPendingServiceCount = 0;
        if (serviceCount >= 2) {
            startForegroundServiceCompat(new Intent(this, MockMicrophoneService.class));
            appendLog("已请求启动麦克风Service");
        }
        if (serviceCount >= 3) {
            startForegroundServiceCompat(new Intent(this, MockThirdForegroundService.class));
            appendLog("已请求启动第3个Service");
        }
        if (serviceCount >= 4) {
            startForegroundServiceCompat(new Intent(this, MockFourthForegroundService.class));
            appendLog("已请求启动第4个Service");
        }
    }

    private void startServicesWithPermission(int serviceCount) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && checkSelfPermission(Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            mPendingServiceCount = serviceCount;
            requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_RECORD_AUDIO);
            appendLog("请求麦克风权限...");
            return;
        }
        if (serviceCount >= 2) {
            mPendingServiceCount = serviceCount;
            requestScreenCapture();
        } else {
            startForegroundServiceCompat(new Intent(this, MockMicrophoneService.class));
            appendLog("已请求启动麦克风Service");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode != REQUEST_RECORD_AUDIO) {
            return;
        }
        boolean granted = grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED;
        if (!granted) {
            appendLog("麦克风权限被拒绝");
            mPendingServiceCount = 0;
            return;
        }
        if (mPendingServiceCount >= 2) {
            requestScreenCapture();
        } else if (mPendingServiceCount == 1) {
            mPendingServiceCount = 0;
            startForegroundServiceCompat(new Intent(this, MockMicrophoneService.class));
            appendLog("已请求启动麦克风Service");
        }
    }

    private void startForegroundServiceCompat(Intent intent) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent);
            } else {
                startService(intent);
            }
        } catch (RuntimeException e) {
            appendLog("ERROR: " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }

    private void requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= 33
                && checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 2003);
        }
    }

    private void registerStatusReceiver() {
        IntentFilter filter = new IntentFilter(MockScreenShareService.ACTION_STATUS);
        if (Build.VERSION.SDK_INT >= 33) {
            registerReceiver(mStatusReceiver, filter, Context.RECEIVER_NOT_EXPORTED);
        } else {
            registerReceiver(mStatusReceiver, filter);
        }
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

    private void appendLog(String message) {
        if (message == null || mLogView == null) {
            return;
        }
        mLogView.append("• " + message + "\n");
    }

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density + 0.5f);
    }
}
