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

package com.tencent.shadow.test.plugin.general_cases.lib.usecases.dialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.ViewGroup;

import com.tencent.shadow.test.plugin.general_cases.lib.gallery.util.UiUtil;

public class TestDialogActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TestDialog dialog = new TestDialog(this);

        dialog.setOwnerActivity(this);
        Activity ownerActivity = dialog.getOwnerActivity();

        ViewGroup mItemViewGroup = UiUtil.setDialogContentView(dialog);
        mItemViewGroup.addView(
                UiUtil.makeItem(
                        this,
                        "ownerActivityIsThis",
                        "ownerActivityIsThis",
                        Boolean.toString(ownerActivity == this)
                )
        );

        dialog.show();
    }

    @SuppressLint("NewApi")
    @Override
    protected void attachBaseContext(Context newBase) {
        Configuration configuration = new Configuration();
        Context context = newBase.createConfigurationContext(configuration);
        super.attachBaseContext(context);
    }
}
