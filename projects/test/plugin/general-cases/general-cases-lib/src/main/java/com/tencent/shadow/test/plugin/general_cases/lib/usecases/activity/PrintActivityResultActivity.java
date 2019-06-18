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

package com.tencent.shadow.test.plugin.general_cases.lib.usecases.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.tencent.shadow.test.plugin.general_cases.lib.R;
import com.tencent.shadow.test.plugin.general_cases.lib.usecases.WithIdlingResourceActivity;

public class PrintActivityResultActivity extends WithIdlingResourceActivity {

    private TextView mText;

    public static String KEY_FROM_JUMP = "fromJump";
    public static String KEY_TARGET_CLASS = "targetClassName";
    public static String KEY_WAIT_FOR_RESULT = "waitForResult";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_common);
        mText = findViewById(R.id.text);
    }

    public void doClick(View view) {
        boolean wait = getIntent().getBooleanExtra(KEY_WAIT_FOR_RESULT, true);
        if (wait) {
            mIdlingResource.setIdleState(false);
        }
        String className = getIntent().getStringExtra(KEY_TARGET_CLASS);
        Intent intent = new Intent();
        intent.setClassName(this,className);
        intent.putExtra(KEY_FROM_JUMP, true);
        startActivityForResult(intent, 1001);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mIdlingResource.setIdleState(true);
        String txt = data.getStringExtra("result");
        mText.setText(txt);

    }
}
