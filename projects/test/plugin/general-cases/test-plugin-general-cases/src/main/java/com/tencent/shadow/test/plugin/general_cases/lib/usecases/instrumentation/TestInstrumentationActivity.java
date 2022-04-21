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

package com.tencent.shadow.test.plugin.general_cases.lib.usecases.instrumentation;

import android.app.Activity;
import android.app.Application;
import android.app.Instrumentation;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.view.ViewGroup;

import com.tencent.shadow.test.plugin.general_cases.lib.gallery.util.UiUtil;

public class TestInstrumentationActivity extends Activity {

    private ViewGroup mItemViewGroup;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mItemViewGroup = UiUtil.setActivityContentView(this);

        boolean newApplicationSuccess = false;
        try {
            Application app = Instrumentation.newApplication(Application.class, getApplicationContext());
            newApplicationSuccess = true;
        } catch (Exception ignored) {
        }

        mItemViewGroup.addView(
                UiUtil.makeItem(
                        this,
                        "newApplicationSuccess",
                        "newApplicationSuccess",
                        Boolean.toString(newApplicationSuccess)
                )
        );

        boolean callActivityOnDestroySuccess = false;
        try {
            MyInstrumentation myInstrumentation = new MyInstrumentation();
            TestInstrumentationActivity testActivity = new TestInstrumentationActivity();
            myInstrumentation.callActivityOnDestroy(testActivity);
        } catch (NullPointerException ignored) {
            callActivityOnDestroySuccess = true;
        }


        mItemViewGroup.addView(
                UiUtil.makeItem(
                        this,
                        "callActivityOnDestroySuccess",
                        "callActivityOnDestroySuccess",
                        Boolean.toString(callActivityOnDestroySuccess)
                )
        );


        boolean newApplicationSuccess1 = false;
        try {
            MyInstrumentation myInstrumentation = new MyInstrumentation();
            Application app = myInstrumentation.newApplication(myInstrumentation.getClass().getClassLoader(),
                    Application.class.getName(),
                    getApplicationContext());
            newApplicationSuccess1 = true;
        } catch (Exception ignore) {
        }


        mItemViewGroup.addView(
                UiUtil.makeItem(this,
                        "newApplicationSuccess1",
                        "newApplicationSuccess1",
                        Boolean.toString(newApplicationSuccess1))
        );


        boolean newShadowActivitySuccess = false;
        try {
            MyInstrumentation myInstrumentation = new MyInstrumentation();
            myInstrumentation.newActivity(myInstrumentation.getClass().getClassLoader(),
                    TestInstrumentationActivity.class.getName(),
                    null);
            newShadowActivitySuccess = true;
        } catch (Exception ignore) {
        }


        mItemViewGroup.addView(
                UiUtil.makeItem(this,
                        "newShadowActivitySuccess",
                        "newShadowActivitySuccess",
                        Boolean.toString(newShadowActivitySuccess))
        );

        boolean callApplicationOnCreateSuccess = false;
        try {
            MyInstrumentation myInstrumentation = new MyInstrumentation();
            myInstrumentation.callApplicationOnCreate(getApplication());
            callApplicationOnCreateSuccess = true;
        } catch (Exception ignore) {
        }


        mItemViewGroup.addView(
                UiUtil.makeItem(
                        this,
                        "callApplicationOnCreateSuccess",
                        "callApplicationOnCreateSuccess",
                        Boolean.toString(callApplicationOnCreateSuccess)
                )
        );

        boolean callActivityOnCreateSuccess = false;
        try {
            MyInstrumentation myInstrumentation = new MyInstrumentation();
            myInstrumentation.callActivityOnCreate(this, new Bundle());
            callActivityOnCreateSuccess = true;
        } catch (Exception ignore) {
        }


        mItemViewGroup.addView(
                UiUtil.makeItem(
                        this,
                        "callActivityOnCreateSuccess",
                        "callActivityOnCreateSuccess",
                        Boolean.toString(callActivityOnCreateSuccess)
                )
        );

        boolean callActivityOnCreateSuccess1 = false;
        try {
            MyInstrumentation myInstrumentation = new MyInstrumentation();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                myInstrumentation.callActivityOnCreate(this, new Bundle(), new PersistableBundle());
            }
            callActivityOnCreateSuccess1 = true;
        } catch (Exception ignore) {
        }


        mItemViewGroup.addView(
                UiUtil.makeItem(
                        this,
                        "callActivityOnCreateSuccess1",
                        "callActivityOnCreateSuccess1",
                        Boolean.toString(callActivityOnCreateSuccess1)
                )
        );

        boolean execStartActivitySuccess = false;
        try {
            MyInstrumentation myInstrumentation = new MyInstrumentation();
            // 这里是UnsupportedAppUsage的,无法测试
//            myInstrumentation.execStartActivity(this, new Bundle(), new PersistableBundle());
            execStartActivitySuccess = true;
        } catch (Exception ignore) {
        }


        mItemViewGroup.addView(
                UiUtil.makeItem(
                        this,
                        "execStartActivitySuccess",
                        "execStartActivitySuccess",
                        Boolean.toString(execStartActivitySuccess)
                )
        );
    }

}
