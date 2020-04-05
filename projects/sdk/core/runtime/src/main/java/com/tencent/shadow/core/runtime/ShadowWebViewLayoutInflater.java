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
import android.util.Pair;
import android.view.LayoutInflater;

import com.tencent.shadow.core.runtime.container.PluginContainerActivity;

/**
 * 1.模拟PhoneLayoutInflater的系统view构造过程
 * 2.将xml中的webview替换成shadowWebView
 */
public class ShadowWebViewLayoutInflater extends FixedContextLayoutInflater{

    private static final String AndroidWebView = "android.webkit.WebView";

    private static final String ShadowPackagePrefix = "com.tencent.shadow.core.runtime.";

    private static final String ShadowWebView = "ShadowWebView";

    public ShadowWebViewLayoutInflater(LayoutInflater original, Context newContext) {
        super(original, newContext);
    }

    @Override
    LayoutInflater createNewContextLayoutInflater(Context newContext) {
        if (newContext instanceof PluginContainerActivity) {
            Object pluginActivity = PluginActivity.get((PluginContainerActivity) newContext);
            return new ShadowWebViewLayoutInflater(this, (Context) pluginActivity);
        } else {
            //context有2种可能，1种是ShadowContext,一种是其他context
            return new ShadowWebViewLayoutInflater(this, newContext);
        }
    }

    @Override
    Pair<String,String> changeViewNameAndPrefix(String prefix,String name) {
        if (AndroidWebView.equals(prefix + name)) {
            prefix = ShadowPackagePrefix;
            name = ShadowWebView;
        }
        return new Pair<>(name,prefix);
    }


}
