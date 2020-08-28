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

package com.tencent.shadow.core.runtime;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;

public class ShadowInstrumentation extends Instrumentation {

    public void callActivityOnDestroy(ShadowActivity activity) {
        Activity hostActivity = (Activity) activity.hostActivityDelegator.getHostActivity();
        super.callActivityOnDestroy(hostActivity);
    }

    static public ShadowApplication newShadowApplication(Class<?> clazz, Context context)
            throws InstantiationException, IllegalAccessException,
            ClassNotFoundException {
        ShadowApplication app = (ShadowApplication) clazz.newInstance();

        app.attachBaseContext(context);

        //这样构造的 ShadowApplication 跟 CreateApplicationBloc 正常构造的不一样。
        //这里构造的只是个Context而已，没有插件的各种信息。
        return app;
    }
}
