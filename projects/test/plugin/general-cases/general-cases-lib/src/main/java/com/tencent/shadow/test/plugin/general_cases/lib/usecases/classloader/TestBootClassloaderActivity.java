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

package com.tencent.shadow.test.plugin.general_cases.lib.usecases.classloader;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.ViewGroup;

import com.tencent.shadow.test.plugin.general_cases.lib.gallery.util.UiUtil;

/**
 * 插件中自己声明了一个和系统类重名的类org.xmlpull.v1.XmlPullParser
 * 测试在插件环境下加载它是从哪个ClassLoader加载的
 */
public class TestBootClassloaderActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewGroup viewGroup = UiUtil.setActivityContentView(this);

        String xmlPullParserFrom;
        try {
            xmlPullParserFrom = getClassLoader().loadClass("org.xmlpull.v1.XmlPullParser")
                    .getClassLoader().getClass().getName();
        } catch (ClassNotFoundException e) {
            xmlPullParserFrom = "ClassNotFoundException";
        }

        viewGroup.addView(
                UiUtil.makeItem(
                        this,
                        "xmlPullParserFrom",
                        "xmlPullParserFrom",
                        xmlPullParserFrom
                )
        );
    }

}
