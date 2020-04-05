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

package com.tencent.shadow.test.plugin.general_cases.lib.usecases.receiver;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.tencent.shadow.test.plugin.general_cases.lib.R;
import com.tencent.shadow.test.plugin.general_cases.lib.gallery.util.ToastUtil;
import com.tencent.shadow.test.plugin.general_cases.lib.usecases.WithIdlingResourceActivity;

public class TestReceiverActivity extends WithIdlingResourceActivity {

    private final static String INTENT_NORMAL_ACTION = "com.tencent.test.normal.action";
    private final static String INTENT_DYNAMIC_ACTION = "com.tencent.test.action.DYNAMIC";

    private final static String MSG_NORMAL = "收到测试静态广播发送";
    private final static String MSG_DYNAMIC = "收到动态动态广播发送";


    private TextView mTextView;

    private BroadcastReceiver mBroadcastReceiver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_receiver);

        mTextView = findViewById(R.id.content);

        BroadCastHelper.setNotify(new BroadCastHelper.Notify() {
            @Override
            public void onReceiver(Intent intent, Context context) {
                mIdlingResource.setIdleState(true);

                boolean isShadowContext = false;
                try {
                    Class clazz = Class.forName("com.tencent.shadow.core.runtime.ShadowContext");
                    isShadowContext = clazz.isAssignableFrom(context.getClass());
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                mTextView.setText(String.format("action:%s msg:%s isShadowContext:%s", intent.getAction(), intent.getStringExtra("msg"), isShadowContext));
            }
        });

        mBroadcastReceiver = new DynamicBroadcastReceiver();
        registerReceiver(mBroadcastReceiver, new IntentFilter(INTENT_DYNAMIC_ACTION));
    }

    public void TestNormalBraodcast(View view) {
        mIdlingResource.setIdleState(false);
        Intent intent = new Intent(INTENT_NORMAL_ACTION);
        intent.putExtra("msg", MSG_NORMAL);
        sendBroadcast(intent);
    }

    public void TestDynamicBraodcast(View view) {
        mIdlingResource.setIdleState(false);
        Intent intent = new Intent(INTENT_DYNAMIC_ACTION);
        intent.putExtra("msg", MSG_DYNAMIC);
        sendBroadcast(intent);
    }

    public void TestUnregisterDynamicBraodcast(View view){
        mTextView.setText("");
        unregisterReceiver(mBroadcastReceiver);
        TestDynamicBraodcast(view);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mIdlingResource.setIdleState(true);
            }
        },2000);
    }

    private class DynamicBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String msg = intent.getStringExtra("msg");
            ToastUtil.showToast(context, msg);

            BroadCastHelper.notify(intent, context);
        }
    }

}
