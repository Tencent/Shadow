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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.tencent.shadow.test.lib.constant.Constant;
import com.tencent.shadow.test.lib.custom_view.TestViewConstructorCacheView;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.TestHostTheme);

        LinearLayout rootView = new LinearLayout(this);
        rootView.setOrientation(LinearLayout.VERTICAL);

        TextView infoTextView = new TextView(this);
        infoTextView.setText(R.string.main_activity_info);
        rootView.addView(infoTextView);

        final Spinner partKeySpinner = new Spinner(this);
        ArrayAdapter<String> partKeysAdapter = new ArrayAdapter<>(this, R.layout.part_key_adapter);
        partKeysAdapter.addAll(
                Constant.PART_KEY_PLUGIN_MAIN_APP
        );
        partKeySpinner.setAdapter(partKeysAdapter);

        rootView.addView(partKeySpinner);

        Button startPluginButton = new Button(this);
        startPluginButton.setText(R.string.start_plugin);
        startPluginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String partKey = (String) partKeySpinner.getSelectedItem();
                Intent intent = new Intent(MainActivity.this, PluginLoadActivity.class);
                intent.putExtra(Constant.KEY_PLUGIN_PART_KEY, partKey);
                switch (partKey) {
                    case Constant.PART_KEY_PLUGIN_MAIN_APP:
                        intent.putExtra(Constant.KEY_ACTIVITY_CLASSNAME, "com.tencent.shadow.test.plugin.general_cases.lib.usecases.application.TestApplicationActivity");
                        break;
                }
                startActivity(intent);
            }
        });
        rootView.addView(startPluginButton);

        rootView.addView(new TestViewConstructorCacheView(this));

        setContentView(rootView);
    }

}
