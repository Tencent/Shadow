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

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.tencent.shadow.sample.plugin.app.lib.R;
import com.tencent.shadow.sample.plugin.app.lib.gallery.cases.entity.UseCase;
import com.tencent.shadow.sample.plugin.app.lib.gallery.util.ToastUtil;

public class TestActivityReCreate extends Activity {


    public static class Case extends UseCase {
        @Override
        public String getName() {
            return "ReCreate";
        }

        @Override
        public String getSummary() {
            return "测试Activity的调用ReCreate是否工作正常";
        }

        @Override
        public Class getPageClass() {
            return TestActivityReCreate.class;
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_recreate);
        TextView textView = findViewById(R.id.tv_msg);
        boolean isRecreate = getIntent().getBooleanExtra("reCreate", false);
        textView.setText("isRecreate:"+isRecreate);
        ToastUtil.showToast(this, "onCreate");
    }

    public void reCreate(View view) {
        getIntent().putExtra("reCreate", true);
        recreate();
    }

    @Override
    protected void onStart() {
        super.onStart();
        ToastUtil.showToast(this, "onStart");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        ToastUtil.showToast(this, "onRestart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        ToastUtil.showToast(this, "onResume");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        ToastUtil.showToast(this, "onSaveInstanceState");
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        ToastUtil.showToast(this, "onRestoreInstanceState");
    }

    @Override
    protected void onStop() {
        super.onStop();
        ToastUtil.showToast(this, "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ToastUtil.showToast(this, "onDestroy");
    }
}
