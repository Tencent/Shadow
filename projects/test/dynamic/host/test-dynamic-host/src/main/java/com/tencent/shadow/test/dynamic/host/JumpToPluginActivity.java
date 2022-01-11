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
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.tencent.shadow.dynamic.host.EnterCallback;
import com.tencent.shadow.test.lib.constant.Constant;
import com.tencent.shadow.test.lib.test_manager.SimpleIdlingResource;

public class JumpToPluginActivity extends Activity {

    final private Application.ActivityLifecycleCallbacks lifecycleCallbacks = new Application.ActivityLifecycleCallbacks() {
        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

        }

        @Override
        public void onActivityStarted(Activity activity) {

        }

        @Override
        public void onActivityResumed(Activity activity) {
            if (isPluginContainerActivity(activity)) {
                getApplication().unregisterActivityLifecycleCallbacks(lifecycleCallbacks);
                setIdlingResourceTrue();
            }
        }

        @Override
        public void onActivityPaused(Activity activity) {

        }

        @Override
        public void onActivityStopped(Activity activity) {

        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

        }

        @Override
        public void onActivityDestroyed(Activity activity) {

        }

        private boolean isPluginContainerActivity(Activity activity) {
            Class<?> superclass = activity.getClass().getSuperclass();

            final String superclassName;
            if (superclass != null) {
                superclassName = superclass.getName();
            } else {
                superclassName = "";
            }
            return "com.tencent.shadow.core.runtime.container.PluginContainerActivity".equals(superclassName);
        }

        private void setIdlingResourceTrue() {
            final SimpleIdlingResource idlingResource = HostApplication.getApp().mIdlingResource;
            idlingResource.setIdleState(true);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jump_to_plugin);

        getApplication().registerActivityLifecycleCallbacks(lifecycleCallbacks);
    }

    @Override
    protected void onDestroy() {
        getApplication().unregisterActivityLifecycleCallbacks(lifecycleCallbacks);
        super.onDestroy();
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
                    }

                    @Override
                    public void onEnterComplete() {

                    }
                });

    }
}
