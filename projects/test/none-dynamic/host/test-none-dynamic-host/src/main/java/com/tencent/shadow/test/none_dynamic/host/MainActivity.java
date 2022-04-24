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

package com.tencent.shadow.test.none_dynamic.host;

import static com.tencent.shadow.test.none_dynamic.host.HostApplication.PART_MAIN;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.TestHostTheme);
        setContentView(R.layout.activity_main);
    }

    public void startPlugin(View view) {
        final HostApplication application = (HostApplication) getApplication();
        application.loadPlugin(PART_MAIN, new Runnable() {
            @Override
            public void run() {
                Intent pluginIntent = new Intent();
                pluginIntent.setClassName(getPackageName(), "com.tencent.shadow.test.plugin.general_cases.lib.usecases.activity.TestListActivity");
                pluginIntent.putStringArrayListExtra("activities", TestComponentManager.sActivities);
                Intent intent = application.getPluginLoader().getMComponentManager().convertPluginActivityIntent(pluginIntent);
                startActivity(intent);
            }
        });

    }

}
