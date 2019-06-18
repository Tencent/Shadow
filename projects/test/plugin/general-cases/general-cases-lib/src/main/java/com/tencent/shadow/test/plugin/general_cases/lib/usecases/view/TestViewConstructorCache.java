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

package com.tencent.shadow.test.plugin.general_cases.lib.usecases.view;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.tencent.shadow.test.lib.custom_view.TestViewConstructorCacheView;
import com.tencent.shadow.test.plugin.general_cases.lib.R;

import dalvik.system.PathClassLoader;

public class TestViewConstructorCache extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_test_view_cons_cache);
        TestViewConstructorCacheView testView = findViewById(R.id.testView);

        PathClassLoader pathClassLoader = (PathClassLoader) getApplication().getBaseContext().getClass().getClassLoader();

        boolean assertTrue;
        try {
            assertTrue = pathClassLoader.loadClass(TestViewConstructorCacheView.class.getName()) != testView.getClass();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("宿主中应该也有同名View");
        }

        if (!assertTrue) {
            throw new AssertionError("插件和宿主中不应该能加载出相同View名的同一个类");
        }
    }
}
