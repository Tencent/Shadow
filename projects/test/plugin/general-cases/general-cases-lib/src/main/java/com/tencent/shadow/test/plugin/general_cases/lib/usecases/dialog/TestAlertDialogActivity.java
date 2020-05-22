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

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.ViewGroup;

import com.tencent.shadow.test.plugin.general_cases.lib.gallery.util.UiUtil;

public class TestAlertDialogActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);


        ViewGroup mItemViewGroup = UiUtil.setAlertDialogBuilderContentView(builder);


        AlertDialog dialog = builder.show();

        dialog.setOwnerActivity(this);
        Activity ownerActivity = dialog.getOwnerActivity();

        mItemViewGroup.addView(
                UiUtil.makeItem(
                        this,
                        "ownerActivityIsThis",
                        "ownerActivityIsThis",
                        Boolean.toString(ownerActivity == this)
                )
        );
    }
}
