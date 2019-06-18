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

package com.tencent.shadow.test.plugin.general_cases.lib.usecases.context;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;

import androidx.test.espresso.IdlingRegistry;

import com.tencent.shadow.test.plugin.general_cases.lib.usecases.SimpleIdlingResource;
import com.tencent.shadow.test.plugin.general_cases.lib.usecases.service.TestService;

public class ServiceContextSubDirTestActivity extends SubDirContextThemeWrapperTestActivity {

    final private SimpleIdlingResource mIdlingResource = new SimpleIdlingResource();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mIdlingResource.setIdleState(false);
        IdlingRegistry.getInstance().register(mIdlingResource);

        Intent intent = new Intent(this, TestService.class);
        bindService(intent, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                TestService.MyLocalServiceBinder binder = (TestService.MyLocalServiceBinder) service;
                TestService testService = binder.getMyLocalService();
                fillTestValues(testService);
                mIdlingResource.setIdleState(true);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        }, BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        IdlingRegistry.getInstance().unregister(mIdlingResource);
    }
}
