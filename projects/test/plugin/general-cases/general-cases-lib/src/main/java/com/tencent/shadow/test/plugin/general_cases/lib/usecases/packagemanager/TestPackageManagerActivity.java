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

package com.tencent.shadow.test.plugin.general_cases.lib.usecases.packagemanager;

import android.app.Activity;
import android.content.ComponentName;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.tencent.shadow.test.plugin.general_cases.lib.R;

public class TestPackageManagerActivity extends Activity {

    private TextView mTvTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_packagemanager);
        mTvTextView = findViewById(R.id.text);
    }


    public void getApplicationInfo(View view){
        try {
            ApplicationInfo applicationInfo = getPackageManager().getApplicationInfo(getPackageName(),0);
            mTvTextView.setText("ApplicationInfo className:"+applicationInfo.className+
                    "\nnativeLibraryDir:"+applicationInfo.nativeLibraryDir
            +"\nmetaData:"+applicationInfo.metaData);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }


    public void getActivityInfo(View view){
        try {
            ActivityInfo activityInfo = getPackageManager().getActivityInfo(new ComponentName(this,this.getClass()),0);
            mTvTextView.setText("activityInfo name:"+activityInfo.name
                    +"\npackageName:"+activityInfo.packageName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }


    public void getPackageInfo(View view){
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(),0);
            mTvTextView.setText("packageInfo versionName:"+packageInfo.versionName
                    +"\nversionCode:"+packageInfo.versionCode);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }
}
