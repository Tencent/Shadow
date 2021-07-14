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

package com.tencent.shadow.test.plugin.general_cases.lib.gallery;

import android.app.Activity;
import android.app.Application;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.LinkedList;
import java.util.List;

public class TestApplication extends Application {

    private static TestApplication sInstence;

    public boolean isOnCreate;

    final private TestActivityLifecycleCallbacks alc = new TestActivityLifecycleCallbacks(
            "com.tencent.shadow.test.plugin.general_cases.lib.usecases.application.ActivityLifecycleCallbacksTestActivity");

    @Override
    public void onCreate() {
        sInstence = this;
        isOnCreate = true;
        super.onCreate();

        registerActivityLifecycleCallbacks(alc);

        //额外添加一个callback，构造通知遍历多个callback的场景
        registerActivityLifecycleCallbacks(new TestActivityLifecycleCallbacks("TestForRegisterInPreCreatedCallback"));
    }

    public static TestApplication getInstance() {
        return sInstence;
    }

    public List<String> getTestActivityLifecycleCallbacksRecord() {
        return alc.recordList;
    }
}

class TestActivityLifecycleCallbacks implements Application.ActivityLifecycleCallbacks {
    final private String targetActivityName;
    final List<String> recordList = new LinkedList<>();

    TestActivityLifecycleCallbacks(String targetActivityName) {
        this.targetActivityName = targetActivityName;
    }

    private boolean isTargetActivity(Activity activity) {
        return activity.getClass().getName().equals(targetActivityName);
    }

    @Override
    public void onActivityPreCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
        if (isTargetActivity(activity)) {
            recordList.add("onActivityPreCreated");
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            TestApplication.getInstance().registerActivityLifecycleCallbacks(
                    new TestActivityLifecycleCallbacks("TestForRegisterInPreCreatedCallback")
            );
        }
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
        if (isTargetActivity(activity)) {
            recordList.add("onActivityCreated");
        }
    }

    @Override
    public void onActivityPostCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
        if (isTargetActivity(activity)) {
            recordList.add("onActivityPostCreated");
        }
    }

    @Override
    public void onActivityPreStarted(@NonNull Activity activity) {
        if (isTargetActivity(activity)) {
            recordList.add("onActivityPreStarted");
        }
    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {
        if (isTargetActivity(activity)) {
            recordList.add("onActivityStarted");
        }
    }

    @Override
    public void onActivityPostStarted(@NonNull Activity activity) {
        if (isTargetActivity(activity)) {
            recordList.add("onActivityPostStarted");
        }
    }

    @Override
    public void onActivityPreResumed(@NonNull Activity activity) {
        if (isTargetActivity(activity)) {
            recordList.add("onActivityPreResumed");
        }
    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {
        if (isTargetActivity(activity)) {
            recordList.add("onActivityResumed");
        }
    }

    @Override
    public void onActivityPostResumed(@NonNull Activity activity) {
        if (isTargetActivity(activity)) {
            recordList.add("onActivityPostResumed");
        }
    }

    @Override
    public void onActivityPrePaused(@NonNull Activity activity) {
        if (isTargetActivity(activity)) {
            recordList.add("onActivityPrePaused");
        }
    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {
        if (isTargetActivity(activity)) {
            recordList.add("onActivityPaused");
        }
    }

    @Override
    public void onActivityPostPaused(@NonNull Activity activity) {
        if (isTargetActivity(activity)) {
            recordList.add("onActivityPostPaused");
        }
    }

    @Override
    public void onActivityPreStopped(@NonNull Activity activity) {
        if (isTargetActivity(activity)) {
            recordList.add("onActivityPreStopped");
        }
    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {
        if (isTargetActivity(activity)) {
            recordList.add("onActivityStopped");
        }
    }

    @Override
    public void onActivityPostStopped(@NonNull Activity activity) {
        if (isTargetActivity(activity)) {
            recordList.add("onActivityPostStopped");
        }
    }

    @Override
    public void onActivityPreSaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {
        if (isTargetActivity(activity)) {
            recordList.add("onActivityPreSaveInstanceState");
        }
    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {
        if (isTargetActivity(activity)) {
            recordList.add("onActivitySaveInstanceState");
        }
    }

    @Override
    public void onActivityPostSaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {
        if (isTargetActivity(activity)) {
            recordList.add("onActivityPostSaveInstanceState");
        }
    }

    @Override
    public void onActivityPreDestroyed(@NonNull Activity activity) {
        if (isTargetActivity(activity)) {
            recordList.add("onActivityPreDestroyed");
        }
    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {
        if (isTargetActivity(activity)) {
            recordList.add("onActivityDestroyed");
        }
    }

    @Override
    public void onActivityPostDestroyed(@NonNull Activity activity) {
        if (isTargetActivity(activity)) {
            recordList.add("onActivityPostDestroyed");
        }
    }
}