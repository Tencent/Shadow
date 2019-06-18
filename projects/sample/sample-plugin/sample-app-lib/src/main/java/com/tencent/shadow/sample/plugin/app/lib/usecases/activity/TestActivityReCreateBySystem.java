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
import android.widget.TextView;

import com.tencent.shadow.sample.plugin.app.lib.R;
import com.tencent.shadow.sample.plugin.app.lib.gallery.cases.entity.UseCase;

public class TestActivityReCreateBySystem extends Activity {

    public static class Case extends UseCase {
        @Override
        public String getName() {
            return "ReCreateBySystem";
        }

        @Override
        public String getSummary() {
            return "不保留活动进行测试，需要手动到开发者模式中开启";
        }

        @Override
        public Class getPageClass() {
            return TestActivityReCreateBySystem.class;
        }

        @Override
        public Bundle getPageParams() {
            Bundle bundle= new Bundle();
            bundle.putString("url", "https://www.baidu.com");
            return bundle;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_re_create_by_system);
        String url = "url : " + getIntent().getStringExtra("url");
        ((TextView) findViewById(R.id.url_tv)).setText(url);
    }
}