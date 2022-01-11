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

package com.tencent.shadow.test.dynamic.host;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.tencent.shadow.dynamic.host.EnterCallback;
import com.tencent.shadow.test.lib.constant.Constant;
import com.tencent.shadow.test.lib.test_manager.SimpleIdlingResource;
import com.tencent.shadow.test.lib.test_manager.TestManager;

public class BindPluginServiceActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jump_to_plugin);
        TestManager.sBindPluginServiceActivityContentView = findViewById(R.id.root);
    }

    public void jump(View view) {
        HostApplication.getApp().loadPluginManager(PluginHelper.getInstance().pluginManagerFile);

        Bundle bundle = new Bundle();
        Intent intent = getIntent();
        bundle.putString(Constant.KEY_PLUGIN_ZIP_PATH, PluginHelper.getInstance().pluginZipFile.getAbsolutePath());
        bundle.putString(Constant.KEY_PLUGIN_PART_KEY, intent.getStringExtra(Constant.KEY_PLUGIN_PART_KEY));
        bundle.putString(Constant.KEY_ACTIVITY_CLASSNAME, intent.getStringExtra(Constant.KEY_ACTIVITY_CLASSNAME));
        bundle.putBundle(Constant.KEY_EXTRAS, intent.getBundleExtra(Constant.KEY_EXTRAS));

        int fromId = intent.getIntExtra(Constant.KEY_FROM_ID, Constant.FROM_ID_NOOP);

        final SimpleIdlingResource idlingResource = HostApplication.getApp().mIdlingResource;
        idlingResource.setIdleState(false);
        HostApplication.getApp().getPluginManager()
                .enter(this, fromId, bundle, new EnterCallback() {
                    @Override
                    public void onShowLoadingView(View view) {

                    }

                    @Override
                    public void onCloseLoadingView() {
                        idlingResource.setIdleState(true);
                    }

                    @Override
                    public void onEnterComplete() {

                    }
                });

    }
}
