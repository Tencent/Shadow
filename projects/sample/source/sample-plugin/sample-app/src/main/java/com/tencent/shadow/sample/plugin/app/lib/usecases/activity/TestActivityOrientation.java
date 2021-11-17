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

package com.tencent.shadow.sample.plugin.app.lib.usecases.activity;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.tencent.shadow.sample.plugin.app.lib.R;
import com.tencent.shadow.sample.plugin.app.lib.gallery.BaseActivity;
import com.tencent.shadow.sample.plugin.app.lib.gallery.cases.entity.UseCase;
import com.tencent.shadow.sample.plugin.app.lib.gallery.util.ToastUtil;


public class TestActivityOrientation extends BaseActivity {

    public static class Case extends UseCase {
        @Override
        public String getName() {
            return "横竖屏切换测试";
        }

        @Override
        public String getSummary() {
            return "测试横竖屏切换时，Activity的生命周期变化是否和AndroidManifest.xml中配置的config相关";
        }

        @Override
        public Class getPageClass() {
            return TestActivityOrientation.class;
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_orientation);
        ToastUtil.showToast(this,"onCreate");
    }


    public void setOrientation(View view){
       int orientation =  getRequestedOrientation();
       if(orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){
           setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
       }else {
           setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
       }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
}
