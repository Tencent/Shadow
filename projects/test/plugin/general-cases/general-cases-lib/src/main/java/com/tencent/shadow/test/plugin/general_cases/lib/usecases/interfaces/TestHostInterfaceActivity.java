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

package com.tencent.shadow.test.plugin.general_cases.lib.usecases.interfaces;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.tencent.shadow.test.lib.plugin_use_host_code_lib.interfaces.HostTestInterface;
import com.tencent.shadow.test.lib.plugin_use_host_code_lib.other.HostOtherInterface;
import com.tencent.shadow.test.plugin.general_cases.lib.R;
import com.tencent.shadow.test.plugin.general_cases.lib.usecases.WithIdlingResourceActivity;

public class TestHostInterfaceActivity extends WithIdlingResourceActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_host_interface);

    }

    public void doClick(View view){
        TextView textView = findViewById(R.id.text);
        textView.setText(HostTestInterface.getText());
    }

    public void doClick1(View view){
        String str = "";
        try {
            str = HostOtherInterface.getText();
        } catch (NoClassDefFoundError e) {
            str = "ClassNotFound";
        }
        TextView textView = findViewById(R.id.text);
        textView.setText(str);
    }
}