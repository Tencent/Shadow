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

package com.tencent.shadow.test.plugin.general_cases.lib.usecases.context;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.ContextThemeWrapper;
import android.view.ViewGroup;

import com.tencent.shadow.test.plugin.general_cases.lib.gallery.util.UiUtil;

import java.lang.reflect.Method;

public class TestThemeActivity extends Activity {

    private ViewGroup mItemViewGroup;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mItemViewGroup = UiUtil.setActivityContentView(this);

        mItemViewGroup.addView(
                UiUtil.makeItem(
                        this,
                        "ApplicationThemeName",
                        "ApplicationThemeName",
                        getThemeName(getApplicationContext())
                )
        );

        mItemViewGroup.addView(
                UiUtil.makeItem(
                        this,
                        "ActivityThemeName",
                        "ActivityThemeName",
                        getThemeName(this)
                )
        );
    }

    private static String getThemeName(Context context) {
        try {
            Class<?> clazz = ContextThemeWrapper.class;
            Method method = clazz.getMethod("getThemeResId");
            method.setAccessible(true);
            int themeResId = (Integer) method.invoke(context);
            return context.getResources().getResourceName(themeResId);
        } catch (Exception e) {
            return "Exception: " + e.getMessage();
        }
    }
}
