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

package com.tencent.shadow.sample.plugin.app.lib.usecases.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.tencent.shadow.sample.plugin.app.lib.R;
import com.tencent.shadow.sample.plugin.app.lib.gallery.BaseActivity;
import com.tencent.shadow.sample.plugin.app.lib.gallery.cases.entity.UseCase;

public class TestDynamicFragmentActivity extends BaseActivity {

    public static class Case extends UseCase {
        @Override
        public String getName() {
            return "代码添加fragment相关测试";
        }

        @Override
        public String getSummary() {
            return "测试通过代码添加一个fragment";
        }

        @Override
        public Class getPageClass() {
            return TestDynamicFragmentActivity.class;
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_fragment_activity);

        String msg = "这是一个动态添加的fragment";
        Bundle bundle = new Bundle();
        bundle.putString("msg", msg);
        TestFragment testFragment = TestFragment.newInstance(bundle);
        getFragmentManager().beginTransaction().add(R.id.fragment_container,testFragment).commit();
    }
}
