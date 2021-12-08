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

package com.tencent.shadow.core.runtime;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;

/**
 * 在HostActivityDelegate.getLayoutInflater返回的LayoutInflater虽然已经被替换为ShadowActivity作为Context了.
 * 但是Fragment在创建时还是会通过这个LayoutInflater的cloneInContext方法,传入宿主Activity作为新的Context.
 * 这里通过覆盖cloneInContext方法,避免Context被替换.
 * 见onGetLayoutInflater() of Activity$HostCallbacks in Activity.java
 *
 * @author cubershi
 */
public abstract class FixedContextLayoutInflater extends LayoutInflater {
    /**
     * 复制自
     * com.android.internal.policy.PhoneLayoutInflater#sClassPrefixList
     */
    private static final String[] sClassPrefixList = {
            "android.widget.",
            "android.webkit.",
            "android.app."
    };

    public FixedContextLayoutInflater(Context context) {
        super(context);
    }

    public FixedContextLayoutInflater(LayoutInflater original, Context newContext) {
        super(original, newContext);
    }

    @Override
    protected View onCreateView(String name, AttributeSet attrs) throws ClassNotFoundException {
        //模仿com.android.internal.policy.PhoneLayoutInflater#onCreateView实现
        //xml中一些系统view省略了包名，这里在尝试拼上包名
        for (String prefix : sClassPrefixList) {
            try {
                Pair<String, String> afterChange = changeViewNameAndPrefix(name, prefix);
                name = afterChange.first;
                prefix = afterChange.second;
                View view = createView(name, prefix, attrs);
                if (view != null) {
                    return view;
                }
            } catch (ClassNotFoundException e) {
                // In this case we want to let the base class take a crack
                // at it.
            }
        }

        return super.onCreateView(name, attrs);
    }

    @Override
    public LayoutInflater cloneInContext(Context newContext) {
        return createNewContextLayoutInflater(newContext);
    }

    abstract LayoutInflater createNewContextLayoutInflater(Context context);

    abstract Pair<String, String> changeViewNameAndPrefix(String name, String prefix);

}
