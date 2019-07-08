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

package com.tencent.shadow.sample.plugin.app.lib.usecases.application;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.tencent.shadow.sample.plugin.app.lib.R;
import com.tencent.shadow.sample.plugin.app.lib.gallery.BaseActivity;
import com.tencent.shadow.sample.plugin.app.lib.gallery.TestApplication;

public class TestApplicationActivity extends BaseActivity {

    private TextView mText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_common);
        mText = findViewById(R.id.text);
        mText.setText("isCallOnCreate:" + TestApplication.getInstance().isOnCreate);
    }

    public void doClick(View view){

    }
}
