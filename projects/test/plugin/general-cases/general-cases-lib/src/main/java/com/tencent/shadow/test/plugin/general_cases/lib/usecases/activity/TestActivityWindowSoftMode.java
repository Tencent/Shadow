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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.widget.EditText;

import com.tencent.shadow.test.plugin.general_cases.lib.R;
import com.tencent.shadow.test.plugin.general_cases.lib.usecases.util.SoftKeyBoardListener;


public class TestActivityWindowSoftMode extends Activity {

    private EditText mEditText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_softmode);

        mEditText = findViewById(R.id.edit_view);
        mEditText.requestFocus();

        //是否来自单元测试的中转Activity
        final boolean isFromJump = getIntent().getBooleanExtra(WindowSoftModeJumpActivity.KEY_FROM_JUMP, false);

        final Handler handler = new Handler();

        if (isFromJump) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    setResult("hide");
                }
            }, 3000);
        }


        SoftKeyBoardListener.setListener(this, new SoftKeyBoardListener.OnSoftKeyBoardChangeListener() {
            @Override
            public void keyBoardShow(int height) {
                if (isFromJump) {
                    handler.removeCallbacksAndMessages(null);

                    setResult("show");
                }
            }

            @Override
            public void keyBoardHide(int height) {

            }
        });

    }

    private void setResult(String result){
        Intent intent = new Intent();
        intent.putExtra("result", result);
        setResult(0, intent);
        finish();
    }

}
