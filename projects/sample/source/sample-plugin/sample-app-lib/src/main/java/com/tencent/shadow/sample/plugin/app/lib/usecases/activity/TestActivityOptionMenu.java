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
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;

import com.tencent.shadow.sample.plugin.app.lib.R;
import com.tencent.shadow.sample.plugin.app.lib.gallery.cases.entity.UseCase;

public class TestActivityOptionMenu extends Activity {

    public static class Case extends UseCase {
        @Override
        public String getName() {
            return "Activity Menu测试";
        }

        @Override
        public String getSummary() {
            return "测试Activity的 onCreateOptionsMenu";
        }

        @Override
        public Class getPageClass() {
            return TestActivityOptionMenu.class;
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(R.style.PluginAppThemeLight);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_activity_settheme);
        setTitle("看右边的 menu ->");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.case_test_activity_option_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
}
