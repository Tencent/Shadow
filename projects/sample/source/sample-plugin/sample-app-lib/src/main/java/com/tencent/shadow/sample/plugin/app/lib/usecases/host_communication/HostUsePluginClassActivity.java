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

package com.tencent.shadow.sample.plugin.app.lib.usecases.host_communication;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.LinearLayout;

import com.tencent.shadow.sample.host.lib.Callback;
import com.tencent.shadow.sample.host.lib.HostCallbackProvider;
import com.tencent.shadow.sample.host.lib.HostCallbackProvider2;
import com.tencent.shadow.sample.host.lib.HostUiLayerProvider;
import com.tencent.shadow.sample.plugin.app.lib.gallery.BaseActivity;
import com.tencent.shadow.sample.plugin.app.lib.gallery.cases.entity.UseCase;

public class HostUsePluginClassActivity extends BaseActivity {
    public static class Case extends UseCase {
        @Override
        public String getName() {
            return "宿主使用插件类测试";
        }

        @Override
        public String getSummary() {
            return "测试宿主使用插件类的方法";
        }

        @Override
        public Class getPageClass() {
            return HostUsePluginClassActivity.class;
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout linearLayout = new LinearLayout(this);

        HostUiLayerProvider hostUiLayerProvider = HostUiLayerProvider.getInstance();
        View hostUiLayer = hostUiLayerProvider.buildHostUiLayer();
        linearLayout.addView(hostUiLayer);

        setContentView(linearLayout);

        HostCallbackProvider.getInstance().setCallback(new Callback() {
            @Override
            public String call(String s) {
                return "Hi, " + s + ", byebye";
            }
        });


//        HostCallbackProvider.getInstance().call();

        HostCallbackProvider2.getInstance().call();
    }
}
