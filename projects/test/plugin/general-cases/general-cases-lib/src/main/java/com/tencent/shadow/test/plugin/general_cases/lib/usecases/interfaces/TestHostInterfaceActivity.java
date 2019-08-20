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

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.ViewGroup;

import com.tencent.shadow.test.plugin.general_cases.lib.gallery.util.UiUtil;

public class TestHostInterfaceActivity extends Activity {

    private static final String BASE_PACKAGE = "com.tencent.shadow.test.lib.plugin_use_host_code_lib";
    private ViewGroup mItemViewGroup;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mItemViewGroup = UiUtil.setActivityContentView(this);

        loadClass("in_whitelist",//直接在白名单中的类
                BASE_PACKAGE + ".interfaces.HostTestInterface");

        loadClass("not_in_whitelist_other_package",//不再白名单中的其他包中的类
                BASE_PACKAGE + ".other.HostOtherInterface");

        loadClass("not_in_whitelist_sub_package",//属于白名单中包的子包中的类
                BASE_PACKAGE + ".interfaces.subpackage.Foo");
    }

    private void loadClass(String tag, String className) {

        ClassLoader classLoader = TestHostInterfaceActivity.class.getClassLoader();

        boolean loadSuccess;
        try {
            classLoader.loadClass(className);
            loadSuccess = true;
        } catch (ClassNotFoundException e) {
            loadSuccess = false;
        }

        makeItem("loadClass:" + className, "TAG_loadClass_" + tag,
                Boolean.toString(loadSuccess)
        );
    }

    private void makeItem(
            String labelText,
            final String viewTag,
            String value
    ) {
        ViewGroup item = UiUtil.makeItem(this, labelText, viewTag, value);
        mItemViewGroup.addView(item);
    }
}