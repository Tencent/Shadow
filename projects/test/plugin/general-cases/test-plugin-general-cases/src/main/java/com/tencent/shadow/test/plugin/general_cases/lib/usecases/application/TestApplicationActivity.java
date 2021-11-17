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

package com.tencent.shadow.test.plugin.general_cases.lib.usecases.application;

import android.app.Activity;
import android.app.Application;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.ViewGroup;

import com.tencent.shadow.test.plugin.general_cases.lib.gallery.TestApplication;
import com.tencent.shadow.test.plugin.general_cases.lib.gallery.util.UiUtil;

public class TestApplicationActivity extends Activity {

    private ViewGroup mItemViewGroup;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mItemViewGroup = UiUtil.setActivityContentView(this);

        mItemViewGroup.addView(
                UiUtil.makeItem(
                        this,
                        "TestApplication被调用过onCreate",
                        "TAG_IS_ON_CREATE",
                        Boolean.toString(TestApplication.getInstance().isOnCreate)
                )
        );

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            mItemViewGroup.addView(
                    UiUtil.makeItem(
                            this,
                            "Application.getProcessName()",
                            "Application.getProcessName()",
                            Application.getProcessName()
                    )
            );
        }
    }
}
