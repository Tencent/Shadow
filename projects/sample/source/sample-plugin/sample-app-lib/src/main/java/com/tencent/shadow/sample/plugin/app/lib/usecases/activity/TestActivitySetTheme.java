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

package com.tencent.shadow.sample.plugin.app.lib.usecases.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.tencent.shadow.sample.plugin.app.lib.R;
import com.tencent.shadow.sample.plugin.app.lib.gallery.cases.entity.UseCase;
import com.tencent.shadow.sample.plugin.app.lib.gallery.util.ToastUtil;

public class TestActivitySetTheme extends Activity {

    public static class Case extends UseCase {
        @Override
        public String getName() {
            return "Activity 主题测试";
        }

        @Override
        public String getSummary() {
            return "测试Activity的 setTheme 方法";
        }

        @Override
        public Class getPageClass() {
            return TestActivitySetTheme.class;
        }
    }

    int currentTheme = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        int currentTheme = getIntent().getIntExtra("theme", 0);
        currentTheme++;
        setTheme(currentTheme % 2 == 0 ? R.style.TestPluginTheme : R.style.PluginAppThemeLight);
        ToastUtil.showToast(TestActivitySetTheme.this, currentTheme % 2 == 0 ? "R.style.TestPluginTheme" : "R.style.PluginAppThemeLight");
        //setTheme必须在super.onCreate之前调用才行
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_activity_settheme);
        final View btn = findViewById(R.id.button);
        btn.setEnabled(true);
        final int finalCurrentTheme = currentTheme;
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn.setEnabled(false);

                Intent intent = new Intent(TestActivitySetTheme.this, TestActivitySetTheme.class);
                intent.putExtra("theme", finalCurrentTheme);
                startActivity(intent);

                btn.setEnabled(true);
            }
        });
    }

}
