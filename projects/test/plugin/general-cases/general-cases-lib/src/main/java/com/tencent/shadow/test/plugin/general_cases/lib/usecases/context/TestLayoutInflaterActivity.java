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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tencent.shadow.test.plugin.general_cases.lib.gallery.util.UiUtil;

public class TestLayoutInflaterActivity extends Activity {

    private ViewGroup mItemViewGroup;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mItemViewGroup = UiUtil.setActivityContentView(this);

        mItemViewGroup.addView(
                UiUtil.makeItem(
                        this,
                        "FactoryClassNameBeforeSet",
                        "FactoryClassNameBeforeSet",
                        getFactoryClassName(getLayoutInflater())
                )
        );

        LayoutInflater layoutInflater = getLayoutInflater();
        layoutInflater.setFactory2(new TestFactory2());
        mItemViewGroup.addView(
                UiUtil.makeItem(
                        this,
                        "FactoryClassNameAfterSet",
                        "FactoryClassNameAfterSet",
                        getFactoryClassName(layoutInflater)
                )
        );
    }

    private static String getFactoryClassName(LayoutInflater layoutInflater) {
        LayoutInflater.Factory factory = layoutInflater.getFactory();
        if (factory == null) {
            return "null";
        } else {
            return factory.getClass().getName();
        }
    }
}

class TestFactory2 implements LayoutInflater.Factory2 {

    @Nullable
    @Override
    public View onCreateView(@Nullable View parent, @NonNull String name, @NonNull Context context, @NonNull AttributeSet attrs) {
        return null;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull String name, @NonNull Context context, @NonNull AttributeSet attrs) {
        return null;
    }
}